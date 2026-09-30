"""Read-only structural audit of four locked raw IDEAM station members.

The ZIP has no observation-quality flags. Passing does not certify station
meteorology, date-label interpretation, or spatial representativeness.
"""

import csv
import hashlib
import io
import json
import re
import sys
from datetime import date, datetime, timedelta
from decimal import Decimal, InvalidOperation
from pathlib import Path
from zipfile import BadZipFile, ZipFile


ROOT = Path(__file__).resolve().parent
SOURCE = ROOT / "data/raw/ideam_precipitacion_nacional_diaria.zip"
CATALOG = ROOT / "data/raw/ideam_station_catalog.csv"
SOURCE_SHA256 = "fa695160a154a7ce9c95dee736535a5a8cd9bbcfafe5a6aa3dace13a9bd03e1b"
CATALOG_SHA256 = "2ad7c7613ccc2596bd15989e8a141ff0766a5e65bd6e7eb3d2efbdacc488a7b5"
STATIONS = ("29030080", "29030160", "29030780", "29035040")
YEARS = (2019, 2022)
TIMESTAMP = re.compile(r"^\d{4}-\d{2}-\d{2} 07:00:00$")


def require(condition, message):
    if not condition:
        raise ValueError(message)


def sha256(path):
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def selected_catalog(path):
    stations = {}
    with path.open(encoding="utf-8-sig", newline="") as stream:
        reader = csv.DictReader(stream)
        required = {"Codigo", "Nombre", "Categoria", "Estado", "Municipio",
                    "Fecha_instalacion", "Fecha_suspension", "LATITUD", "LONGITUD"}
        require(required.issubset(reader.fieldnames or []), "catalog columns missing")
        for row in reader:
            code = row["Codigo"].lstrip("0")
            if code not in STATIONS:
                continue
            require(code not in stations, f"duplicate catalog station: {code}")
            require(row["Estado"] == "Activa" and row["Categoria"] == "Pluviométrica"
                    and row["Municipio"] == "María La Baja" and not row["Fecha_suspension"],
                    f"unexpected selected station metadata: {code}")
            installed = datetime.strptime(row["Fecha_instalacion"], "%d/%m/%Y").date()
            try:
                latitude = Decimal(row["LATITUD"])
                longitude = Decimal(row["LONGITUD"])
            except InvalidOperation as error:
                raise ValueError(f"invalid selected station coordinates: {code}") from error
            require(latitude.is_finite() and longitude.is_finite()
                    and 9 <= latitude <= 11 and -76 <= longitude <= -74,
                    f"invalid selected station coordinates: {code}")
            stations[code] = {"name": row["Nombre"], "installed": installed}
    require(set(stations) == set(STATIONS), "selected station catalog set incomplete")
    return stations


def station_series(archive, code):
    member = f"PTPM_CON_INTER@{code}.data"
    require(archive.namelist().count(member) == 1, f"missing or duplicate raw ZIP member: {member}")
    daily = {}
    with archive.open(member) as binary:
        reader = csv.DictReader(io.TextIOWrapper(binary, encoding="utf-8-sig", newline=""),
                                delimiter="|")
        require(reader.fieldnames == ["Fecha", "Valor"], f"raw member header differs: {member}")
        for line, row in enumerate(reader, 2):
            require(None not in row and None not in row.values(),
                    f"malformed raw row: {member}:{line}")
            stamp = row["Fecha"]
            require(TIMESTAMP.fullmatch(stamp) is not None,
                    f"timestamp is not 07:00:00: {member}:{line}")
            day = datetime.strptime(stamp, "%Y-%m-%d %H:%M:%S").date()
            try:
                rain = Decimal(row["Valor"])
            except InvalidOperation as error:
                raise ValueError(f"invalid rainfall value: {member}:{line}") from error
            require(rain.is_finite() and rain >= 0,
                    f"negative or non-finite rainfall: {member}:{line}")
            require(day not in daily, f"duplicate raw date: {member}:{day}")
            daily[day] = rain
    require(daily, f"empty raw member: {member}")
    return daily


