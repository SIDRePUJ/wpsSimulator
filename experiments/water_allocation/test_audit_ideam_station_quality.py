"""Synthetic structural failures and non-exclusion flags for raw IDEAM audits."""

import csv
import hashlib
import tempfile
import unittest
from datetime import date, timedelta
from pathlib import Path
from zipfile import ZipFile

from audit_ideam_station_quality import STATIONS, audit


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


class StationQualityAuditTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        root = Path(temporary.name)
        self.source = root / "source.zip"
        self.catalog = root / "catalog.csv"
        self.rows = {}
        for code in STATIONS:
            rows = []
            for year in (2019, 2022):
                for index in range(365):
                    day = date(year, 1, 1) + timedelta(days=index)
                    rows.append([f"{day} 07:00:00", "1.5" if index == 0 else "0.0"])
                rows.append([f"{year + 1}-01-01 07:00:00", "0.0"])
            self.rows[code] = rows
        self.installations = {code: "01/01/2018" for code in STATIONS}
        self.write_inputs()

    def write_inputs(self):
        with ZipFile(self.source, "w") as archive:
            for code, rows in self.rows.items():
                body = "Fecha|Valor\n" + "".join(f"{stamp}|{value}\n" for stamp, value in rows)
                archive.writestr(f"PTPM_CON_INTER@{code}.data", body)
        with self.catalog.open("w", encoding="utf-8", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=("Codigo", "Nombre", "Categoria",
                                                        "Estado", "Municipio", "Fecha_instalacion",
                                                        "Fecha_suspension", "LATITUD", "LONGITUD"))
            writer.writeheader()
            for code in STATIONS:
                writer.writerow({"Codigo": "00" + code, "Nombre": code,
                                 "Categoria": "Pluviométrica", "Estado": "Activa",
                                 "Municipio": "María La Baja",
                                 "Fecha_instalacion": self.installations[code],
                                 "Fecha_suspension": "", "LATITUD": "10", "LONGITUD": "-75"})

    def inspect(self):
        return audit(self.source, self.catalog, digest(self.source), digest(self.catalog))

    def test_complete_selected_years_and_adjacent_dates_pass_without_writes(self):
        before = (digest(self.source), digest(self.catalog))
        result = self.inspect()
        self.assertEqual("structural_pass_with_descriptive_flags", result["status"])
        self.assertEqual(4, result["selected_members"])
        self.assertEqual(8, len(result["station_years"]))
        self.assertTrue(all(row["observed_days"] == 365 for row in result["station_years"]))
        self.assertEqual(before, (digest(self.source), digest(self.catalog)))

    def test_raw_start_before_installation_is_flag_not_failure(self):
        self.installations["29030780"] = "15/07/2020"
        self.write_inputs()
        result = self.inspect()
        flags = [flag for flag in result["review_flags"]
                 if flag["kind"] == "raw_series_precedes_catalog_installation"]
        self.assertEqual(["29030780"], [flag["station_code"] for flag in flags])

    def test_missing_selected_or_required_adjacent_date_fails(self):
        self.rows["29030080"] = [row for row in self.rows["29030080"]
                                  if row[0] != "2019-07-01 07:00:00"]
        self.write_inputs()
        with self.assertRaisesRegex(ValueError, "missing selected-year date"):
            self.inspect()
        self.rows["29030080"].append(["2019-07-01 07:00:00", "0.0"])
        self.rows["29030080"] = [row for row in self.rows["29030080"]
                                  if row[0] != "2023-01-01 07:00:00"]
        self.write_inputs()
        with self.assertRaisesRegex(ValueError, "missing date required for previous_day"):
            self.inspect()

    def test_duplicate_or_malformed_timestamp_fails(self):
        self.rows["29030080"].append(list(self.rows["29030080"][0]))
        self.write_inputs()
        with self.assertRaisesRegex(ValueError, "duplicate raw date"):
            self.inspect()
        self.rows["29030080"].pop()
        self.rows["29030080"][0][0] = "2019-01-01 08:00:00"
        self.write_inputs()
        with self.assertRaisesRegex(ValueError, "timestamp is not 07:00:00"):
            self.inspect()

    def test_missing_selected_member_or_catalog_station_fails(self):
        self.rows.pop("29030780")
        self.write_inputs()
        with self.assertRaisesRegex(ValueError, "missing or duplicate raw ZIP member"):
            self.inspect()
        self.rows["29030780"] = list(self.rows["29030080"])
        self.write_inputs()
        lines = self.catalog.read_text(encoding="utf-8").splitlines()
        self.catalog.write_text("\n".join(line for line in lines
                                           if not line.startswith("0029030780,")) + "\n",
                                encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "selected station catalog set incomplete"):
            self.inspect()

    def test_negative_or_nonfinite_value_fails(self):
        for bad in ("-1", "NaN", "Infinity"):
            with self.subTest(value=bad):
                self.rows["29030080"][0][1] = bad
                self.write_inputs()
                with self.assertRaisesRegex(ValueError, "negative or non-finite"):
                    self.inspect()

    def test_locked_source_or_catalog_hash_change_fails(self):
        expected_source = digest(self.source)
        expected_catalog = digest(self.catalog)
        self.rows["29030080"][0][1] = "9"
        self.write_inputs()
        with self.assertRaisesRegex(ValueError, "source ZIP SHA-256 differs"):
            audit(self.source, self.catalog, expected_source, digest(self.catalog))
        with self.assertRaisesRegex(ValueError, "catalog SHA-256 differs"):
            audit(self.source, self.catalog, digest(self.source), "0" * 64)
        self.assertNotEqual(expected_catalog, "0" * 64)


if __name__ == "__main__":
    unittest.main()
