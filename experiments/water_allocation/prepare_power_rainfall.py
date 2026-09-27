"""Prepare preselected POWER precipitation proxy years for one simulation calendar."""

import argparse
import csv
import hashlib
import io
import json
from datetime import date, timedelta
from pathlib import Path


ROOT = Path(__file__).resolve().parent
SOURCE = ROOT / "data/raw/nasa_power_maria_la_baja_2013_2024_daily.csv"
OUTPUT = ROOT / "data/derived"
SCENARIOS = {"dry_proxy": 2014, "near_median_proxy": 2019}
SIMULATION_YEAR = 2022
WINDOW_START = (2, 1)
WINDOW_END = (8, 11)


def load_power(source: Path) -> dict[int, dict[date, float]]:
    raw = source.read_bytes()
    lines = raw.decode("utf-8-sig").splitlines()
    if "-END HEADER-" not in lines:
        raise ValueError("POWER CSV header terminator is absent")
    rows = csv.DictReader(io.StringIO("\n".join(lines[lines.index("-END HEADER-") + 1 :])))
    if rows.fieldnames is None or not {"YEAR", "DOY", "PRECTOTCORR"}.issubset(rows.fieldnames):
        raise ValueError("POWER CSV lacks the required precipitation fields")
    years: dict[int, dict[date, float]] = {}
    for row in rows:
        year = int(row["YEAR"])
        day_of_year = int(row["DOY"])
        day = date(year, 1, 1) + timedelta(days=day_of_year - 1)
        if day.year != year or day.timetuple().tm_yday != day_of_year:
            raise ValueError(f"Invalid day of year: {year}/{day_of_year}")
        rain = float(row["PRECTOTCORR"])
        if not 0 <= rain < 1e6:
            raise ValueError(f"Missing or invalid precipitation: {day}")
        if day in years.setdefault(year, {}):
            raise ValueError(f"Duplicate POWER date: {day}")
        years[year][day] = rain
    for year, days in years.items():
        expected = 366 if date(year, 12, 31).timetuple().tm_yday == 366 else 365
        if len(days) != expected:
            raise ValueError(f"Incomplete POWER year: {year} ({len(days)}/{expected})")
    return years


def season_total(days: dict[date, float]) -> float:
    return sum(rain for day, rain in days.items()
               if WINDOW_START <= (day.month, day.day) <= WINDOW_END)


def write_immutable(path: Path, content: str) -> None:
    encoded = content.encode("utf-8")
    if path.exists():
        if path.read_bytes() != encoded:
            raise FileExistsError(f"Existing evidence differs: {path}")
        return
    path.write_bytes(encoded)


def prepare(source: Path, output: Path) -> dict:
    years = load_power(source)
    output.mkdir(parents=True, exist_ok=True)
    ranking = sorted((year, round(season_total(days), 2)) for year, days in years.items())
    ranking.sort(key=lambda item: (item[1], item[0]))
    metadata = {
        "source": source.name,
        "source_sha256": hashlib.sha256(source.read_bytes()).hexdigest(),
        "source_parameter": "PRECTOTCORR",
        "source_unit": "mm/day",
        "source_type": "NASA POWER gridded MERRA-2 precipitation proxy; not IDEAM station observation",
        "simulation_year": SIMULATION_YEAR,
        "selection_window_month_day": ["02-01", "08-11"],
        "ranking_ascending_by_window_mm": [
            {"year": year, "window_mm": total} for year, total in ranking
        ],
        "scenarios": {},
        "limits": "Rainfall only; other meteorological drivers remain the simulator's seeded monthly generator. Water-source volume is an independent scenario parameter.",
    }
    for label, year in SCENARIOS.items():
        days = years[year]
        if len(days) != 365:
            raise ValueError(f"Selected year must be nonleap for date remapping: {year}")
        name = f"power_rainfall_{year}_as_{SIMULATION_YEAR}.csv"
        body = "date,rain_mm\n" + "".join(
            f"{day.day:02d}/{day.month:02d}/{SIMULATION_YEAR},{days[day]}\n"
            for day in sorted(days)
        )
        write_immutable(output / name, body)
        metadata["scenarios"][label] = {
            "source_year": year,
            "output_file": name,
            "output_sha256": hashlib.sha256(body.encode("utf-8")).hexdigest(),
            "window_mm": round(season_total(days), 2),
            "day_count": len(days),
            "mapping": "same month/day mapped to the common 2022 simulation calendar",
        }
    write_immutable(output / "power_rainfall_scenarios.json",
                    json.dumps(metadata, indent=2, sort_keys=True) + "\n")
    return metadata


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=SOURCE)
    parser.add_argument("--output", type=Path, default=OUTPUT)
    arguments = parser.parse_args()
    metadata = prepare(arguments.source, arguments.output)
    for name, scenario in metadata["scenarios"].items():
        print(f"{name}: {scenario['source_year']} {scenario['window_mm']} mm "
              f"{scenario['output_sha256']}")


if __name__ == "__main__":
    main()
