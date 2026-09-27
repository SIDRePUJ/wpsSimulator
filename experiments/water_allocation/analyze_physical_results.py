"""Summarize paired physical-unit scenario outputs; never runs WellProdSim."""

import argparse
import csv
import json
import math
from collections import defaultdict
from pathlib import Path


REQUIRED = {
    "weather", "scarcity_ratio", "rule", "seed", "parameter_set",
    "plot_id", "area_ha", "full_t", "actual_t", "gross_m3",
}
BASELINE_RULE = "PROPORTIONAL_DEMAND"


def nonnegative(row, field):
    value = float(row[field])
    if not math.isfinite(value) or value < 0:
        raise ValueError(f"{field} must be finite and non-negative")
    return value


def percentile(values, probability):
    ordered = sorted(values)
    if not ordered:
        raise ValueError("cannot calculate percentile of an empty group")
    return ordered[min(len(ordered) - 1, math.ceil(probability * len(ordered)) - 1)]


def gini(values):
    """Unweighted Gini across UPA analogues; all-zero loss has Gini zero."""
    ordered = sorted(values)
    if not ordered or any(not math.isfinite(value) or value < 0 for value in ordered):
        raise ValueError("Gini requires finite non-negative observations")
    total = sum(ordered)
    if total == 0:
        return 0.0
    return 2 * sum(index * value for index, value in enumerate(ordered, 1)) / (
        len(ordered) * total) - (len(ordered) + 1) / len(ordered)


def read_results(path):
    groups = defaultdict(dict)
    with path.open(encoding="utf-8-sig", newline="") as stream:
        reader = csv.DictReader(stream)
        if not reader.fieldnames or not REQUIRED.issubset(reader.fieldnames):
            raise ValueError(f"missing required columns: {sorted(REQUIRED - set(reader.fieldnames or []))}")
        for row in reader:
            ratio = nonnegative(row, "scarcity_ratio")
            if ratio > 1:
                raise ValueError("scarcity_ratio cannot exceed one")
            area = nonnegative(row, "area_ha")
            full = nonnegative(row, "full_t")
            actual = nonnegative(row, "actual_t")
            gross = nonnegative(row, "gross_m3")
            if not row["plot_id"] or area <= 0 or full <= 0 or actual > full + 1e-9:
                raise ValueError("plot ID, area and full production must be positive; actual cannot exceed full")
            key = (row["weather"], ratio, row["rule"], row["seed"], row["parameter_set"])
            if row["plot_id"] in groups[key]:
                raise ValueError(f"duplicate plot in scenario {key}")
            groups[key][row["plot_id"]] = {
                "area_ha": area, "full_t": full, "actual_t": actual, "gross_m3": gross,
            }
    if not groups:
        raise ValueError("no results")
    return groups


def summarize(groups):
    records = []
    for key, plots in sorted(groups.items()):
        weather, scarcity, rule, seed, parameter_set = key
        total_full = sum(plot["full_t"] for plot in plots.values())
        total_actual = sum(plot["actual_t"] for plot in plots.values())
        losses = {name: max(0.0, 1 - plot["actual_t"] / plot["full_t"]) for name, plot in plots.items()}
        absolute_losses = [max(0.0, plot["full_t"] - plot["actual_t"]) for plot in plots.values()]
        smallest_count = max(1, math.ceil(len(plots) / 4))
        by_area = sorted(plots, key=lambda name: (plots[name]["area_ha"], name))
        smallest = by_area[:smallest_count]
        largest = by_area[-smallest_count:]
        smallest_mean_loss = sum(losses[name] for name in smallest) / smallest_count
        largest_mean_loss = sum(losses[name] for name in largest) / smallest_count
        records.append({
            "weather": weather, "scarcity_ratio": scarcity, "rule": rule,
            "seed": seed, "parameter_set": parameter_set, "plot_count": len(plots),
            "production_t": total_actual, "production_ratio": total_actual / total_full,
            "p90_relative_loss": percentile(list(losses.values()), 0.9),
            "smallest_quartile_p90_loss": percentile([losses[name] for name in smallest], 0.9),
            "gini_relative_loss": gini(list(losses.values())),
            "gini_absolute_loss_t": gini(absolute_losses),
            "smallest_to_largest_quartile_mean_relative_loss_ratio": (
                smallest_mean_loss / largest_mean_loss if largest_mean_loss > 0 else None),
            "gross_water_m3": sum(plot["gross_m3"] for plot in plots.values()),
        })
    return records


