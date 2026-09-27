"""Admit physical-water runs and join eligible harvests to same-run water audits.

Input is a JSON manifest with a `runs` array. Each run names `directory`,
`requests_csv`, `farm_csv`, `rain_csv`, `weather`, `scarcity_ratio`, `rule`,
`seed`, `parameter_set`, and `source_m3`. Paths resolve from the manifest.
Only a fully audited, paired set can produce an output CSV.
"""

import argparse
import csv
import hashlib
import json
import math
import re
import tempfile
from datetime import datetime, timedelta
from pathlib import Path

from analyze_upa_results import report as upa_report


FIELDS = ["weather", "scarcity_ratio", "rule", "seed", "parameter_set",
          "plot_id", "family_alias", "area_ha", "full_t", "actual_t", "gross_m3"]
AUDIT_FIELDS = {
    "FARM": ("plannedFamilies", "assignedFamilies", "failedFamilies"),
    "WATER": ("plannedPlots", "registeredPlots", "absentPlots",
              "failedPlotRegistrations", "missingDeliveries", "appliedDeliveries"),
    "YIELD": ("plannedPlots", "harvestedPlots", "missingHarvests"),
    "CLIMATE": ("plannedPlots", "observedPlots", "dailyRows",
                "missingPlots", "gapDays", "duplicateDays"),
}


def rows(path, fields):
    with path.open(encoding="utf-8-sig", newline="") as stream:
        reader = csv.DictReader(stream)
        if not reader.fieldnames or not set(fields).issubset(reader.fieldnames):
            raise ValueError(f"missing columns in {path}: {set(fields) - set(reader.fieldnames or [])}")
        if len(reader.fieldnames) != len(set(reader.fieldnames)):
            raise ValueError(f"duplicate CSV header in {path}")
        result = list(reader)
    if any(None in row for row in result):
        raise ValueError(f"malformed CSV row in {path}")
    if not result:
        raise ValueError(f"empty CSV: {path}")
    return result


def number(value, name, *, positive=False):
    try:
        parsed = float(value)
    except (TypeError, ValueError) as error:
        raise ValueError(f"invalid {name}: {value}") from error
    if not math.isfinite(parsed) or (parsed <= 0 if positive else parsed < 0):
        raise ValueError(f"{name} must be finite and {'positive' if positive else 'non-negative'}")
    return parsed


def near(actual, expected, name):
    if not math.isclose(actual, expected, rel_tol=1e-9, abs_tol=1e-7):
        raise ValueError(f"{name} mismatch: {actual} versus {expected}")


def date(value):
    try:
        return datetime.strptime(value, "%d/%m/%Y").date()
    except ValueError as error:
        raise ValueError(f"invalid date: {value}") from error


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def audited_counts(directory):
    if (directory / "exit.txt").read_text(encoding="utf-8").strip() != "0":
        raise ValueError(f"run did not exit zero: {directory}")
    log = (directory / "stdout.txt").read_text(encoding="utf-8", errors="replace")
    counts = {}
    for name, fields in AUDIT_FIELDS.items():
        lines = [line for line in log.splitlines() if line.startswith(f"PHYSICAL_{name}_AUDIT:")]
        if len(lines) != 1:
            raise ValueError(f"expected one {name} audit marker in {directory}")
        values = {}
        for field in fields:
            match = re.search(rf"\b{field}=(\d+)\b", lines[0])
            if not match:
                raise ValueError(f"missing {field} in {name} audit")
            values[field] = int(match.group(1))
        counts[name] = values
    return counts


def index_requests(path):
    result = {}
    for row in rows(path, ("date", "plot_id", "area_ha", "net_demand_mm", "delivery_efficiency")):
        key = (date(row["date"]), row["plot_id"])
        if not key[1] or key in result:
            raise ValueError(f"blank or duplicate irrigation request: {key}")
        area = number(row["area_ha"], "request area", positive=True)
        depth = number(row["net_demand_mm"], "net demand")
        efficiency = number(row["delivery_efficiency"], "delivery efficiency", positive=True)
        if efficiency > 1:
            raise ValueError("delivery efficiency exceeds one")
        result[key] = (area, depth, efficiency)
    return result


def index_rain(path):
    result = {}
    for row in rows(path, ("date", "rain_mm")):
        day = date(row["date"])
        if day in result:
            raise ValueError(f"duplicate rainfall date: {day}")
        result[day] = number(row["rain_mm"], "rainfall")
    return result


def read_families(path):
    families = set()
    for row in rows(path, ("family_alias", "farm_name")):
        family = row["family_alias"].strip()
        if not family or family in families:
            raise ValueError("blank or duplicate family in farm manifest")
        families.add(family)
    return families


