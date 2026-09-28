"""Tests for station-request provenance and immutable preparation."""

import hashlib
import json
import shutil
import tempfile
import unittest
from datetime import date, timedelta
from decimal import Decimal
from pathlib import Path

from prepare_ideam_rainfall import MAPPINGS, SOURCE_SHA256, STATIONS, YEARS
from prepare_ideam_requests import ROOT, prepare_batch


def sha(content: bytes) -> str:
    return hashlib.sha256(content).hexdigest()


class IdeamRequestsTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.rain_dir = self.root / "rain"
        self.rain_dir.mkdir()
        self.output_dir = self.root / "requests"
        self.manifest = self.root / "requests_manifest.json"
        self.rain_manifest = self.root / "rain_manifest.json"
        self.roster = self.root / "roster.csv"
        self.windows = self.root / "windows.csv"
        source = ROOT / "data/derived"
        shutil.copyfile(source / "world24_plot_roster.csv", self.roster)
        shutil.copyfile(source / "world24_crop_windows.csv", self.windows)
        daily = ("date,rain_mm\n" + "".join(
            f"{(date(2022, 1, 1) + timedelta(days=i)):%d/%m/%Y},0\n"
            for i in range(365))).encode("utf-8")
        scenarios = {}
        for code in STATIONS:
            for year in YEARS:
                for mapping in MAPPINGS:
                    key = f"{code}/{year}/{mapping}"
                    filename = f"ideam_{code}_{year}_{mapping}_as_2022.csv"
                    (self.rain_dir / filename).write_bytes(daily)
                    scenarios[key] = {"station_code": code, "source_year": year,
                                      "date_mapping": mapping, "output_file": filename,
                                      "output_sha256": sha(daily)}
        self.rain_manifest.write_text(json.dumps({"source_sha256": SOURCE_SHA256.lower(),
                                                  "scenarios": scenarios}), encoding="utf-8")

    def batch(self):
        return prepare_batch(self.rain_manifest, self.rain_dir, self.roster,
                             self.windows, self.output_dir, self.manifest)

    def test_all_variants_are_audited_and_idempotent(self):
        audit = self.batch()
        self.assertEqual(16, len(audit["scenarios"]))
        self.assertEqual(sha(self.rain_manifest.read_bytes()), audit["rainfall_manifest_sha256"])
        for record in audit["scenarios"].values():
            content = (self.output_dir / record["request_file"]).read_bytes()
            self.assertEqual(408, record["request_rows"])
            self.assertEqual(sha(content), record["request_sha256"])
            gross = Decimal(record["unconstrained_gross_m3"])
            for ratio, stock in record["source_m3_by_ratio"].items():
                self.assertEqual((gross * Decimal(ratio)).quantize(Decimal("0.000001")),
                                 Decimal(stock))
        before = self.manifest.read_bytes()
        self.batch()
        self.assertEqual(before, self.manifest.read_bytes())

    def test_changed_rainfall_or_existing_request_fails_closed(self):
        path = next(self.rain_dir.glob("*.csv"))
        path.write_bytes(path.read_bytes().replace(b",0\n", b",1\n", 1))
        with self.assertRaisesRegex(ValueError, "SHA-256 differs"):
            self.batch()
        self.assertFalse(self.manifest.exists())

        path.write_bytes(("date,rain_mm\n" + "".join(
            f"{(date(2022, 1, 1) + timedelta(days=i)):%d/%m/%Y},0\n"
            for i in range(365))).encode("utf-8"))
        audit = self.batch()
        output = self.output_dir / next(iter(audit["scenarios"].values()))["request_file"]
        output.write_bytes(output.read_bytes() + b"tampered\n")
        with self.assertRaises(FileExistsError):
            self.batch()

    def test_missing_variant_in_manifest_is_rejected(self):
        data = json.loads(self.rain_manifest.read_text(encoding="utf-8"))
        data["scenarios"].pop(next(iter(data["scenarios"])))
        self.rain_manifest.write_text(json.dumps(data), encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "scenario set"):
            self.batch()
        self.assertFalse(self.output_dir.exists())


if __name__ == "__main__":
    unittest.main()
