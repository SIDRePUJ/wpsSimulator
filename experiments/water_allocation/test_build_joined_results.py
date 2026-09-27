"""Fail-closed run-admission tests with synthetic physical audit files."""

import csv
import json
import shutil
import tempfile
import unittest
from pathlib import Path

from build_joined_results import build


def write_csv(path, fields, rows):
    with path.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=fields)
        writer.writeheader()
        writer.writerows(rows)


class JoinedResultsTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.run = self.root / "run"
        self.run.mkdir()
        self.output = self.root / "joined.csv"
        write_csv(self.root / "requests.csv",
                  ("date", "plot_id", "area_ha", "net_demand_mm", "delivery_efficiency"),
                  [{"date": "02/01/2022", "plot_id": "p1", "area_ha": 1,
                    "net_demand_mm": 10, "delivery_efficiency": 1}])
        write_csv(self.root / "farms.csv", ("family_alias", "farm_name"),
                  [{"family_alias": "family-a", "farm_name": "farm-a"}])
        write_csv(self.root / "rain.csv", ("date", "rain_mm"),
                  [{"date": f"0{day}/01/2022", "rain_mm": day - 1} for day in (1, 2, 3)])
        self.water_fields = ("date", "plot_id", "area_ha", "gross_m3", "net_mm",
                             "applied_net_mm", "status")
        self.water = {"date": "02/01/2022", "plot_id": "p1", "area_ha": 1,
                      "gross_m3": 100, "net_mm": 10, "applied_net_mm": 10, "status": "APPLIED"}
        self.yield_fields = ("plot_id", "area_ha", "family_alias", "planting_date",
                             "harvest_date", "actual_t_ha", "actual_t", "full_t", "status")
        self.harvest = {"plot_id": "p1", "area_ha": 1, "family_alias": "family-a",
                        "planting_date": "01/01/2022", "harvest_date": "03/01/2022",
                        "actual_t_ha": 4, "actual_t": 4, "full_t": 6, "status": "HARVESTED"}
        self.climate_fields = ("plot_id", "date", "rain_mm", "reference_et_mm",
                               "temperature_c", "short_wave_radiation")
        self.climate = [{"plot_id": "p1", "date": f"0{day}/01/2022",
                         "rain_mm": day - 1, "reference_et_mm": 4,
                         "temperature_c": 25, "short_wave_radiation": 12}
                        for day in (1, 2, 3)]
        self.spec = {"directory": "run", "requests_csv": "requests.csv",
                     "farm_csv": "farms.csv", "rain_csv": "rain.csv",
                     "weather": "proxy", "scarcity_ratio": 1,
                     "rule": "PROPORTIONAL_DEMAND", "seed": 1,
                     "parameter_set": "A", "source_m3": 100}
        self.write_run()

    def write_run(self, *, applied=1):
        (self.run / "exit.txt").write_text("0\n", encoding="utf-8")
        (self.run / "stdout.txt").write_text(
            "PHYSICAL_FARM_AUDIT: Status[plannedFamilies=1, assignedFamilies=1, failedFamilies=0]\n"
            f"PHYSICAL_WATER_AUDIT: AuditSummary[plannedPlots=1, registeredPlots=1, absentPlots=0, failedPlotRegistrations=0, missingDeliveries=0, appliedDeliveries={applied}]\n"
            "PHYSICAL_YIELD_AUDIT: Summary[plannedPlots=1, harvestedPlots=1, missingHarvests=0]\n"
            "PHYSICAL_CLIMATE_AUDIT: Summary[plannedPlots=1, observedPlots=1, dailyRows=3, missingPlots=0, gapDays=0, duplicateDays=0]\n",
            encoding="utf-8")
        write_csv(self.run / "audit.csv", self.water_fields, [self.water])
        write_csv(self.run / "yield.csv", self.yield_fields, [self.harvest])
        write_csv(self.run / "climate.csv", self.climate_fields, self.climate)
        self.manifest = self.root / "manifest.json"
        self.manifest.write_text(json.dumps({"runs": [self.spec]}), encoding="utf-8")

    def test_admits_complete_run(self):
        self.assertEqual(1, build(self.manifest, self.output))
        with self.output.open(newline="", encoding="utf-8") as stream:
            result = list(csv.DictReader(stream))
        self.assertEqual("family-a", result[0]["family_alias"])
        self.assertEqual("100.0", result[0]["gross_m3"])

    def test_zero_allocation_still_keeps_positive_demand_plot(self):
        self.water.update(gross_m3=0, net_mm=0, applied_net_mm="", status="NO_DELIVERY")
        self.spec.update(source_m3=0, scarcity_ratio=0)
        self.write_run(applied=0)
        self.assertEqual(1, build(self.manifest, self.output))
        with self.output.open(newline="", encoding="utf-8") as stream:
            self.assertEqual("0.0", list(csv.DictReader(stream))[0]["gross_m3"])

    def test_rejects_failed_run_without_output(self):
        (self.run / "exit.txt").write_text("2\n", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "did not exit zero"):
            build(self.manifest, self.output)
        self.assertFalse(self.output.exists())

    def test_rejects_unmatched_water_area(self):
        self.water["area_ha"] = 2
        self.write_run()
        with self.assertRaisesRegex(ValueError, "water area mismatch"):
            build(self.manifest, self.output)
        self.assertFalse(self.output.exists())

    def test_rejects_missing_climate_day_despite_claimed_audit(self):
        self.climate.pop()
        self.write_run()
        with self.assertRaisesRegex(ValueError, "climate/crop daily cohort mismatch"):
            build(self.manifest, self.output)

    def test_rejects_rainfall_mismatch(self):
        self.climate[1]["rain_mm"] = 99
        self.write_run()
        with self.assertRaisesRegex(ValueError, "rainfall forcing mismatch"):
            build(self.manifest, self.output)

    def test_rejects_missing_audit_marker(self):
        (self.run / "stdout.txt").write_text("", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "expected one FARM audit"):
            build(self.manifest, self.output)

    def test_rejects_different_climate_for_paired_rule(self):
        second = self.root / "other"
        shutil.copytree(self.run, second)
        changed = [dict(row) for row in self.climate]
        changed[1]["temperature_c"] = 26
        write_csv(second / "climate.csv", self.climate_fields, changed)
        alternate = {**self.spec, "directory": "other", "rule": "SMALL_PLOT_FLOOR"}
        self.manifest.write_text(json.dumps({"runs": [self.spec, alternate]}), encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "paired runs differ"):
            build(self.manifest, self.output)
        self.assertFalse(self.output.exists())

    def test_rejects_shared_source_overwithdrawal(self):
        self.spec["source_m3"] = 50
        self.spec["scarcity_ratio"] = 0.5
        self.write_run()
        with self.assertRaisesRegex(ValueError, "shared-source withdrawal mismatch"):
            build(self.manifest, self.output)


if __name__ == "__main__":
    unittest.main()
