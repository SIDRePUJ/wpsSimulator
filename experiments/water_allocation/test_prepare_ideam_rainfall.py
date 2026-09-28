"""Synthetic guards for the station-specific IDEAM rainfall transformation."""

import hashlib
import json
import tempfile
import unittest
from datetime import date, timedelta
from pathlib import Path
from zipfile import ZipFile

from prepare_ideam_rainfall import STATIONS, load_station, prepare


def station_rows(year: int, missing: date | None = None) -> list[str]:
    rows = []
    for index in range(365):
        day = date(year, 1, 1) + timedelta(days=index)
        if day != missing:
            rows.append(f"{day} 07:00:00|{day.month}.0")
    rows.append(f"{year + 1}-01-01 07:00:00|9.0")
    return rows


def write_archive(path: Path, *, missing: date | None = None) -> bytes:
    with ZipFile(path, "w") as archive:
        for code in STATIONS:
            rows = ["Fecha|Valor"]
            rows += station_rows(2019, missing if code == "29030080" else None)
            rows += station_rows(2022)
            archive.writestr(f"PTPM_CON_INTER@{code}.data", "\n".join(rows) + "\n")
    return path.read_bytes()


class IdeamRainfallPreparationTest(unittest.TestCase):
    def test_writes_all_variants_and_regenerates_identically(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = root / "source.zip"
            source_bytes = write_archive(source)
            output = root / "ignored"
            manifest = root / "manifest.json"
            expected_hash = hashlib.sha256(source_bytes).hexdigest()

            metadata = prepare(source, output, manifest, expected_hash)
            self.assertEqual(len(metadata["scenarios"]), 16)
            self.assertEqual(metadata["source_sha256"], expected_hash)
            self.assertEqual(len(list(output.glob("*.csv"))), 16)
            sample = output / "ideam_29030080_2019_previous_day_as_2022.csv"
            lines = sample.read_text(encoding="utf-8").splitlines()
            self.assertEqual(len(lines), 366)
            self.assertEqual(lines[0], "date,rain_mm")
            self.assertEqual(lines[1], "01/01/2022,1.0")
            self.assertEqual(lines[-1], "31/12/2022,9.0")
            label = output / "ideam_29030080_2019_label_date_as_2022.csv"
            self.assertEqual(label.read_text(encoding="utf-8").splitlines()[-1],
                             "31/12/2022,12.0")
            self.assertEqual(json.loads(manifest.read_text(encoding="utf-8")), metadata)
            self.assertEqual(prepare(source, output, manifest, expected_hash), metadata)

            sample.write_text("changed", encoding="utf-8")
            with self.assertRaises(FileExistsError):
                prepare(source, output, manifest, expected_hash)

    def test_rejects_changed_source_and_missing_date_before_writing(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = root / "source.zip"
            source_bytes = write_archive(source, missing=date(2019, 7, 1))
            with self.assertRaisesRegex(ValueError, "source SHA-256"):
                prepare(source, root / "ignored", root / "manifest.json", "0" * 64)
            with self.assertRaisesRegex(ValueError, "Missing IDEAM date"):
                prepare(source, root / "ignored", root / "manifest.json",
                        hashlib.sha256(source_bytes).hexdigest())
            self.assertFalse((root / "ignored").exists())
            self.assertFalse((root / "manifest.json").exists())

    def test_rejects_bad_observation_rows(self):
        for bad_row, message in (
            ("2019-01-01 07:00:00|-1", "Invalid precipitation"),
            ("2019-01-01 07:00:00|NaN", "Invalid precipitation"),
            ("2019-01-01 08:00:00|1", "Unexpected observation timestamp"),
            ("2019-01-01 07:00:00|1", "Duplicate station date"),
        ):
            with self.subTest(bad_row=bad_row), tempfile.TemporaryDirectory() as directory:
                path = Path(directory) / "bad.zip"
                with ZipFile(path, "w") as archive:
                    archive.writestr("PTPM_CON_INTER@29030080.data",
                                         "Fecha|Valor\n2019-01-01 07:00:00|1\n" + bad_row + "\n")
                with ZipFile(path) as archive:
                    with self.assertRaisesRegex(ValueError, message):
                        load_station(archive, "29030080")


if __name__ == "__main__":
    unittest.main()
