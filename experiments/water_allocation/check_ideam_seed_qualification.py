"""Read-only admission of two future district-calendar seed diagnostics.

This checks captured evidence, not the simulator itself or policy outcomes. A
capture.json is a launcher-produced record, not independent process attestation.
"""

import argparse
import csv
from collections import Counter, defaultdict
from datetime import datetime, timedelta
from decimal import Decimal, InvalidOperation
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import sys


ROOT = Path(__file__).resolve().parent
SEEDS = (271828, 314159)
DIAGNOSTIC_REQUEST_SHA256 = "7349e3ca1c2b591de3cc3afdd751a7c73d08450dddce8296b3d10b546b9eeff4"
FROZEN = {
    "data/derived/ideam_district_request_manifest.json": "dfd0766a42a12ad0954d44c617408ba764db4dc273b4f0cbd20eb9071065db80",
    "data/derived/ideam_rainfall_manifest.json": "fa937f9486d9f8bb9ee1ab944b8f205449863ced71769006a55840454a9194d6",
    "data/derived/district_crop_cohort_manifest.json": "d9143e69fa6a002e7a698c0043a38219e77248219b8f60011cb377c1d580e058",
    "data/derived/world24_district_crop_windows.csv": "28c15b179ca5cb8bfd8de1195a3f065bd30dbb4b0e78638608dc4a1c5538f4f7",
    "data/derived/world24_plot_roster.csv": "c85255d95a7ea639debe91470fb4ac4990de26019992377fbf8b6e526a8d14ce",
    "twelve_upa_manifest.csv": "eaf2d0fd34e72c18d3def0e2a89f8cc44f86ec7e4ba789d187cca884ad24ab3c",
    "../../src/main/resources/web/data/world.24.json": "244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c",
}
OUTPUTS = ("command.txt", "exit.txt", "stdout.txt", "stderr.txt", "water_audit.csv",
           "yield_audit.csv", "climate_audit.csv")
PLOT = re.compile(r"^WATER_PLOT: plot_id=(\S+) crop=(\S+) area_ha=(\S+)$", re.M)
PLANT = re.compile(r"^WATER_PLANT: plot_id=(\S+) family_alias=(\S+) planting_date=(\S+) area_ha=(\S+)$", re.M)
MARKERS = {
    "PHYSICAL_WATER_AUDIT": {"plannedPlots": 48, "registeredPlots": 48,
                             "absentPlots": 0, "failedPlotRegistrations": 0,
                             "missingDeliveries": 0, "appliedDeliveries": 24},
    "PHYSICAL_FARM_AUDIT": {"plannedFamilies": 12, "assignedFamilies": 12,
                            "failedFamilies": 0},
    "PHYSICAL_YIELD_AUDIT": {"plannedPlots": 24, "harvestedPlots": 24,
                             "missingHarvests": 0},
    "PHYSICAL_CLIMATE_AUDIT": {"plannedPlots": 24, "observedPlots": 24,
                               "dailyRows": 2904, "missingPlots": 0,
                               "gapDays": 0, "duplicateDays": 0},
}


def require(condition, message):
    if not condition:
        raise ValueError(message)


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def rows(path, header):
    with path.open(encoding="utf-8-sig", newline="") as stream:
        reader = csv.DictReader(stream)
        require(tuple(reader.fieldnames or ()) == header, f"invalid header: {path.name}")
        return list(reader)


def number(value):
    try:
        result = Decimal(value)
    except (InvalidOperation, TypeError) as error:
        raise ValueError(f"invalid number: {value}") from error
    require(result.is_finite(), f"non-finite number: {value}")
    return result


def day(value):
    return datetime.strptime(value, "%d/%m/%Y").date()


def frozen_cohort(root, expected_hashes=FROZEN):
    for name, expected in expected_hashes.items():
        require(sha(root / name) == expected, f"frozen hash mismatch: {name}")
    roster = {row["plot_id"]: number(row["area_ha"]) for row in rows(
        root / "data/derived/world24_plot_roster.csv", ("plot_id", "area_ha"))}
    require(len(roster) == 48 and all(area > 0 for area in roster.values()), "invalid roster")
    windows = {}
    for row in rows(root / "data/derived/world24_district_crop_windows.csv",
                    ("plot_id", "area_ha", "family_alias", "planting_date", "harvest_date")):
        plot = row["plot_id"]
        require(plot not in windows and plot in roster, "duplicate/unknown window plot")
        area = number(row["area_ha"])
        planted, harvested = day(row["planting_date"]), day(row["harvest_date"])
        require(area == roster[plot] and planted.year == harvested.year == 2022
                and planted.month in (1, 2, 3) and planted < harvested,
                f"invalid frozen window: {plot}")
        windows[plot] = (area, row["family_alias"], planted, harvested)
    require(len(windows) == 24 and sum((w[0] for w in windows.values()), Decimal(0)) == 96,
            "wrong eligible plot count/area")
    by_owner = defaultdict(Decimal)
    for area, owner, _, _ in windows.values():
        by_owner[owner] += area
    require(len(by_owner) == 12 and Counter(by_owner.values()) ==
            {Decimal(2): 4, Decimal(8): 5, Decimal(16): 3}, "wrong UPA classes")
    return roster, windows


