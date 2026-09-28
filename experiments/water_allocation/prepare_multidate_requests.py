"""Build fixed weekly root-zone supplement scenarios from audited crop windows and rainfall."""

import argparse
import csv
from collections import Counter, defaultdict
from datetime import date, datetime, timedelta
from decimal import Decimal, InvalidOperation
import hashlib
import json
from pathlib import Path


DATE = "%d/%m/%Y"
HEADER = ("date", "plot_id", "area_ha", "net_demand_mm", "delivery_efficiency")
RATIOS = (Decimal("0.35"), Decimal("0.65"), Decimal("1.00"))


def decimal(value, label):
    try:
        result = Decimal(str(value))
    except InvalidOperation as error:
        raise ValueError(f"invalid {label}: {value}") from error
    if not result.is_finite():
        raise ValueError(f"non-finite {label}")
    return result


def read_rows(path, expected):
    with path.open(newline="", encoding="utf-8-sig") as stream:
        reader = csv.DictReader(stream)
        if tuple(reader.fieldnames or ()) != expected:
            raise ValueError(f"unexpected header in {path}")
        return list(reader)


def read_rain(path):
    rows = read_rows(path, ("date", "rain_mm"))
    rain = {}
    for row in rows:
        day = datetime.strptime(row["date"], DATE).date()
        value = decimal(row["rain_mm"], "rain_mm")
        if day in rain or value < 0:
            raise ValueError("duplicate date or negative rainfall")
        rain[day] = value
    expected = {date(2022, 1, 1) + timedelta(days=index) for index in range(365)}
    if set(rain) != expected:
        raise ValueError("rainfall fixture must contain every day of 2022 exactly once")
    return rain


def read_roster(path):
    roster = {}
    for row in read_rows(path, ("plot_id", "area_ha")):
        plot = row["plot_id"]
        area = decimal(row["area_ha"], "area_ha")
        if not plot or plot in roster or area <= 0:
            raise ValueError("invalid or duplicate registered plot")
        roster[plot] = area
    return roster


def read_windows(path, roster):
    expected = ("plot_id", "area_ha", "family_alias", "planting_date", "harvest_date")
    harvest = {}
    for row in read_rows(path, expected):
        plot = row["plot_id"]
        if plot in harvest or plot not in roster:
            raise ValueError("crop-window/registered-plot cohort mismatch")
        area = decimal(row["area_ha"], "harvest area")
        if area != roster[plot] or not row["family_alias"]:
            raise ValueError("harvest owner or area mismatch")
        planted = datetime.strptime(row["planting_date"], DATE).date()
        harvested = datetime.strptime(row["harvest_date"], DATE).date()
        if not (date(2022, 1, 1) <= planted < harvested <= date(2022, 12, 31)):
            raise ValueError("invalid crop window")
        harvest[plot] = (area, row["family_alias"], planted, harvested)
    if not harvest:
        raise ValueError("crop-window cohort is empty")
    return harvest


def validate_study_cohort(roster, harvest):
    if len(roster) != 48 or len(harvest) != 24:
        raise ValueError("expected 48 registered and 24 eligible study plots")
    family_areas = defaultdict(Decimal)
    for area, family, _, _ in harvest.values():
        family_areas[family] += area
    if Counter(family_areas.values()) != {Decimal(2): 4, Decimal(8): 5, Decimal(16): 3}:
        raise ValueError("eligible UPA area classes differ from the scenario register")


def prepare(roster, harvest, rain, target, rain_factor, efficiency):
    if target <= 0 or rain_factor < 0 or not 0 < efficiency <= 1:
        raise ValueError("invalid scenario parameters")
    scheduled = []
    positive_by_plot = {plot: 0 for plot in harvest}
    for plot, area in roster.items():
        if plot not in harvest:
            scheduled.append((date(2022, 5, 1), plot, area, Decimal(0), efficiency))
            continue
        _, _, planted, harvested = harvest[plot]
        day = planted + timedelta(days=7)
        # The farmer's harvest event can lag crop maturity: leave one request interval clear.
        while day < harvested - timedelta(days=7):
            prior = sum((rain[day - timedelta(days=offset)] for offset in range(1, 8)), Decimal(0))
            depth = max(Decimal(0), target - rain_factor * prior)
            scheduled.append((day, plot, area, depth, efficiency))
            positive_by_plot[plot] += int(depth > 0)
            day += timedelta(days=7)
    if any(count == 0 for count in positive_by_plot.values()):
        raise ValueError("rainfall scenario leaves a harvested plot without positive demand")
    scheduled.sort(key=lambda row: (row[0], row[1]))
    gross = sum((Decimal(10) * area * depth / efficiency
                 for _, _, area, depth, efficiency in scheduled), Decimal(0))
    gross = gross.quantize(Decimal("0.000001"))
    if gross <= 0:
        raise ValueError("zero unconstrained seasonal demand")
    return scheduled, gross


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def write_schedule(path, rows):
    if path.exists():
        raise FileExistsError(path)
    with path.open("x", newline="", encoding="utf-8") as stream:
        writer = csv.writer(stream, lineterminator="\n")
        writer.writerow(HEADER)
        for day, plot, area, depth, efficiency in rows:
            writer.writerow((day.strftime(DATE), plot, format(area, "f"),
                             format(depth, "f"), format(efficiency, "f")))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--roster", type=Path, required=True)
    parser.add_argument("--windows", type=Path, required=True)
    parser.add_argument("--rain", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--target-mm", type=Decimal, default=Decimal("30"))
    parser.add_argument("--rain-factor", type=Decimal, default=Decimal("0.8"))
    parser.add_argument("--efficiency", type=Decimal, default=Decimal("0.48"))
    args = parser.parse_args()
    if args.output.exists() or args.output.with_suffix(".budget.json").exists():
        raise FileExistsError("refusing to overwrite requests or budget")
    roster = read_roster(args.roster)
    harvest = read_windows(args.windows, roster)
    validate_study_cohort(roster, harvest)
    rain = read_rain(args.rain)
    rows, gross = prepare(roster, harvest, rain, args.target_mm, args.rain_factor,
                          args.efficiency)
    write_schedule(args.output, rows)
    budget = {"status": "synthetic_root_zone_scenario_not_observed_water_supply",
              "request_sha256": digest(args.output),
              "roster_sha256": digest(args.roster), "windows_sha256": digest(args.windows),
              "rain_sha256": digest(args.rain), "eligible_plots": len(harvest),
              "registration_plots": len(roster) - len(harvest), "request_rows": len(rows),
              "target_mm": str(args.target_mm), "rain_factor": str(args.rain_factor),
              "efficiency": str(args.efficiency), "unconstrained_gross_m3": str(gross),
              "source_m3_by_ratio": {str(ratio): format((gross * ratio).quantize(
                  Decimal("0.000001")), "f") for ratio in RATIOS}}
    args.output.with_suffix(".budget.json").write_text(
        json.dumps(budget, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print(f"Prepared {len(rows)} dated requests for {len(harvest)} eligible plots")


if __name__ == "__main__":
    main()