def admit_run(spec, root):
    required = ("directory", "requests_csv", "farm_csv", "rain_csv", "weather",
                "scarcity_ratio", "rule", "seed", "parameter_set", "source_m3")
    if any(field not in spec for field in required):
        raise ValueError(f"run manifest missing fields: {[f for f in required if f not in spec]}")
    if not all(str(spec[field]).strip() for field in ("weather", "rule", "seed", "parameter_set")):
        raise ValueError("empty scenario identifier")
    directory = (root / spec["directory"]).resolve()
    request_path = (root / spec["requests_csv"]).resolve()
    farm_path = (root / spec["farm_csv"]).resolve()
    rain_path = (root / spec["rain_csv"]).resolve()
    counts = audited_counts(directory)
    horizon = spec.get("horizon")
    if horizon is not None:
        if horizon not in ("ROUND_CHRONOLOGICAL", "SEASONAL_ENTITLEMENT"):
            raise ValueError(f"invalid allocation horizon: {horizon}")
        log = (directory / "stdout.txt").read_text(encoding="utf-8", errors="replace")
        markers = [line for line in log.splitlines() if line.startswith("PHYSICAL_WATER_HORIZON:")]
        if markers != [f"PHYSICAL_WATER_HORIZON: {horizon}"]:
            raise ValueError("declared allocation horizon differs from run marker")
    requests = index_requests(request_path)
    families = read_families(farm_path)
    rain = index_rain(rain_path)
    source = number(spec["source_m3"], "source_m3")
    demand = sum(area * 10 * depth / efficiency for area, depth, efficiency in requests.values())
    if demand <= 0:
        raise ValueError("scenario has no positive irrigation demand")
    scarcity = number(spec["scarcity_ratio"], "scarcity_ratio")
    near(scarcity, min(1, source / demand), "scarcity ratio")
    eligible = {plot for (_, plot), (_, depth, _) in requests.items() if depth > 0}
    if counts["FARM"] != {"plannedFamilies": len(families), "assignedFamilies": len(families),
                           "failedFamilies": 0}:
        raise ValueError("farm audit failed or does not match manifest")
    if counts["WATER"]["plannedPlots"] != len({plot for _, plot in requests}):
        raise ValueError("water planned-plot count differs from request cohort")
    if any(counts["WATER"][field] != 0 for field in
           ("absentPlots", "failedPlotRegistrations", "missingDeliveries")) or (
           counts["WATER"]["registeredPlots"] != counts["WATER"]["plannedPlots"]):
        raise ValueError("water audit failed")
    for name, left, right, missing in (("YIELD", "plannedPlots", "harvestedPlots", "missingHarvests"),
                                       ("CLIMATE", "plannedPlots", "observedPlots", "missingPlots")):
        if counts[name][left] != len(eligible) or counts[name][right] != len(eligible) or counts[name][missing]:
            raise ValueError(f"{name.lower()} audit failed or eligible cohort differs")
    if counts["CLIMATE"]["gapDays"] or counts["CLIMATE"]["duplicateDays"]:
        raise ValueError("climate audit has gaps or duplicate days")

    water = {}
    for row in rows(directory / "audit.csv", ("date", "plot_id", "area_ha", "gross_m3",
                                                    "net_mm", "applied_net_mm", "status")):
        key = (date(row["date"]), row["plot_id"])
        if key in water or key not in requests:
            raise ValueError(f"duplicate or unrequested water row: {key}")
        area, depth, efficiency = requests[key]
        near(number(row["area_ha"], "water area", positive=True), area, "water area")
        gross = number(row["gross_m3"], "gross withdrawal")
        net = number(row["net_mm"], "net delivery")
        near(net, gross * efficiency / (area * 10), "net/gross conversion")
        if net > depth + 1e-7:
            raise ValueError("net delivery exceeds demand")
        if gross > 0:
            if row["status"] != "APPLIED":
                raise ValueError("positive water was not applied")
            near(number(row["applied_net_mm"], "applied depth"), net, "applied depth")
        elif row["status"] != "NO_DELIVERY" or row["applied_net_mm"].strip():
            raise ValueError("zero-water row has inconsistent status/depth")
        water[key] = gross
    if set(water) != set(requests):
        raise ValueError("water/request rows do not match")
    if counts["WATER"]["appliedDeliveries"] != sum(gross > 0 for gross in water.values()):
        raise ValueError("applied-delivery audit count mismatch")
    near(sum(water.values()), min(source, demand), "shared-source withdrawal")

    harvest = {}
    for row in rows(directory / "yield.csv", ("plot_id", "area_ha", "family_alias",
                                                    "planting_date", "harvest_date", "actual_t_ha",
                                                    "actual_t", "full_t", "status")):
        plot = row["plot_id"]
        if plot in harvest or plot not in eligible or row["status"] != "HARVESTED":
            raise ValueError(f"duplicate, ineligible or unharvested plot: {plot}")
        family = row["family_alias"].strip()
        if family not in families:
            raise ValueError(f"unknown family owner: {family}")
        area = number(row["area_ha"], "harvest area", positive=True)
        full = number(row["full_t"], "full production", positive=True)
        actual = number(row["actual_t"], "actual production")
        if actual > full + 1e-7:
            raise ValueError("actual production exceeds full reference")
        near(actual, area * number(row["actual_t_ha"], "yield t/ha"), "tonne conversion")
        start, end = date(row["planting_date"]), date(row["harvest_date"])
        if start >= end:
            raise ValueError("invalid planting/harvest interval")
        deliveries = [(day, values) for (day, name), values in requests.items() if name == plot]
        for day, (requested_area, depth, _) in deliveries:
            near(area, requested_area, "harvest/request area")
            if depth > 0 and not start < day < end:
                raise ValueError(f"positive request outside crop interval: {plot}")
        harvest[plot] = (family, area, full, actual, start, end)
    if set(harvest) != eligible or counts["YIELD"]["harvestedPlots"] != len(harvest):
        raise ValueError("yield/positive-demand cohort does not match")

    climate = {}
    for row in rows(directory / "climate.csv", ("plot_id", "date", "rain_mm", "reference_et_mm",
                                                      "temperature_c", "short_wave_radiation")):
        plot, day = row["plot_id"], date(row["date"])
        if plot not in eligible or (plot, day) in climate:
            raise ValueError(f"unexpected or duplicate climate row: {plot} {day}")
        if day not in rain:
            raise ValueError(f"rainfall fixture missing crop day: {day}")
        near(number(row["rain_mm"], "consumed rainfall"), rain[day], "rainfall forcing")
        number(row["reference_et_mm"], "reference ET")
        number(row["short_wave_radiation"], "radiation")
        temperature = float(row["temperature_c"])
        if not math.isfinite(temperature):
            raise ValueError("non-finite temperature")
        climate[(plot, day)] = True
    expected_climate = {(plot, day) for plot, (_, _, _, _, start, end) in harvest.items()
                        for day in (start + timedelta(days=offset)
                                    for offset in range((end - start).days + 1))}
    if set(climate) != expected_climate or counts["CLIMATE"]["dailyRows"] != len(climate):
        raise ValueError("climate/crop daily cohort mismatch")

    metadata = {"weather": spec["weather"], "scarcity_ratio": scarcity,
                "rule": spec["rule"], "seed": str(spec["seed"]),
                "parameter_set": spec["parameter_set"]}
    joined = []
    for plot, (family, area, full, actual, _, _) in sorted(harvest.items()):
        joined.append({**metadata, "plot_id": plot, "family_alias": family,
                       "area_ha": area, "full_t": full, "actual_t": actual,
                       "gross_m3": sum(gross for (day, name), gross in water.items() if name == plot)})
    pair = (metadata["weather"], scarcity, metadata["seed"], metadata["parameter_set"])
    fingerprints = (digest(request_path), digest(farm_path), digest(rain_path),
                    digest(directory / "climate.csv"), source, horizon)
    return joined, pair, fingerprints


