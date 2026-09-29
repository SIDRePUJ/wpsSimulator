"""Aggregate validated per-plot scenario rows to UPA before inequality analysis.

The input is a joined, eligible-plot table, not the raw simulator audit. Its
creation and the run-status/audit checks remain a separate mandatory gate.
"""

import argparse
import csv
import json
import math
from collections import defaultdict
from pathlib import Path

from analyze_physical_results import (
    BASELINE_RULE, REQUIRED, contrasts, read_results, sensitivity_envelope, summarize,
)


def read_owners(path):
    owners = defaultdict(dict)
    with path.open(encoding="utf-8-sig", newline="") as stream:
        reader = csv.DictReader(stream)
        if not reader.fieldnames or not (REQUIRED | {"family_alias"}).issubset(reader.fieldnames):
            raise ValueError("joined plot table must include family_alias and all physical-result columns")
        for row in reader:
            family = row["family_alias"].strip()
            if not family:
                raise ValueError("empty family_alias")
            key = (row["weather"], float(row["scarcity_ratio"]), row["rule"],
                   row["seed"], row["parameter_set"])
            plot_id = row["plot_id"]
            if plot_id in owners[key]:
                raise ValueError(f"duplicate plot owner in scenario {key}: {plot_id}")
            owners[key][plot_id] = family
    return owners


def aggregate(path):
    plots = read_results(path)
    owners = read_owners(path)
    if set(plots) != set(owners):
        raise ValueError("scenario/owner groups do not match")

    upas = {}
    membership = {}
    for key, scenario_plots in plots.items():
        if set(scenario_plots) != set(owners[key]):
            raise ValueError(f"plot/owner cohort does not match in scenario {key}")
        weather, scarcity, rule, seed, parameter_set = key
        if rule != BASELINE_RULE:
            baseline_key = (weather, scarcity, BASELINE_RULE, seed, parameter_set)
            baseline = plots.get(baseline_key)
            if baseline is None or set(baseline) != set(scenario_plots):
                raise ValueError(f"missing matched proportional baseline or plot IDs for {key}")
            for plot_id, values in scenario_plots.items():
                reference = baseline[plot_id]
                if owners[key][plot_id] != owners[baseline_key][plot_id]:
                    raise ValueError(f"unmatched owner for plot {plot_id}")
                if abs(values["area_ha"] - reference["area_ha"]) > 1e-9 or abs(
                    values["full_t"] - reference["full_t"]
                ) > 1e-9:
                    raise ValueError(f"unmatched area or full-water reference for plot {plot_id}")

        by_family = defaultdict(lambda: {"area_ha": 0.0, "full_t": 0.0,
                                         "actual_t": 0.0, "gross_m3": 0.0})
        family_plots = defaultdict(list)
        for plot_id, values in scenario_plots.items():
            family = owners[key][plot_id]
            for column in ("area_ha", "full_t", "actual_t", "gross_m3"):
                by_family[family][column] += values[column]
            family_plots[family].append(plot_id)
        upas[key] = dict(by_family)
        membership[key] = {family: sorted(ids) for family, ids in family_plots.items()}

    return upas, membership


def report(path):
    upas, membership = aggregate(path)
    summaries = summarize(upas)
    for row in summaries:
        row["upa_count"] = row.pop("plot_count")
        key = (row["weather"], row["scarcity_ratio"], row["rule"],
               row["seed"], row["parameter_set"])
        families = upas[key]
        minimum_area = min(values["area_ha"] for values in families.values())
        smallest = [values for values in families.values()
                    if math.isclose(values["area_ha"], minimum_area, rel_tol=0, abs_tol=1e-9)]
        losses = [max(0.0, 1 - values["actual_t"] / values["full_t"]) for values in smallest]
        row["smallest_area_upa_count"] = len(smallest)
        row["smallest_area_upa_mean_relative_loss"] = sum(losses) / len(losses)
        row["smallest_area_upa_max_relative_loss"] = max(losses)
    differences = contrasts(upas, summaries)
    summary_by_key = {
        (row["weather"], row["scarcity_ratio"], row["seed"], row["parameter_set"], row["rule"]): row
        for row in summaries
    }
    for difference in differences:
        pair = (difference["weather"], difference["scarcity_ratio"],
                difference["seed"], difference["parameter_set"])
        current = summary_by_key[pair + (difference["rule"],)]
        baseline = summary_by_key[pair + (BASELINE_RULE,)]
        for metric in ("mean", "max"):
            field = f"smallest_area_upa_{metric}_relative_loss"
            difference[f"delta_{field}"] = current[field] - baseline[field]
    detail = []
    for key, families in sorted(upas.items()):
        weather, scarcity, rule, seed, parameter_set = key
        for family, values in sorted(families.items()):
            detail.append({
                "weather": weather, "scarcity_ratio": scarcity, "rule": rule,
                "seed": seed, "parameter_set": parameter_set,
                "family_alias": family, "eligible_plot_ids": membership[key][family],
                **values, "relative_loss": 1 - values["actual_t"] / values["full_t"],
            })
    return {"unit_of_inequality": "UPA", "eligible_plot_only": True,
            "upa_rows": detail, "scenario_summaries": summaries,
            "paired_rule_contrasts": differences,
            "sensitivity_envelopes": sensitivity_envelope(differences)}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("results", type=Path, help="joined eligible-plot scenario CSV")
    parser.add_argument("--output", type=Path, help="JSON report path; otherwise print to stdout")
    args = parser.parse_args()
    rendered = json.dumps(report(args.results), indent=2, ensure_ascii=False)
    if args.output:
        args.output.write_text(rendered + "\n", encoding="utf-8")
    else:
        print(rendered)


if __name__ == "__main__":
    main()
