"""Read-only identity and structural preflight for the locked IDEAM screen.

Passing this check does not validate station meteorology or authorize model runs.
"""

import csv
import hashlib
import json
import sys
from collections import defaultdict
from datetime import date, datetime, timedelta
from decimal import Decimal
from pathlib import Path

from prepare_multidate_requests import (decimal, read_rain, read_roster,
                                        read_rows, read_windows, validate_study_cohort)


ROOT = Path(__file__).resolve().parent
STATIONS = {"29030080": "Puerto Santander", "29030160": "Flamenco",
            "29030780": "Mampujan", "29035040": "Nueva Florida"}
YEARS = (2019, 2022)
MAPPINGS = ("label_date", "previous_day")
RATIOS = ("0.35", "0.65", "1.00")
LOCKED = {
    "data/raw/ideam_precipitacion_nacional_diaria.zip": "fa695160a154a7ce9c95dee736535a5a8cd9bbcfafe5a6aa3dace13a9bd03e1b",
    "data/raw/ideam_station_catalog.csv": "2ad7c7613ccc2596bd15989e8a141ff0766a5e65bd6e7eb3d2efbdacc488a7b5",
    "data/derived/ideam_rainfall_manifest.json": "fa937f9486d9f8bb9ee1ab944b8f205449863ced71769006a55840454a9194d6",
    "data/derived/ideam_district_request_manifest.json": "dfd0766a42a12ad0954d44c617408ba764db4dc273b4f0cbd20eb9071065db80",
    "data/derived/district_crop_cohort_manifest.json": "d9143e69fa6a002e7a698c0043a38219e77248219b8f60011cb377c1d580e058",
    "data/derived/world24_district_crop_windows.csv": "28c15b179ca5cb8bfd8de1195a3f065bd30dbb4b0e78638608dc4a1c5538f4f7",
    "data/derived/world24_plot_roster.csv": "c85255d95a7ea639debe91470fb4ac4990de26019992377fbf8b6e526a8d14ce",
    "twelve_upa_manifest.csv": "eaf2d0fd34e72c18d3def0e2a89f8cc44f86ec7e4ba789d187cca884ad24ab3c",
    "../../src/main/resources/web/data/world.24.json": "244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c",
}


def sha256(path):
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def require(condition, message):
    if not condition:
        raise ValueError(message)


def check_hash(path, expected):
    require(path.is_file(), f"missing locked input: {path}")
    require(sha256(path).lower() == expected.lower(), f"SHA-256 differs: {path}")


def check_stations(path):
    seen = set()
    with path.open(encoding="utf-8-sig", newline="") as stream:
        for row in csv.DictReader(stream):
            code = row["Codigo"].lstrip("0")
            if code not in STATIONS:
                continue
            require(code not in seen, f"duplicate station catalog code: {code}")
            seen.add(code)
            require(row["Estado"] == "Activa" and row["Categoria"] == "Pluviométrica"
                    and row["Municipio"] == "María La Baja",
                    f"station catalog metadata differs: {code}")
            latitude = decimal(row["LATITUD"], "station latitude")
            longitude = decimal(row["LONGITUD"], "station longitude")
            require(9 <= latitude <= 11 and -76 <= longitude <= -74,
                    f"station catalog coordinates differ: {code}")
    require(seen == set(STATIONS), "station catalog does not contain exactly four locked stations")


def check_requests(path, roster, windows, rain):
    records = read_rows(path, ("date", "plot_id", "area_ha", "net_demand_mm",
                             "delivery_efficiency"))
    require(len(records) == 408, f"request row count differs: {path}")
    seen = set()
    by_plot = defaultdict(set)
    positive = set()
    gross = Decimal(0)
    for row in records:
        day = datetime.strptime(row["date"], "%d/%m/%Y").date()
        plot = row["plot_id"]
        require(plot in roster and (day, plot) not in seen,
                f"missing/duplicate/unknown request plot: {plot}")
        seen.add((day, plot))
        area = decimal(row["area_ha"], "request area")
        depth = decimal(row["net_demand_mm"], "request depth")
        efficiency = decimal(row["delivery_efficiency"], "delivery efficiency")
        require(area == roster[plot] and depth >= 0 and efficiency == Decimal("0.48"),
                f"invalid request area, depth or efficiency: {plot}")
        if plot in windows:
            _, _, planted, harvested = windows[plot]
            require(planted + timedelta(days=7) <= day < harvested - timedelta(days=7)
                    and (day - planted).days % 7 == 0,
                    f"request outside frozen crop window: {plot} {day}")
            previous = sum((rain[day - timedelta(days=offset)] for offset in range(1, 8)), Decimal(0))
            require(depth == max(Decimal(0), Decimal(30) - Decimal("0.8") * previous),
                    f"rain-conditioned request depth differs: {plot} {day}")
            by_plot[plot].add(day)
            if depth > 0:
                positive.add(plot)
        else:
            require(day == date(2022, 5, 1) and depth == 0,
                    f"invalid zero-demand registration: {plot}")
            by_plot[plot].add(day)
        gross += Decimal(10) * area * depth / efficiency
    require(set(by_plot) == set(roster), "request roster is incomplete")
    for plot, days in by_plot.items():
        if plot in windows:
            planted, harvested = windows[plot][2:]
            expected = set()
            day = planted + timedelta(days=7)
            while day < harvested - timedelta(days=7):
                expected.add(day)
                day += timedelta(days=7)
            require(days == expected and len(days) == 16,
                    f"missing/extra weekly requests: {plot}")
        else:
            require(days == {date(2022, 5, 1)}, f"registration row differs: {plot}")
    require(positive == set(windows), "positive-demand eligible plot cohort differs")
    return gross.quantize(Decimal("0.000001"))


