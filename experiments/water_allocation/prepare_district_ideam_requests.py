"""Freeze repeated district-calendar crop windows and prepare matched IDEAM requests."""

import argparse
import csv
import hashlib
import io
import json
import re
from collections import Counter
from datetime import datetime
from decimal import Decimal
from pathlib import Path

from prepare_ideam_requests import prepare_batch
from prepare_ideam_rainfall import ROOT, write_immutable
from prepare_multidate_requests import read_roster, read_windows, validate_study_cohort


RUNS = (ROOT / "reports/raw/calendar-c2-local-20260928-web-osredirect",
        ROOT / "reports/raw/calendar-c2-local-20260928-web-repeat")
ROSTER = ROOT / "data/derived/world24_plot_roster.csv"
WINDOWS = ROOT / "data/derived/world24_district_crop_windows.csv"
COHORT_MANIFEST = ROOT / "data/derived/district_crop_cohort_manifest.json"
REQUEST_DIR = ROOT / "data/raw/ideam_derived/district_requests"
REQUEST_MANIFEST = ROOT / "data/derived/ideam_district_request_manifest.json"
PLANT = re.compile(r"^WATER_PLANT: plot_id=(\S+) family_alias=(\S+) planting_date=(\d\d/\d\d/\d{4}) area_ha=(\S+)$", re.M)
WINDOW_HEADER = ("plot_id", "area_ha", "family_alias", "planting_date", "harvest_date")


def sha(content):
    return hashlib.sha256(content).hexdigest()


def run_windows(directory, roster):
    if (directory / "exit.txt").read_text(encoding="utf-8").strip() != "JAVA_EXIT=0":
        raise ValueError(f"nonzero or missing Java exit: {directory}")
    stdout = (directory / "stdout.txt").read_bytes()
    yield_bytes = (directory / "yield_audit.csv").read_bytes()
    plants = {}
    for match in PLANT.finditer(stdout.decode("utf-8").replace("\r\n", "\n")):
        plot, owner, day, area = match.groups()
        if plot in plants or plot not in roster or Decimal(area) != roster[plot]:
            raise ValueError(f"invalid plant roster: {plot}")
        plants[plot] = (owner, day)
    if len(plants) != 48:
        raise ValueError("expected 48 plant records")
    rows = list(csv.DictReader(io.StringIO(yield_bytes.decode("utf-8-sig"))))
    if len(rows) != 24 or len({row["plot_id"] for row in rows}) != 24:
        raise ValueError("expected 24 unique first-version harvests")
    windows = []
    for row in rows:
        plot = row["plot_id"]
        if (plot not in plants or row["status"] != "HARVESTED"
                or Decimal(row["area_ha"]) != roster[plot]
                or (row["family_alias"], row["planting_date"]) != plants[plot]):
            raise ValueError(f"harvest/plant mismatch: {plot}")
        planted = datetime.strptime(row["planting_date"], "%d/%m/%Y").date()
        harvested = datetime.strptime(row["harvest_date"], "%d/%m/%Y").date()
        if not (planted.year == harvested.year == 2022 and planted.month in (1, 2, 3)
                and harvested > planted):
            raise ValueError(f"invalid district crop window: {plot}")
        windows.append((plot, format(roster[plot], "f"), row["family_alias"],
                        row["planting_date"], row["harvest_date"]))
    stream = io.StringIO(newline="")
    writer = csv.writer(stream, lineterminator="\n")
    writer.writerow(WINDOW_HEADER)
    writer.writerows(sorted(windows))
    return stream.getvalue().encode("utf-8"), {
        "stdout_sha256": sha(stdout), "yield_audit_sha256": sha(yield_bytes),
        "canonical_plant_sha256": sha("\n".join(
            f"{plot},{roster[plot]},{plants[plot][0]},{plants[plot][1]}"
            for plot in sorted(plants)).encode("utf-8")),
        "plant_records": len(plants), "harvest_records": len(rows),
    }


def prepare_district(runs=RUNS, roster_path=ROSTER, windows_path=WINDOWS,
                     cohort_manifest=COHORT_MANIFEST, request_dir=REQUEST_DIR,
                     request_manifest=REQUEST_MANIFEST, **batch_options):
    roster = read_roster(roster_path)
    if len(runs) != 2 or runs[0] == runs[1]:
        raise ValueError("two distinct diagnostic runs required")
    first, first_provenance = run_windows(runs[0], roster)
    second, second_provenance = run_windows(runs[1], roster)
    if first != second or first_provenance["canonical_plant_sha256"] != second_provenance["canonical_plant_sha256"]:
        raise ValueError("repeated diagnostic crop windows differ")
    # Validate before writing either permanent artifact.
    from tempfile import TemporaryDirectory
    with TemporaryDirectory() as temporary:
        candidate = Path(temporary) / "windows.csv"
        candidate.write_bytes(first)
        windows = read_windows(candidate, roster)
        validate_study_cohort(roster, windows)
    if Counter(window[2] for window in windows.values()) != Counter({
            datetime(2022, 2, 1).date(): 12, datetime(2022, 2, 14).date(): 12}):
        raise ValueError("unexpected technical planting dates")
    record = {
        "status": "repeat_verified_synthetic_crop_windows_not_observed_planting_dates",
        "roster_sha256": sha(roster_path.read_bytes()), "windows_sha256": sha(first),
        "runs": {runs[0].name: first_provenance, runs[1].name: second_provenance},
        "eligible_plots": len(windows), "eligible_area_ha": format(sum(
            (value[0] for value in windows.values()), Decimal(0)), "f"),
        "limitations": "One synthetic seed and calendar mode; neither dates nor water supply are historical observations.",
    }
    manifest_bytes = (json.dumps(record, indent=2, sort_keys=True) + "\n").encode("utf-8")
    for path, content in ((windows_path, first), (cohort_manifest, manifest_bytes)):
        if path.exists() and path.read_bytes() != content:
            raise FileExistsError(f"existing cohort evidence differs: {path}")
    write_immutable(windows_path, first)
    write_immutable(cohort_manifest, manifest_bytes)
    audit = prepare_batch(roster_path=roster_path, windows_path=windows_path,
                          output_dir=request_dir, manifest_path=request_manifest,
                          **batch_options)
    if audit["windows_sha256"] != record["windows_sha256"]:
        raise ValueError("request audit used different windows")
    return record, audit


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--run-a", type=Path, default=RUNS[0])
    parser.add_argument("--run-b", type=Path, default=RUNS[1])
    args = parser.parse_args()
    cohort, audit = prepare_district(runs=(args.run_a, args.run_b))
    print(f"Frozen {cohort['eligible_plots']} crop windows; prepared {len(audit['scenarios'])} request/stock scenarios")


if __name__ == "__main__":
    main()
