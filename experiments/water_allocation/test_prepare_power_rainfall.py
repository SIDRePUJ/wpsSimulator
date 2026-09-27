"""Synthetic guards for dated NASA POWER precipitation preparation."""

import csv
import tempfile
import unittest
from pathlib import Path

from prepare_power_rainfall import prepare


class PowerRainfallPreparationTest(unittest.TestCase):
    def test_remaps_complete_nonleap_years_without_changing_depth(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            source = root / "power.csv"
            lines = ["synthetic test", "-END HEADER-", "YEAR,DOY,PRECTOTCORR"]
            for year, rain in ((2014, 1.0), (2019, 2.0)):
                lines += [f"{year},{doy},{rain}" for doy in range(1, 366)]
            source.write_text("\n".join(lines) + "\n", encoding="utf-8")
            output = root / "derived"
            metadata = prepare(source, output)
            with (output / "power_rainfall_2014_as_2022.csv").open(encoding="utf-8") as stream:
                dry = list(csv.DictReader(stream))
            with (output / "power_rainfall_2019_as_2022.csv").open(encoding="utf-8") as stream:
                median = list(csv.DictReader(stream))
            self.assertEqual((len(dry), len(median)), (365, 365))
            self.assertEqual((dry[0]["date"], dry[-1]["date"]),
                             ("01/01/2022", "31/12/2022"))
            self.assertEqual((dry[58]["date"], dry[59]["date"]),
                             ("28/02/2022", "01/03/2022"))
            self.assertEqual(float(dry[0]["rain_mm"]), 1.0)
            self.assertEqual(float(median[0]["rain_mm"]), 2.0)
            self.assertEqual(metadata["scenarios"]["dry_proxy"]["window_mm"], 192.0)
            self.assertEqual(metadata["scenarios"]["near_median_proxy"]["window_mm"], 384.0)
            prepare(source, output)  # Identical regeneration is safe.
            source.write_text(source.read_text().replace("2014,1,1.0", "2014,1,5.0"))
            with self.assertRaises(FileExistsError):
                prepare(source, output)

    def test_rejects_missing_or_negative_precipitation(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            source = root / "power.csv"
            lines = ["-END HEADER-", "YEAR,DOY,PRECTOTCORR"]
            lines += [f"2014,{doy},0" for doy in range(1, 365)]
            source.write_text("\n".join(lines) + "\n")
            with self.assertRaisesRegex(ValueError, "Incomplete"):
                prepare(source, root / "derived")
            lines.append("2014,365,-999")
            source.write_text("\n".join(lines) + "\n")
            with self.assertRaisesRegex(ValueError, "invalid precipitation"):
                prepare(source, root / "derived")


if __name__ == "__main__":
    unittest.main()
