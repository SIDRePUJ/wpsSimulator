"""Prepare ignored weekly requests and an aggregate audit for IDEAM variants."""

import argparse
import hashlib
import json
import tempfile
from decimal import Decimal
from pathlib import Path

from prepare_ideam_rainfall import (MANIFEST as RAIN_MANIFEST, MAPPINGS, OUTPUT as RAIN_DIR,
                                    ROOT, SOURCE_SHA256, STATIONS, YEARS, write_immutable)
from prepare_multidate_requests import (RATIOS, prepare, read_rain, read_roster,
                                        read_windows, validate_study_cohort, write_schedule)


ROSTER = ROOT / "data/derived/world24_plot_roster.csv"
WINDOWS = ROOT / "data/derived/world24_crop_windows.csv"
OUTPUT_DIR = RAIN_DIR / "requests"
MANIFEST = ROOT / "data/derived/ideam_request_manifest.json"
TARGET_MM = Decimal("30")
RAIN_FACTOR = Decimal("0.8")
EFFICIENCY = Decimal("0.48")


def digest(content: bytes) -> str:
    return hashlib.sha256(content).hexdigest()


def prepare_batch(rain_manifest: Path = RAIN_MANIFEST, rain_dir: Path = RAIN_DIR,
                  roster_path: Path = ROSTER, windows_path: Path = WINDOWS,
                  output_dir: Path = OUTPUT_DIR, manifest_path: Path = MANIFEST) -> dict:
    rainfall_bytes = rain_manifest.read_bytes()
    rainfall = json.loads(rainfall_bytes)
    expected = {f"{code}/{year}/{mapping}" for code in STATIONS for year in YEARS
                for mapping in MAPPINGS}
    if rainfall.get("source_sha256", "").lower() != SOURCE_SHA256.lower() or set(rainfall.get("scenarios", {})) != expected:
        raise ValueError("IDEAM rainfall manifest has unexpected source or scenario set")

    roster_bytes = roster_path.read_bytes()
    windows_bytes = windows_path.read_bytes()
    roster = read_roster(roster_path)
    windows = read_windows(windows_path, roster)
    validate_study_cohort(roster, windows)

    prepared = {}
    records = {}
    with tempfile.TemporaryDirectory() as temporary:
        for key in sorted(expected):
            source = rainfall["scenarios"][key]
            code, year, mapping = key.split("/")
            if (source.get("station_code"), source.get("source_year"), source.get("date_mapping")) != (code, int(year), mapping):
                raise ValueError(f"IDEAM rainfall identity differs: {key}")
            rain_path = rain_dir / f"ideam_{code}_{year}_{mapping}_as_2022.csv"
            if source.get("output_file") != rain_path.name:
                raise ValueError(f"IDEAM rainfall filename differs: {key}")
            rain_bytes = rain_path.read_bytes()
            if digest(rain_bytes) != source.get("output_sha256"):
                raise ValueError(f"IDEAM rainfall SHA-256 differs: {key}")
            rows, gross = prepare(roster, windows, read_rain(rain_path),
                                  TARGET_MM, RAIN_FACTOR, EFFICIENCY)
            filename = f"ideam_requests_{code}_{year}_{mapping}.csv"
            temporary_path = Path(temporary) / filename
            write_schedule(temporary_path, rows)
            request_bytes = temporary_path.read_bytes()
            prepared[output_dir / filename] = request_bytes
            records[key] = {
                "station_code": code,
                "source_year": int(year),
                "date_mapping": mapping,
                "rain_sha256": source["output_sha256"],
                "request_file": filename,
                "request_sha256": digest(request_bytes),
                "request_rows": len(rows),
                "unconstrained_gross_m3": format(gross, "f"),
                "source_m3_by_ratio": {
                    str(ratio): format((gross * ratio).quantize(Decimal("0.000001")), "f")
                    for ratio in RATIOS
                },
            }

    audit = {
        "status": "synthetic_requests_and_scenario_stocks_not_observed_deliveries",
        "rainfall_manifest_sha256": digest(rainfall_bytes),
        "ideam_source_sha256": SOURCE_SHA256,
        "roster_sha256": digest(roster_bytes),
        "windows_sha256": digest(windows_bytes),
        "parameters": {"target_mm": str(TARGET_MM), "rain_factor": str(RAIN_FACTOR),
                       "delivery_efficiency": str(EFFICIENCY)},
        "limitations": "Station quality, ZIP date-label semantics and spatial representativeness remain unresolved. Stocks are fractions of synthetic gross demand, not measured source volumes. No allocation rules were run. Request CSVs remain ignored because they contain station-derived daily information.",
        "scenarios": records,
    }
    manifest_bytes = (json.dumps(audit, indent=2, sort_keys=True) + "\n").encode("utf-8")
    prepared[manifest_path] = manifest_bytes
    for path, content in prepared.items():
        if path.exists() and path.read_bytes() != content:
            raise FileExistsError(f"Existing evidence differs: {path}")
    for path, content in prepared.items():
        write_immutable(path, content)
    return audit


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--rain-manifest", type=Path, default=RAIN_MANIFEST)
    parser.add_argument("--rain-dir", type=Path, default=RAIN_DIR)
    parser.add_argument("--roster", type=Path, default=ROSTER)
    parser.add_argument("--windows", type=Path, default=WINDOWS)
    parser.add_argument("--output-dir", type=Path, default=OUTPUT_DIR)
    parser.add_argument("--manifest", type=Path, default=MANIFEST)
    args = parser.parse_args()
    audit = prepare_batch(args.rain_manifest, args.rain_dir, args.roster, args.windows,
                          args.output_dir, args.manifest)
    print(f"Prepared {len(audit['scenarios'])} ignored request schedules and an aggregate/hash manifest")


if __name__ == "__main__":
    main()