def contrasts(groups, summaries):
    indexed = {
        (r["weather"], r["scarcity_ratio"], r["seed"], r["parameter_set"], r["rule"]): r
        for r in summaries
    }
    differences = []
    for key, plots in groups.items():
        weather, scarcity, rule, seed, parameter_set = key
        if rule == BASELINE_RULE:
            continue
        pair = (weather, scarcity, seed, parameter_set)
        base = groups.get((weather, scarcity, BASELINE_RULE, seed, parameter_set))
        if base is None or set(base) != set(plots):
            raise ValueError(f"missing matched proportional baseline or plot IDs for {pair}")
        for name in plots:
            if abs(plots[name]["area_ha"] - base[name]["area_ha"]) > 1e-9 or abs(
                plots[name]["full_t"] - base[name]["full_t"]
            ) > 1e-9:
                raise ValueError(f"unmatched area or full-water reference for plot {name}")
        current = indexed[pair + (rule,)]
        reference = indexed[pair + (BASELINE_RULE,)]
        differences.append({
            "weather": weather, "scarcity_ratio": scarcity, "rule": rule,
            "seed": seed, "parameter_set": parameter_set,
            "delta_production_t": current["production_t"] - reference["production_t"],
            "delta_production_ratio": current["production_ratio"] - reference["production_ratio"],
            "delta_gini_relative_loss": current["gini_relative_loss"] - reference["gini_relative_loss"],
            "delta_gini_absolute_loss_t": current["gini_absolute_loss_t"] - reference["gini_absolute_loss_t"],
            "delta_smallest_quartile_p90_loss": (
                current["smallest_quartile_p90_loss"] - reference["smallest_quartile_p90_loss"]
            ),
        })
    return differences


def sensitivity_envelope(differences):
    by_parameter = defaultdict(list)
    for difference in differences:
        key = (difference["weather"], difference["scarcity_ratio"],
               difference["rule"], difference["parameter_set"])
        by_parameter[key].append(difference)
    by_scenario = defaultdict(list)
    for key, entries in by_parameter.items():
        weather, scarcity, rule, parameter_set = key
        by_scenario[(weather, scarcity, rule)].append({
            "parameter_set": parameter_set,
            "mean_delta_production_ratio": sum(
                item["delta_production_ratio"] for item in entries) / len(entries),
            "mean_delta_smallest_quartile_p90_loss": sum(
                item["delta_smallest_quartile_p90_loss"] for item in entries) / len(entries),
        })
    envelopes = []
    for key, values in sorted(by_scenario.items()):
        production = [item["mean_delta_production_ratio"] for item in values]
        equity = [item["mean_delta_smallest_quartile_p90_loss"] for item in values]
        envelopes.append({
            "weather": key[0], "scarcity_ratio": key[1], "rule": key[2],
            "parameter_sets": len(values),
            "delta_production_ratio_range": [min(production), max(production)],
            "delta_smallest_quartile_p90_loss_range": [min(equity), max(equity)],
            "share_parameter_sets_improving_smallest_quartile_loss": (
                sum(value < 0 for value in equity) / len(equity)),
        })
    return envelopes


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("results", type=Path, help="per-plot scenario CSV from the integrated simulator")
    parser.add_argument("--output", type=Path, help="JSON report path; otherwise print to stdout")
    args = parser.parse_args()
    groups = read_results(args.results)
    summaries = summarize(groups)
    differences = contrasts(groups, summaries)
    report = {"scenario_summaries": summaries, "paired_rule_contrasts": differences,
              "sensitivity_envelopes": sensitivity_envelope(differences)}
    rendered = json.dumps(report, indent=2, ensure_ascii=False)
    if args.output:
        args.output.write_text(rendered + "\n", encoding="utf-8")
    else:
        print(rendered)


if __name__ == "__main__":
    main()
