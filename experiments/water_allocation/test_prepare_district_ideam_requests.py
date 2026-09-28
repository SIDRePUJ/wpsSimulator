"""Tests for independent crop-window extraction before request generation."""

import csv
import tempfile
import unittest
from pathlib import Path

from prepare_district_ideam_requests import ROOT, ROSTER, run_windows
from prepare_multidate_requests import read_roster, read_windows, validate_study_cohort


class DistrictWindowsTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.run = Path(temporary.name)
        self.roster = read_roster(ROSTER)
        old = read_windows(ROOT / "data/derived/world24_crop_windows.csv", self.roster)
        owners = {plot: value[1] for plot, value in old.items()}
        self.plants = []
        self.harvests = []
        for index, (plot, area) in enumerate(sorted(self.roster.items())):
            first = not plot.endswith("_3")
            owner = owners[plot if first else plot[:-2]]
            day = "01/02/2022" if index % 2 else "14/02/2022"
            self.plants.append(f"WATER_PLANT: plot_id={plot} family_alias={owner} planting_date={day} area_ha={area}\n")
            if first:
                self.harvests.append({"plot_id": plot, "area_ha": str(area),
                                      "family_alias": owner, "planting_date": day,
                                      "harvest_date": "01/06/2022" if day.startswith("01") else "14/06/2022",
                                      "status": "HARVESTED"})
        self.write()

    def write(self):
        (self.run / "exit.txt").write_text("JAVA_EXIT=0\n", encoding="utf-8")
        (self.run / "stdout.txt").write_text("".join(self.plants), encoding="utf-8")
        with (self.run / "yield_audit.csv").open("w", newline="", encoding="utf-8") as stream:
            writer = csv.DictWriter(stream, fieldnames=list(self.harvests[0]))
            writer.writeheader()
            writer.writerows(self.harvests)

    def test_windows_are_extracted_from_matching_plant_and_harvest(self):
        content, provenance = run_windows(self.run, self.roster)
        self.assertEqual(48, provenance["plant_records"])
        self.assertEqual(24, provenance["harvest_records"])
        path = self.run / "windows.csv"
        path.write_bytes(content)
        windows = read_windows(path, self.roster)
        validate_study_cohort(self.roster, windows)
        self.assertEqual(24, len(windows))

    def test_owner_or_planting_mismatch_is_rejected(self):
        self.harvests[0]["family_alias"] = "WRONG"
        self.write()
        with self.assertRaisesRegex(ValueError, "harvest/plant mismatch"):
            run_windows(self.run, self.roster)

    def test_missing_harvest_and_outside_window_are_rejected(self):
        self.harvests.pop()
        self.write()
        with self.assertRaisesRegex(ValueError, "24 unique"):
            run_windows(self.run, self.roster)
        self.harvests.append({"plot_id": "land_9_1_2", "area_ha": "8",
                              "family_alias": "MAS_PeasantFamily12", "planting_date": "14/02/2022",
                              "harvest_date": "14/06/2022", "status": "HARVESTED"})
        self.plants[0] = self.plants[0].replace("01/02/2022", "13/04/2022").replace("14/02/2022", "13/04/2022")
        self.write()
        with self.assertRaises(ValueError):
            run_windows(self.run, self.roster)


if __name__ == "__main__":
    unittest.main()
