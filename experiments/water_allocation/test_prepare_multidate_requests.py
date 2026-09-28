import csv
from datetime import date, timedelta
from decimal import Decimal
import hashlib
import json
from pathlib import Path
import tempfile
import unittest

from prepare_multidate_requests import (prepare, read_windows, read_rain, read_roster,
                                        validate_study_cohort, write_schedule)


def write_csv(path, fields, rows):
    with path.open("w", newline="", encoding="utf-8") as stream:
        writer = csv.DictWriter(stream, fieldnames=fields, lineterminator="\n")
        writer.writeheader()
        writer.writerows(rows)


class MultidateRequestsTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.rain_path = self.root / "rain.csv"
        self.roster_path = self.root / "roster.csv"
        self.harvest_path = self.root / "windows.csv"
        write_csv(self.rain_path, ("date", "rain_mm"), [
            {"date": (date(2022, 1, 1) + timedelta(days=i)).strftime("%d/%m/%Y"),
             "rain_mm": 5 if i < 7 else 0} for i in range(365)])
        write_csv(self.roster_path, ("plot_id", "area_ha"),
                  [{"plot_id": "first", "area_ha": 1},
                   {"plot_id": "second", "area_ha": 1}])
        write_csv(self.harvest_path,
                  ("plot_id", "area_ha", "family_alias", "planting_date", "harvest_date"),
                  [{"plot_id": "first", "area_ha": 1, "family_alias": "f1",
                    "planting_date": "01/01/2022", "harvest_date": "23/01/2022"}])

    def test_previous_seven_days_and_physical_gross_budget(self):
        roster = read_roster(self.roster_path)
        harvest = read_windows(self.harvest_path, roster)
        rows, gross = prepare(roster, harvest, read_rain(self.rain_path),
                              Decimal(30), Decimal("0.8"), Decimal("0.5"))
        self.assertEqual(["08/01/2022", "15/01/2022"],
                         [row[0].strftime("%d/%m/%Y") for row in rows if row[1] == "first"])
        self.assertEqual([Decimal(2), Decimal(30)],
                         [row[3] for row in rows if row[1] == "first"])
        self.assertEqual((date(2022, 5, 1), "second", Decimal(1), Decimal(0), Decimal("0.5")),
                         rows[-1])
        self.assertEqual(Decimal(640), gross)
        self.assertEqual(Decimal(224), gross * Decimal("0.35"))
        output = self.root / "requests.csv"
        write_schedule(output, rows)
        with self.assertRaises(FileExistsError):
            write_schedule(output, rows)
        self.assertEqual(4, len(output.read_text(encoding="utf-8").splitlines()))

    def test_fails_on_incomplete_rain_or_changed_harvest_cohort(self):
        lines = self.rain_path.read_text(encoding="utf-8").splitlines()
        self.rain_path.write_text("\n".join(lines[:-1]) + "\n", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "every day"):
            read_rain(self.rain_path)
        roster = read_roster(self.roster_path)
        with self.assertRaisesRegex(ValueError, "48 registered"):
            validate_study_cohort(roster, read_windows(self.harvest_path, roster))
        text = self.harvest_path.read_text(encoding="utf-8").replace("first,1", "first,2")
        self.harvest_path.write_text(text, encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "owner or area mismatch"):
            read_windows(self.harvest_path, roster)

    def test_frozen_study_fixtures_reproduce_from_committed_inputs(self):
        data = Path(__file__).with_name("data") / "derived"
        roster = read_roster(data / "world24_plot_roster.csv")
        windows = read_windows(data / "world24_crop_windows.csv", roster)
        validate_study_cohort(roster, windows)
        for year in (2014, 2019):
            rain = read_rain(data / f"power_rainfall_{year}_as_2022.csv")
            rows, gross = prepare(roster, windows, rain, Decimal(30), Decimal("0.8"),
                                  Decimal("0.48"))
            self.assertEqual(408, len(rows))
            output = self.root / f"{year}.csv"
            write_schedule(output, rows)
            frozen = data / f"world24-weekly-{year}-guarded.csv"
            self.assertEqual(frozen.read_bytes(), output.read_bytes())
            budget = json.loads((data / f"world24-weekly-{year}-guarded.budget.json").read_text())
            self.assertEqual(hashlib.sha256(output.read_bytes()).hexdigest(),
                             budget["request_sha256"])
            self.assertEqual(Decimal(budget["unconstrained_gross_m3"]), gross)
            for ratio in ("0.35", "0.65", "1.00"):
                self.assertEqual(Decimal(budget["source_m3_by_ratio"][ratio]),
                                 (gross * Decimal(ratio)).quantize(Decimal("0.000001")))


if __name__ == "__main__":
    unittest.main()