def option(argv, name):
    positions = [i for i, value in enumerate(argv) if value == name]
    require(len(positions) == 1 and positions[0] + 1 < len(argv), f"missing/repeated {name}")
    return argv[positions[0] + 1]


def property_value(argv, name):
    values = [arg.split("=", 1)[1] for arg in argv if arg.startswith(f"-D{name}=")]
    require(len(values) == 1, f"missing/repeated {name}")
    return values[0]


def captured_path(value, posix=False):
    """Compare captured paths using their origin host, not the review host."""
    if not posix:
        return Path(value).resolve()
    require(isinstance(value, str) and "\\" not in value
            and not value.startswith("//"),
            "invalid captured POSIX path")
    path = PurePosixPath(value)
    require(path.is_absolute() and str(path) == value and ".." not in path.parts,
            "invalid captured POSIX path")
    return path


def check_argv(argv, directory, root, seed, captured_posix=False):
    require(isinstance(argv, list) and all(isinstance(arg, str) for arg in argv), "invalid argv")
    require(option(argv, "-seed") == str(seed), "seed differs from argv")
    for name, value in {"-env": "local", "-mode": "web", "-agents": "12",
                        "-world": "24", "-land": "2", "-years": "1",
                        "-startyear": "2022", "-perturbation": "none"}.items():
        require(option(argv, name) == value, f"wrong {name}")
    for name in ("districtRiceCalendar", "riceOnlyCohort", "discoverPlots"):
        require(property_value(argv, f"wps.water.{name}") == "true", f"wrong {name}")
    require(property_value(argv, "wps.water.rule") == "PROPORTIONAL_DEMAND",
            "diagnostic rule changed")
    require(property_value(argv, "wps.water.sourceM3") == "19200",
            "diagnostic source changed")
    require(property_value(argv, "wps.water.potentialYieldTpha") == "5"
            and property_value(argv, "wps.water.ky") == "1", "crop response changed")
    farm = root / "twelve_upa_manifest.csv"
    require(captured_path(property_value(argv, "wps.water.farmAssignments"), captured_posix) ==
            (farm if captured_posix else farm.resolve()), "farm assignment path changed")
    for prop, filename in (("requests", "diagnostic_requests.csv"),
                           ("auditCsv", "water_audit.csv"),
                           ("yieldCsv", "yield_audit.csv"),
                           ("climateCsv", "climate_audit.csv")):
        expected = directory / filename
        require(captured_path(property_value(argv, f"wps.water.{prop}"), captured_posix) ==
                (expected if captured_posix else expected.resolve()), f"wrong {prop} path")
    require("org.wpsim.WellProdSim.wpsStart" in argv, "wrong entry point")
    require(option(argv, "-cp"), "missing classpath")


def check_runtime_identity(capture, argv, directory, captured_posix=False):
    """Check internal capture consistency, not independent executable attestation."""
    manifest_path = directory / "build_manifest.json"
    require(capture.get("build_manifest_sha256") == sha(manifest_path),
            "captured build manifest hash mismatch")
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    identity = capture.get("runtime_identity")
    require(identity == manifest and isinstance(identity, dict)
            and identity.get("schema") == "seed-diagnostic-build/v1",
            "captured runtime identity differs from build manifest")
    java, classpath = identity.get("java"), identity.get("classpath")
    require(isinstance(java, dict) and java.get("kind") == "file"
            and isinstance(classpath, list) and classpath,
            "invalid runtime identity shape")
    components = [java, *classpath]
    for entry in components:
        require(isinstance(entry, dict) and entry.get("kind") in ("file", "tree")
                and isinstance(entry.get("path"), str)
                and (captured_path(entry["path"], posix=True).is_absolute()
                     if captured_posix else Path(entry["path"]).is_absolute())
                and isinstance(entry.get("sha256"), str)
                and re.fullmatch(r"[0-9a-f]{64}", entry["sha256"]) is not None,
                "invalid runtime component identity")
    paths = [entry["path"] for entry in classpath]
    require(len(paths) == len(set(paths)) and
            captured_path(argv[0], captured_posix) ==
            captured_path(java["path"], captured_posix), "Java identity differs from argv")
    separator = ":" if captured_posix else os.pathsep
    require([captured_path(path, captured_posix) for path in option(argv, "-cp").split(separator)] ==
            [captured_path(path, captured_posix) for path in paths],
            "classpath order/identity differs from argv")