def check(root=ROOT, locked=LOCKED):
    for relative, expected in locked.items():
        check_hash(root / relative, expected)
    check_stations(root / "data/raw/ideam_station_catalog.csv")
    rain_manifest = json.loads((root / "data/derived/ideam_rainfall_manifest.json").read_text(encoding="utf-8"))
    request_manifest = json.loads((root / "data/derived/ideam_district_request_manifest.json").read_text(encoding="utf-8"))
    cohort = json.loads((root / "data/derived/district_crop_cohort_manifest.json").read_text(encoding="utf-8"))
    keys = {f"{code}/{year}/{mapping}" for code in STATIONS for year in YEARS for mapping in MAPPINGS}
    source_hash = locked["data/raw/ideam_precipitacion_nacional_diaria.zip"]
    require(set(rain_manifest.get("scenarios", {})) == keys
            and set(request_manifest.get("scenarios", {})) == keys,
            "IDEAM scenario key set differs from locked 16 variants")
    require(rain_manifest.get("source_sha256", "").lower() == source_hash
            and request_manifest.get("ideam_source_sha256", "").lower() == source_hash
            and rain_manifest.get("source_file") == "ideam_precipitacion_nacional_diaria.zip"
            and rain_manifest.get("source_daily_unit") == "mm"
            and rain_manifest.get("source_label") == "PTPM_CON_INTER"
            and rain_manifest.get("simulation_year") == 2022,
            "IDEAM source metadata differs")
    require(request_manifest.get("rainfall_manifest_sha256", "").lower()
            == locked["data/derived/ideam_rainfall_manifest.json"]
            and request_manifest.get("roster_sha256", "").lower()
            == locked["data/derived/world24_plot_roster.csv"]
            and request_manifest.get("windows_sha256", "").lower()
            == locked["data/derived/world24_district_crop_windows.csv"]
            and cohort.get("windows_sha256", "").lower()
            == locked["data/derived/world24_district_crop_windows.csv"]
            and request_manifest.get("parameters") == {
                "target_mm": "30", "rain_factor": "0.8", "delivery_efficiency": "0.48"},
            "request/cohort manifest identity or parameters differ")
    roster = read_roster(root / "data/derived/world24_plot_roster.csv")
    windows = read_windows(root / "data/derived/world24_district_crop_windows.csv", roster)
    validate_study_cohort(roster, windows)
    require(sum((values[0] for values in windows.values()), Decimal(0)) == 96,
            "eligible area differs from 96 ha")
    summaries = []
    for key in sorted(keys):
        code, year, mapping = key.split("/")
        rain_record = rain_manifest["scenarios"][key]
        request_record = request_manifest["scenarios"][key]
        rain_name = f"ideam_{code}_{year}_{mapping}_as_2022.csv"
        request_name = f"ideam_requests_{code}_{year}_{mapping}.csv"
        require((rain_record.get("station_code"), rain_record.get("source_year"),
                 rain_record.get("date_mapping"), rain_record.get("station"),
                 rain_record.get("output_file"), rain_record.get("day_count"))
                == (code, int(year), mapping, STATIONS[code], rain_name, 365),
                f"rainfall scenario metadata differs: {key}")
        require((request_record.get("station_code"), request_record.get("source_year"),
                 request_record.get("date_mapping"), request_record.get("request_file"),
                 request_record.get("request_rows"), request_record.get("rain_sha256"))
                == (code, int(year), mapping, request_name, 408,
                    rain_record.get("output_sha256")),
                f"request scenario metadata differs: {key}")
        rain_path = root / "data/raw/ideam_derived" / rain_name
        request_path = root / "data/raw/ideam_derived/district_requests" / request_name
        check_hash(rain_path, rain_record["output_sha256"])
        check_hash(request_path, request_record["request_sha256"])
        rain = read_rain(rain_path)
        season = sum((value for day, value in rain.items()
                      if (2, 1) <= (day.month, day.day) <= (8, 11)), Decimal(0))
        require(season == decimal(rain_record["window_02_01_to_08_11_mm"], "season rain"),
                f"rainfall seasonal total differs: {key}")
        gross = check_requests(request_path, roster, windows, rain)
        require(gross == decimal(request_record["unconstrained_gross_m3"], "gross demand"),
                f"gross demand differs: {key}")
        stocks = request_record.get("source_m3_by_ratio", {})
        require(set(stocks) == set(RATIOS), f"stock ratio set differs: {key}")
        for ratio in RATIOS:
            expected = (gross * Decimal(ratio)).quantize(Decimal("0.000001"))
            require(decimal(stocks[ratio], "source stock") == expected,
                    f"synthetic stock arithmetic differs: {key}/{ratio}")
        values = list(rain.values())
        summaries.append({"scenario": key, "max_daily_mm": str(max(values)),
                          "zero_days": sum(value == 0 for value in values),
                          "days_ge_100_mm": sum(value >= 100 for value in values),
                          "gross_demand_m3": str(gross)})
    return {"status": "structural_identity_pass", "scenario_count": len(summaries),
            "station_quality_certified": False,
            "limitations": "Station meteorology, ZIP date-label semantics and spatial representativeness remain unverified; no simulation is authorized.",
            "scenarios": summaries}


def main():
    try:
        print(json.dumps(check(), indent=2))
    except (OSError, ValueError, KeyError, TypeError, json.JSONDecodeError) as error:
        print(f"IDEAM readiness failed: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
