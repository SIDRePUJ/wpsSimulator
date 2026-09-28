"""Prepare unvalidated IDEAM station-rainfall variants without selecting one gauge."""

import argparse
import csv
import hashlib
import io
import json
from datetime import date, datetime, time, timedelta
from decimal import Decimal, InvalidOperation
from pathlib import Path
from zipfile import ZipFile


ROOT = Path(__file__).resolve().parent
SOURCE = ROOT / "data/raw/ideam_precipitacion_nacional_diaria.zip"
OUTPUT = ROOT / "data/raw/ideam_derived"
MANIFEST = ROOT / "data/derived/ideam_rainfall_manifest.json"
SOURCE_SHA256 = "FA695160A154A7CE9C95DEE736535A5A8CD9BBCFAFE5A6AA3DACE13A9BD03E1B"
SOURCE_URL = "https://bart.ideam.gov.co/PQRS/AQTSUtils/PrecipitacionNacionalDiaria.zip"
STATIONS = {
    "29030080": "Puerto Santander",
    "29030160": "Flamenco",
    "29030780": "Mampujan",
    "29035040": "Nueva Florida",
}
YEARS = (2019, 2022)
SIMULATION_YEAR = 2022
MAPPINGS = ("label_date", "previous_day")


def sha256(content: bytes) -> str:
    return hashlib.sha256(content).hexdigest()


def load_station(archive: ZipFile, code: str) -> dict[date, Decimal]:
    member = f"PTPM_CON_INTER@{code}.data"
    rows = csv.DictReader(io.StringIO(archive.read(member).decode("utf-8-sig")), delimiter="|")
    if rows.fieldnames != ["Fecha", "Valor"]:
        raise ValueError(f"Unexpected IDEAM columns: {member}")
    daily = {}
    for row in rows:
        try:
            stamp = datetime.fromisoformat(row["Fecha"])
            value = Decimal(row["Valor"])
        except (TypeError, ValueError, InvalidOperation) as error:
            raise ValueError(f"Invalid IDEAM record: {member}") from error
        if stamp.time() != time(7) or stamp.tzinfo is not None:
            raise ValueError(f"Unexpected observation timestamp: {member} {stamp}")
        if not value.is_finite() or value < 0:
            raise ValueError(f"Invalid precipitation: {member} {stamp.date()}")
        if stamp.date() in daily:
            raise ValueError(f"Duplicate station date: {member} {stamp.date()}")
        daily[stamp.date()] = value
    return daily


def mapped_year(daily: dict[date, Decimal], year: int, mapping: str) -> list[tuple[date, Decimal]]:
    if mapping not in MAPPINGS or date(year, 12, 31).timetuple().tm_yday != 365:
        raise ValueError("Unsupported mapping or leap source year")
    offset = timedelta(days=int(mapping == "previous_day"))
    mapped = []
    for index in range(365):
        source_day = date(year, 1, 1) + timedelta(days=index) + offset
        if source_day not in daily:
            raise ValueError(f"Missing IDEAM date: {source_day}")
        mapped.append((date(SIMULATION_YEAR, 1, 1) + timedelta(days=index), daily[source_day]))
    return mapped


def csv_bytes(mapped: list[tuple[date, Decimal]]) -> bytes:
    body = "date,rain_mm\n" + "".join(
        f"{day:%d/%m/%Y},{format(rain, 'f')}\n" for day, rain in mapped
    )
    return body.encode("utf-8")


def season_total(mapped: list[tuple[date, Decimal]]) -> str:
    return format(sum((rain for day, rain in mapped
                       if (2, 1) <= (day.month, day.day) <= (8, 11)), Decimal(0)), "f")


def write_immutable(path: Path, content: bytes) -> None:
    if path.exists():
        if path.read_bytes() != content:
            raise FileExistsError(f"Existing evidence differs: {path}")
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("xb") as stream:
        stream.write(content)


def prepare(source: Path, output: Path, manifest: Path, expected_sha256: str = SOURCE_SHA256) -> dict:
    source_bytes = source.read_bytes()
    actual_sha256 = sha256(source_bytes)
    if actual_sha256.lower() != expected_sha256.lower():
        raise ValueError("IDEAM source SHA-256 differs from locked acquisition")
    records = {}
    proposed = {}
    with ZipFile(io.BytesIO(source_bytes)) as archive:
        if archive.testzip() is not None:
            raise ValueError("IDEAM ZIP failed integrity check")
        for code, station in STATIONS.items():
            daily = load_station(archive, code)
            for year in YEARS:
                for mapping in MAPPINGS:
                    mapped = mapped_year(daily, year, mapping)
                    filename = f"ideam_{code}_{year}_{mapping}_as_{SIMULATION_YEAR}.csv"
                    content = csv_bytes(mapped)
                    proposed[output / filename] = content
                    records[f"{code}/{year}/{mapping}"] = {
                        "station": station,
                        "station_code": code,
                        "source_year": year,
                        "date_mapping": mapping,
                        "output_file": filename,
                        "output_sha256": sha256(content),
                        "day_count": len(mapped),
                        "window_02_01_to_08_11_mm": season_total(mapped),
                    }
    metadata = {
        "status": "unvalidated_station_observations_not_municipal_rainfall",
        "source_file": source.name,
        "source_sha256": actual_sha256,
        "source_url": SOURCE_URL,
        "source_label": "PTPM_CON_INTER",
        "source_daily_unit": "mm",
        "simulation_year": SIMULATION_YEAR,
        "mapping_definitions": {
            "label_date": "source value stamped at 07:00 on D is assigned to D",
            "previous_day": "source value stamped at 07:00 on D+1 is assigned to D",
        },
        "limitations": "Date-label semantics and station quality are unverified. No daily values are committed. These inputs do not validate historical water delivery or allocation rules.",
        "scenarios": records,
    }
    manifest_bytes = (json.dumps(metadata, indent=2, sort_keys=True, ensure_ascii=False) + "\n").encode("utf-8")
    proposed[manifest] = manifest_bytes
    for path, content in proposed.items():
        if path.exists() and path.read_bytes() != content:
            raise FileExistsError(f"Existing evidence differs: {path}")
    for path, content in proposed.items():
        write_immutable(path, content)
    return metadata


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=SOURCE)
    parser.add_argument("--output", type=Path, default=OUTPUT)
    parser.add_argument("--manifest", type=Path, default=MANIFEST)
    args = parser.parse_args()
    metadata = prepare(args.source, args.output, args.manifest)
    print(f"Prepared {len(metadata['scenarios'])} ignored station inputs and an aggregate/hash manifest")


if __name__ == "__main__":
    main()