def marker_counts(stdout, directory):
    for marker, expected in MARKERS.items():
        matches = re.findall(rf"^{marker}: ([^\n]+)$", stdout, re.M)
        require(len(matches) == 1, f"missing/repeated {marker}")
        for key, value in expected.items():
            match = re.search(rf"\b{key}=(\d+)\b", matches[0])
            require(match is not None and int(match[1]) == value,
                    f"wrong {marker}.{key}")
        filename = {"PHYSICAL_WATER_AUDIT": "water_audit.csv",
                    "PHYSICAL_YIELD_AUDIT": "yield_audit.csv",
                    "PHYSICAL_CLIMATE_AUDIT": "climate_audit.csv"}.get(marker)
        if filename:
            require(matches[0].endswith(f"file={directory / filename}"),
                    f"wrong {marker} file path")
    for marker in ("PHYSICAL_CROP_COHORT: RICE_ONLY",
                   "PHYSICAL_WATER_HORIZON: ROUND_CHRONOLOGICAL",
                   "PHYSICAL_WATER: plots=48", "SEED: "):
        require(marker in stdout, f"missing {marker}")


def check_run(directory, seed, root, roster, windows, expected_hashes=FROZEN,
              synthetic=False, captured_root=None, captured_directory=None):
    capture = json.loads((directory / "capture.json").read_text(encoding="utf-8"))
    require(capture.get("schema") == "district-seed-diagnostic/v1", "wrong capture schema")
    kind = "synthetic_fixture" if synthetic else "real_seed_diagnostic"
    require(capture.get("kind") == kind, "capture provenance kind mismatch")
    require(capture.get("seed") == seed, "capture seed mismatch")
    require(capture.get("termination") == "natural", "termination not natural")
    require(capture.get("java_exit") == 0 and
            (directory / "exit.txt").read_text(encoding="utf-8").strip() == "JAVA_EXIT=0",
            "non-natural Java exit")
    require(capture.get("frozen_sha256") == expected_hashes, "capture frozen identities differ")
    require(capture.get("output_sha256") == {name: sha(directory / name) for name in OUTPUTS},
            "captured output hash mismatch")
    require(capture.get("diagnostic_requests_sha256") ==
            sha(directory / "diagnostic_requests.csv") == DIAGNOSTIC_REQUEST_SHA256,
            "diagnostic request hash mismatch")
    argv = capture.get("argv")
    captured_posix = captured_root is not None
    check_argv(argv, captured_directory if captured_posix else directory,
               captured_root if captured_posix else root, seed, captured_posix)
    check_runtime_identity(capture, argv, directory, captured_posix)
    require((directory / "command.txt").read_text(encoding="utf-8").strip() ==
            " ".join(argv), "command/argv mismatch")
    stdout = (directory / "stdout.txt").read_text(encoding="utf-8")
    require(re.findall(r"^SEED: (\d+)$", stdout, re.M) == [str(seed)], "runtime seed mismatch")
    marker_counts(stdout, captured_directory if captured_posix else directory)
    plots, plants = {}, {}
    for plot, crop, area_text in PLOT.findall(stdout):
        require(plot not in plots and plot in roster and crop == "rice" and
                number(area_text) == roster[plot], f"invalid plot trace: {plot}")
        plots[plot] = crop
    for plot, owner, planted_text, area_text in PLANT.findall(stdout):
        require(plot not in plants and plot in roster and number(area_text) == roster[plot],
                f"invalid plant trace: {plot}")
        plants[plot] = (owner, day(planted_text))
    require(len(re.findall(r"^WATER_PLOT:", stdout, re.M)) == 48
            and len(re.findall(r"^WATER_PLANT:", stdout, re.M)) == 48,
            "malformed/extra plot or plant trace")
    require(set(plots) == set(plants) == set(roster), "incomplete plot/plant trace")
    for plot, (_, owner, planted, _) in windows.items():
        require(plants[plot] == (owner, planted), f"owner/plant date mismatch: {plot}")
    for plot in set(roster) - set(windows):
        require(plot.endswith("_3") and plot.removesuffix("_3") in windows
                and plants[plot][0] == windows[plot.removesuffix("_3")][1],
                f"second-version owner mismatch: {plot}")
    harvests = rows(directory / "yield_audit.csv",
                    ("plot_id", "area_ha", "family_alias", "planting_date", "harvest_date",
                     "actual_et_mm", "potential_et_mm", "actual_t_ha", "actual_t", "full_t", "status"))
    require(len(harvests) == 24 and len({row["plot_id"] for row in harvests}) == 24,
            "missing/duplicate harvest")
    require({row["plot_id"] for row in harvests} == set(windows), "harvest cohort mismatch")
    for row in harvests:
        plot = row["plot_id"]
        area, owner, planted, harvested = windows[plot]
        require(row["status"] == "HARVESTED" and number(row["area_ha"]) == area
                and row["family_alias"] == owner and day(row["planting_date"]) == planted
                and day(row["harvest_date"]) == harvested,
                f"harvest window mismatch: {plot}")
    water = rows(directory / "water_audit.csv",
                 ("date", "plot_id", "area_ha", "gross_m3", "net_mm", "applied_net_mm", "status"))
    require(len(water) == 48 and {row["plot_id"] for row in water} == set(roster),
            "incomplete water audit rows")
    requests = rows(directory / "diagnostic_requests.csv",
                    ("date", "plot_id", "area_ha", "net_demand_mm", "delivery_efficiency"))
    require(len(requests) == 48 and {row["plot_id"] for row in requests} == set(roster),
            "incomplete diagnostic requests")
    requested = {row["plot_id"]: row for row in requests}
    status_counts = Counter()
    for row in water:
        plot = row["plot_id"]
        requested_row = requested[plot]
        require(number(row["area_ha"]) == number(requested_row["area_ha"]) == roster[plot]
                and day(row["date"]) == day(requested_row["date"]),
                f"water audit roster/request mismatch: {plot}")
        expected_status = "APPLIED" if plot in windows else "NO_DELIVERY"
        require(row["status"] == expected_status, f"water audit status mismatch: {plot}")
        if plot in windows:
            _, _, planted, harvested = windows[plot]
            require(planted <= day(row["date"]) <= harvested,
                    f"water audit outside crop window: {plot}")
        status_counts[row["status"]] += 1
    require(status_counts == {"APPLIED": 24, "NO_DELIVERY": 24},
            "water audit/applied-delivery marker mismatch")
    climate = rows(directory / "climate_audit.csv",
                   ("plot_id", "date", "rain_mm", "reference_et_mm", "temperature_c",
                    "short_wave_radiation"))
    require(len(climate) == 2904 and len({(row["plot_id"], row["date"]) for row in climate}) == 2904
            and {row["plot_id"] for row in climate} == set(windows), "incomplete climate audit rows")
    climate_days = defaultdict(set)
    for row in climate:
        climate_days[row["plot_id"]].add(day(row["date"]))
    for plot, (_, _, planted, harvested) in windows.items():
        expected_days = {planted + timedelta(days=index)
                         for index in range((harvested - planted).days + 1)}
        require(climate_days[plot] == expected_days,
                f"climate dates outside frozen crop window: {plot}")
    return {"seed": seed, "plots": 48, "eligible_plots": 24, "eligible_area_ha": 96,
            "assigned_upa": 12, "runtime_identity_scope": "captured_only"}


