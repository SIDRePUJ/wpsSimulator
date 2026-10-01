"""Synthetic, offline contract tests; these do not qualify actual seeds."""

import csv
from datetime import date, timedelta
import json
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

import check_ideam_seed_qualification as gate


SOURCE = Path(__file__).resolve().parent


def write_csv(path, header, rows):
    with path.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.writer(stream, lineterminator="\n")
        writer.writerow(header)
        writer.writerows(rows)


class SeedQualificationTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name) / "repo/experiments/water_allocation"
        self.root.mkdir(parents=True)
        for name in gate.FROZEN:
            target = self.root / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(SOURCE / name, target)
        self.roster, self.windows = gate.frozen_cohort(self.root)
        self.dirs = [self.root / f"diagnostic-{seed}" for seed in gate.SEEDS]
        for directory, seed in zip(self.dirs, gate.SEEDS):
            self.make_run(directory, seed)

    def make_run(self, directory, seed):
        directory.mkdir()
        shutil.copyfile(SOURCE / "reports/raw/calendar-c2-local-20260928-web-osredirect/diagnostic_requests.csv",
                        directory / "diagnostic_requests.csv")
        argv = ["java", "-Dwps.water.districtRiceCalendar=true",
                "-Dwps.water.riceOnlyCohort=true", "-Dwps.water.discoverPlots=true",
                f"-Dwps.water.requests={directory / 'diagnostic_requests.csv'}",
                "-Dwps.water.sourceM3=19200", "-Dwps.water.rule=PROPORTIONAL_DEMAND",
                f"-Dwps.water.farmAssignments={self.root / 'twelve_upa_manifest.csv'}",
                f"-Dwps.water.auditCsv={directory / 'water_audit.csv'}",
                f"-Dwps.water.yieldCsv={directory / 'yield_audit.csv'}",
                "-Dwps.water.potentialYieldTpha=5", "-Dwps.water.ky=1",
                f"-Dwps.water.climateCsv={directory / 'climate_audit.csv'}",
                "-cp", "synthetic-classpath", "org.wpsim.WellProdSim.wpsStart",
                "-env", "local", "-mode", "web", "-agents", "12", "-world", "24",
                "-land", "2", "-years", "1", "-startyear", "2022", "-seed", str(seed),
                "-perturbation", "none"]
        (directory / "command.txt").write_text(" ".join(argv) + "\n", encoding="utf-8")
        (directory / "exit.txt").write_text("JAVA_EXIT=0\n", encoding="utf-8")
        (directory / "stderr.txt").write_text("", encoding="utf-8")
        stdout = [f"SEED: {seed}", "PHYSICAL_CROP_COHORT: RICE_ONLY",
                  "PHYSICAL_WATER_HORIZON: ROUND_CHRONOLOGICAL",
                  "PHYSICAL_WATER: plots=48 source_m3=19200 allocated_m3=19200"]
        for plot, area in sorted(self.roster.items()):
            reference = plot.removesuffix("_3") if plot.endswith("_3") else plot
            _, owner, planted, _ = self.windows[reference]
            if plot.endswith("_3"):
                planted = date(2022, 9, 26)
            stdout.append(f"WATER_PLOT: plot_id={plot} crop=rice area_ha={area}")
            stdout.append(f"WATER_PLANT: plot_id={plot} family_alias={owner} "
                          f"planting_date={planted:%d/%m/%Y} area_ha={area}")
        stdout += [
            "PHYSICAL_WATER_AUDIT: AuditSummary[plannedPlots=48, registeredPlots=48, "
            f"absentPlots=0, failedPlotRegistrations=0, missingDeliveries=0, appliedDeliveries=24] file={directory / 'water_audit.csv'}",
            "PHYSICAL_FARM_AUDIT: Status[plannedFamilies=12, assignedFamilies=12, failedFamilies=0]",
            f"PHYSICAL_YIELD_AUDIT: Summary[plannedPlots=24, harvestedPlots=24, missingHarvests=0] file={directory / 'yield_audit.csv'}",
            "PHYSICAL_CLIMATE_AUDIT: Summary[plannedPlots=24, observedPlots=24, dailyRows=2904, "
            f"missingPlots=0, gapDays=0, duplicateDays=0] file={directory / 'climate_audit.csv'}",
        ]
        (directory / "stdout.txt").write_text("\n".join(stdout) + "\n", encoding="utf-8")
        write_csv(directory / "yield_audit.csv",
                  ("plot_id", "area_ha", "family_alias", "planting_date", "harvest_date",
                   "actual_et_mm", "potential_et_mm", "actual_t_ha", "actual_t", "full_t", "status"),
                  ((plot, area, owner, f"{planted:%d/%m/%Y}", f"{harvested:%d/%m/%Y}",
                    "", "", "", "", "", "HARVESTED")
                   for plot, (area, owner, planted, harvested) in sorted(self.windows.items())))
        with (directory / "diagnostic_requests.csv").open(encoding="utf-8", newline="") as stream:
            diagnostic_rows = list(csv.DictReader(stream))
        write_csv(directory / "water_audit.csv",
                  ("date", "plot_id", "area_ha", "gross_m3", "net_mm", "applied_net_mm", "status"),
                  ((row["date"], row["plot_id"], row["area_ha"], "", "", "",
                    "APPLIED" if row["plot_id"] in self.windows else "NO_DELIVERY")
                   for row in diagnostic_rows))
        write_csv(directory / "climate_audit.csv",
                  ("plot_id", "date", "rain_mm", "reference_et_mm", "temperature_c",
                   "short_wave_radiation"),
                  ((plot, f"{planted + timedelta(days=index):%d/%m/%Y}", "", "", "", "")
                   for plot, (_, _, planted, harvested) in sorted(self.windows.items())
                   for index in range((harvested - planted).days + 1)))
        capture = {"schema": "district-seed-diagnostic/v1", "kind": "synthetic_fixture",
                   "seed": seed, "termination": "natural", "java_exit": 0,
                   "argv": argv, "frozen_sha256": gate.FROZEN,
                   "diagnostic_requests_sha256": gate.sha(directory / "diagnostic_requests.csv"),
                   "output_sha256": {name: gate.sha(directory / name) for name in gate.OUTPUTS}}
        (directory / "capture.json").write_text(json.dumps(capture), encoding="utf-8")

    def resign(self, directory):
        path = directory / "capture.json"
        capture = json.loads(path.read_text(encoding="utf-8"))
        capture["output_sha256"] = {name: gate.sha(directory / name) for name in gate.OUTPUTS}
        path.write_text(json.dumps(capture), encoding="utf-8")

    def assert_rejected(self, text):
        with self.assertRaisesRegex(ValueError, text):
            gate.qualify(self.root, self.dirs, synthetic=True)

    def test_synthetic_transcripts_admitted_but_not_real_qualification(self):
        result = gate.qualify(self.root, self.dirs, synthetic=True)
        self.assertEqual(result["status"], "synthetic_contract_pass")
        self.assertEqual(result["real_seed_qualification"], "not_run")
        process = subprocess.run([sys.executable, str(SOURCE / "check_ideam_seed_qualification.py"),
                                  str(self.dirs[0]), str(self.dirs[1]), "--root", str(self.root),
                                  "--synthetic-fixture"], capture_output=True, text=True)
        self.assertEqual(process.returncode, 0, process.stderr)
        self.assertIn('"real_seed_qualification": "not_run"', process.stdout)

    def test_real_mode_refuses_synthetic_capture(self):
        with self.assertRaisesRegex(ValueError, "provenance kind"):
            gate.qualify(self.root, self.dirs)

    def test_seed_and_argv_must_agree_with_runtime_trace(self):
        first = self.dirs[0]
        path = first / "capture.json"
        capture = json.loads(path.read_text(encoding="utf-8"))
        capture["argv"][capture["argv"].index("-seed") + 1] = "12345"
        path.write_text(json.dumps(capture), encoding="utf-8")
        self.assert_rejected("seed differs from argv")

    def test_non_natural_exit_rejected(self):
        first = self.dirs[0]
        path = first / "capture.json"
        capture = json.loads(path.read_text(encoding="utf-8"))
        capture["termination"] = "timeout"
        path.write_text(json.dumps(capture), encoding="utf-8")
        self.assert_rejected("termination not natural")

    def test_frozen_hash_mutation_rejected(self):
        path = self.root / "data/derived/world24_district_crop_windows.csv"
        path.write_bytes(path.read_bytes() + b"\n")
        self.assert_rejected("frozen hash mismatch")

    def test_output_hash_mutation_rejected(self):
        path = self.dirs[0] / "stdout.txt"
        path.write_bytes(path.read_bytes() + b"extra\n")
        self.assert_rejected("captured output hash mismatch")

    def test_duplicate_plant_rejected_even_with_updated_hash(self):
        first = self.dirs[0]
        path = first / "stdout.txt"
        line = next(line for line in path.read_text(encoding="utf-8").splitlines()
                    if line.startswith("WATER_PLANT:"))
        path.write_text(path.read_text(encoding="utf-8") + line + "\n", encoding="utf-8")
        self.resign(first)
        self.assert_rejected("invalid plant trace")

    def test_missing_plot_trace_rejected_even_with_updated_hash(self):
        first = self.dirs[0]
        path = first / "stdout.txt"
        lines = path.read_text(encoding="utf-8").splitlines()
        lines.remove(next(line for line in lines if line.startswith("WATER_PLOT:")))
        path.write_text("\n".join(lines) + "\n", encoding="utf-8")
        self.resign(first)
        self.assert_rejected("malformed/extra plot or plant trace")

    def test_changed_planting_date_rejected_even_with_updated_hash(self):
        first = self.dirs[0]
        path = first / "stdout.txt"
        text = path.read_text(encoding="utf-8")
        path.write_text(text.replace("planting_date=01/02/2022", "planting_date=02/02/2022", 1),
                        encoding="utf-8")
        self.resign(first)
        self.assert_rejected("owner/plant date mismatch")

    def test_changed_owner_window_rejected(self):
        first = self.dirs[0]
        path = first / "yield_audit.csv"
        content = path.read_text(encoding="utf-8")
        path.write_text(content.replace("MAS_PeasantFamily1", "MAS_PeasantFamily2", 1), encoding="utf-8")
        self.resign(first)
        self.assert_rejected("harvest window mismatch")

    def test_missing_harvest_rejected(self):
        first = self.dirs[0]
        path = first / "yield_audit.csv"
        path.write_text("\n".join(path.read_text(encoding="utf-8").splitlines()[:-1]) + "\n",
                        encoding="utf-8")
        self.resign(first)
        self.assert_rejected("missing/duplicate harvest")

    def test_incomplete_audit_marker_rejected(self):
        first = self.dirs[0]
        path = first / "stdout.txt"
        path.write_text(path.read_text(encoding="utf-8").replace("registeredPlots=48", "registeredPlots=47"),
                        encoding="utf-8")
        self.resign(first)
        self.assert_rejected("PHYSICAL_WATER_AUDIT.registeredPlots")

    def test_audit_marker_from_other_directory_rejected(self):
        first = self.dirs[0]
        path = first / "stdout.txt"
        path.write_text(path.read_text(encoding="utf-8").replace(
            str(first / "yield_audit.csv"), str(self.dirs[1] / "yield_audit.csv")),
            encoding="utf-8")
        self.resign(first)
        self.assert_rejected("PHYSICAL_YIELD_AUDIT file path")

    def test_no_delivery_rows_cannot_claim_applied_deliveries(self):
        first = self.dirs[0]
        path = first / "water_audit.csv"
        path.write_text(path.read_text(encoding="utf-8").replace(
            ",APPLIED\n", ",NO_DELIVERY\n"), encoding="utf-8")
        self.resign(first)
        self.assert_rejected("water audit status mismatch")

    def test_water_area_and_date_must_match_request_roster(self):
        first = self.dirs[0]
        path = first / "water_audit.csv"
        original = path.read_text(encoding="utf-8")
        path.write_text(original.replace("07/04/2022,land_0_0_2,1,",
                                         "08/04/2022,land_0_0_2,1,", 1), encoding="utf-8")
        self.resign(first)
        self.assert_rejected("water audit roster/request mismatch")
        path.write_text(original.replace("07/04/2022,land_0_0_2,1,",
                                         "07/04/2022,land_0_0_2,2,", 1), encoding="utf-8")
        self.resign(first)
        self.assert_rejected("water audit roster/request mismatch")

    def test_climate_dates_must_match_each_frozen_crop_window(self):
        first = self.dirs[0]
        path = first / "climate_audit.csv"
        path.write_text(path.read_text(encoding="utf-8").replace(
            "land_0_0_2,01/02/2022,", "land_0_0_2,01/01/2022,", 1), encoding="utf-8")
        self.resign(first)
        self.assert_rejected("climate dates outside frozen crop window")

    def test_same_directory_rejected(self):
        with self.assertRaisesRegex(ValueError, "distinct"):
            gate.qualify(self.root, [self.dirs[0], self.dirs[0]], synthetic=True)


if __name__ == "__main__":
    unittest.main()