def year_summary(daily, code, year):
    days = [date(year, 1, 1) + timedelta(days=index) for index in range(365)]
    require(all(day in daily for day in days), f"missing selected-year date: {code}/{year}")
    values = [daily[day] for day in days]
    positive = [value for value in values if value > 0]
    require(positive, f"selected year has no positive rainfall: {code}/{year}")
    fractional = sum(value != value.to_integral_value() for value in positive)
    finest_exponent = min(min(0, value.normalize().as_tuple().exponent) for value in positive)
    window = sum((daily[day] for day in days
                  if (2, 1) <= (day.month, day.day) <= (8, 11)), Decimal(0))
    return {"station_code": code, "year": year, "observed_days": len(values),
            "total_mm": str(sum(values, Decimal(0))),
            "window_02_01_to_08_11_mm": str(window),
            "wet_days": len(positive), "max_daily_mm": str(max(values)),
            "min_positive_mm": str(min(positive)),
            "fractional_positive_days": fractional,
            "finest_positive_decimal_place_mm": str(Decimal(1).scaleb(finest_exponent))}


def audit(source=SOURCE, catalog=CATALOG, source_sha256=SOURCE_SHA256,
          catalog_sha256=CATALOG_SHA256):
    require(source.is_file() and catalog.is_file(), "raw source ZIP or catalog missing")
    require(sha256(source) == source_sha256.lower(), "raw source ZIP SHA-256 differs")
    require(sha256(catalog) == catalog_sha256.lower(), "station catalog SHA-256 differs")
    metadata = selected_catalog(catalog)
    records = []
    flags = []
    with ZipFile(source) as archive:
        names = archive.namelist()
        for code in STATIONS:
            member = f"PTPM_CON_INTER@{code}.data"
            require(names.count(member) == 1, f"missing or duplicate raw ZIP member: {member}")
            daily = station_series(archive, code)
            first = min(daily)
            installed = metadata[code]["installed"]
            if first < installed:
                flags.append({"kind": "raw_series_precedes_catalog_installation",
                              "station_code": code, "raw_first_date": str(first),
                              "catalog_installation_date": str(installed)})
            for year in YEARS:
                adjacent = date(year + 1, 1, 1)
                require(adjacent in daily, f"missing date required for previous_day: {code}/{adjacent}")
                records.append(year_summary(daily, code, year))
    by_year = {year: [row for row in records if row["year"] == year] for year in YEARS}
    for year, rows in by_year.items():
        largest_wet = max(rows, key=lambda row: row["wet_days"])
        smallest_max = min(rows, key=lambda row: Decimal(row["max_daily_mm"]))
        if (largest_wet["station_code"] == smallest_max["station_code"]
                and sum(row["wet_days"] == largest_wet["wet_days"] for row in rows) == 1
                and sum(row["max_daily_mm"] == smallest_max["max_daily_mm"] for row in rows) == 1):
            flags.append({"kind": "most_wet_days_but_lowest_daily_maximum",
                          "station_code": largest_wet["station_code"], "year": year,
                          "wet_days": largest_wet["wet_days"],
                          "max_daily_mm": largest_wet["max_daily_mm"]})
        if any(row["fractional_positive_days"] == 0 for row in rows) and any(
                row["fractional_positive_days"] > 0 for row in rows):
            flags.append({"kind": "mixed_positive_value_granularity", "year": year,
                          "integer_only_station_codes": [row["station_code"] for row in rows
                                                         if row["fractional_positive_days"] == 0]})
    return {"status": "structural_pass_with_descriptive_flags",
            "raw_source_sha256": source_sha256.lower(),
            "catalog_sha256": catalog_sha256.lower(),
            "selected_members": len(STATIONS), "station_years": records,
            "review_flags": flags,
            "limitations": "No official station QC flags, verified ZIP date-label semantics, or spatial representativeness; flags do not exclude any locked scenario."}


def main():
    try:
        print(json.dumps(audit(), indent=2))
    except (OSError, ValueError, KeyError, TypeError, BadZipFile) as error:
        print(f"IDEAM source audit failed: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