def qualify(root, directories, expected_hashes=FROZEN, synthetic=False,
            captured_posix_root=None, captured_posix_output_root=None):
    require(len(directories) == 2 and directories[0].resolve() != directories[1].resolve(),
            "two distinct diagnostic directories required")
    require((captured_posix_root is None) == (captured_posix_output_root is None),
            "captured POSIX roots must be supplied together")
    captured_root = None
    captured_directories = (None, None)
    if captured_posix_root is not None:
        captured_root = captured_path(captured_posix_root, posix=True)
        captured_output = captured_path(captured_posix_output_root, posix=True)
        captured_directories = tuple(captured_output / f"seed-{seed}" for seed in SEEDS)
    roster, windows = frozen_cohort(root, expected_hashes)
    result = [check_run(path, seed, root, roster, windows, expected_hashes, synthetic,
                        captured_root, captured_directory)
              for path, seed, captured_directory in
              zip(directories, SEEDS, captured_directories)]
    return {"status": "synthetic_contract_pass" if synthetic else "diagnostic_transcripts_admitted",
            "real_seed_qualification": "not_run" if synthetic else "transcript_only",
            "runs": result}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("seed_271828_dir", type=Path)
    parser.add_argument("seed_314159_dir", type=Path)
    parser.add_argument("--root", type=Path, default=ROOT)
    parser.add_argument("--synthetic-fixture", action="store_true")
    parser.add_argument("--captured-posix-root",
                        help="Absolute original POSIX experiments/water_allocation directory")
    parser.add_argument("--captured-posix-output-root",
                        help="Absolute original POSIX parent of seed-271828 and seed-314159")
    args = parser.parse_args()
    try:
        result = qualify(args.root, [args.seed_271828_dir, args.seed_314159_dir],
                         synthetic=args.synthetic_fixture,
                         captured_posix_root=args.captured_posix_root,
                         captured_posix_output_root=args.captured_posix_output_root)
    except (OSError, ValueError, KeyError, json.JSONDecodeError) as error:
        print(f"seed diagnostic rejected: {error}", file=sys.stderr)
        return 1
    print(json.dumps(result, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