def build(manifest, output):
    document = json.loads(manifest.read_text(encoding="utf-8"))
    if not isinstance(document, dict) or not isinstance(document.get("runs"), list) or not document["runs"]:
        raise ValueError("manifest must contain a nonempty runs array")
    if output.exists():
        raise ValueError(f"refusing to overwrite existing output: {output}")
    all_rows = []
    paired = {}
    scenarios = set()
    for spec in document["runs"]:
        joined, pair, fingerprints = admit_run(spec, manifest.parent)
        scenario = pair + (spec["rule"],)
        if scenario in scenarios:
            raise ValueError(f"duplicate scenario: {scenario}")
        scenarios.add(scenario)
        if pair in paired and paired[pair] != fingerprints:
            raise ValueError(f"paired runs differ in requests, farms, rainfall, climate or source: {pair}")
        paired[pair] = fingerprints
        all_rows.extend(joined)
    output.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.NamedTemporaryFile("w", encoding="utf-8", newline="", suffix=".csv",
                                     prefix="joined-", dir=output.parent, delete=False) as stream:
        temporary = Path(stream.name)
        writer = csv.DictWriter(stream, fieldnames=FIELDS)
        writer.writeheader()
        writer.writerows(all_rows)
    try:
        upa_report(temporary)  # Reject unpaired plot/owner/area/full-reference cohorts.
        temporary.replace(output)
    finally:
        temporary.unlink(missing_ok=True)
    return len(all_rows)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest", type=Path)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    print(f"Admitted {build(args.manifest, args.output)} eligible plot rows: {args.output}")


if __name__ == "__main__":
    main()
