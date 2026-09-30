"""Synthetic fail-closed tests for the read-only IDEAM readiness checker."""

import csv
import hashlib
import json
import shutil
import tempfile
import unittest
from datetime import date, timedelta
from decimal import Decimal
from pathlib import Path

from check_ideam_readiness import MAPPINGS, STATIONS, YEARS, check
from prepare_multidate_requests import (prepare, read_roster, read_windows,
                                        write_schedule)


SOURCE = Path(__file__).resolve().parent


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


class IdeamReadinessTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name) / "experiments/water_allocation"
        derived = self.root / "data/derived"
        raw = self.root / "data/raw"
        rain_dir = raw / "ideam_derived"
        request_dir = rain_dir / "district_requests"
        request_dir.mkdir(parents=True)
        derived.mkdir(parents=True)
        world = self.root.parents[1] / "src/main/resources/web/data/world.24.json"
        world.parent.mkdir(parents=True)
        for name in ("world24_plot_roster.csv", "world24_district_crop_windows.csv"):
            shutil.copyfile(SOURCE / "data/derived" / name, derived / name)
        (self.root / "twelve_upa_manifest.csv").write_text(
            "family_alias,farm_name\nf1,farm1\n", encoding="utf-8")
        world.write_text("{}\n", encoding="utf-8")
        (raw / "ideam_precipitacion_nacional_diaria.zip").write_bytes(b"synthetic source fixture")
        with (raw / "ideam_station_catalog.csv").open("w", encoding="utf-8", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=("Codigo", "Estado", "Categoria",
                                                        "Municipio", "LATITUD", "LONGITUD"))
            writer.writeheader()
            for code in STATIONS:
                writer.writerow({"Codigo": "00" + code, "Estado": "Activa",
                                 "Categoria": "Pluviométrica", "Municipio": "María La Baja",
                                 "LATITUD": "10", "LONGITUD": "-75"})
        roster = read_roster(derived / "world24_plot_roster.csv")
        windows = read_windows(derived / "world24_district_crop_windows.csv", roster)
        rain = {date(2022, 1, 1) + timedelta(days=index): Decimal(0) for index in range(365)}
        rainfall_scenarios = {}
        request_scenarios = {}
        for code in STATIONS:
            for year in YEARS:
                for mapping in MAPPINGS:
                    key = f"{code}/{year}/{mapping}"
                    rain_name = f"ideam_{code}_{year}_{mapping}_as_2022.csv"
                    request_name = f"ideam_requests_{code}_{year}_{mapping}.csv"
                    rain_path = rain_dir / rain_name
                    rain_path.write_text("date,rain_mm\n" + "".join(
                        f"{day:%d/%m/%Y},0\n" for day in rain), encoding="utf-8")
                    rows, gross = prepare(roster, windows, rain, Decimal(30),
                                          Decimal("0.8"), Decimal("0.48"))
                    request_path = request_dir / request_name
                    write_schedule(request_path, rows)
                    rainfall_scenarios[key] = {
                        "station_code": code, "source_year": year,
                        "date_mapping": mapping, "station": STATIONS[code],
                        "output_file": rain_name, "output_sha256": digest(rain_path),
                        "day_count": 365, "window_02_01_to_08_11_mm": "0",
                    }
                    request_scenarios[key] = {
                        "station_code": code, "source_year": year,
                        "date_mapping": mapping, "request_file": request_name,
                        "request_sha256": digest(request_path),
                        "rain_sha256": digest(rain_path), "request_rows": len(rows),
                        "unconstrained_gross_m3": str(gross),
                        "source_m3_by_ratio": {
                            ratio: str((gross * Decimal(ratio)).quantize(Decimal("0.000001")))
                            for ratio in ("0.35", "0.65", "1.00")},
                    }
        self.rain_manifest = derived / "ideam_rainfall_manifest.json"
        self.request_manifest = derived / "ideam_district_request_manifest.json"
        self.rain_document = {
            "source_sha256": digest(raw / "ideam_precipitacion_nacional_diaria.zip"),
            "source_file": "ideam_precipitacion_nacional_diaria.zip",
            "source_daily_unit": "mm", "source_label": "PTPM_CON_INTER",
            "simulation_year": 2022, "scenarios": rainfall_scenarios,
        }
        self.request_document = {
            "ideam_source_sha256": self.rain_document["source_sha256"],
            "roster_sha256": digest(derived / "world24_plot_roster.csv"),
            "windows_sha256": digest(derived / "world24_district_crop_windows.csv"),
            "parameters": {"target_mm": "30", "rain_factor": "0.8",
                           "delivery_efficiency": "0.48"},
            "scenarios": request_scenarios,
        }
        (derived / "district_crop_cohort_manifest.json").write_text(json.dumps({
            "windows_sha256": self.request_document["windows_sha256"]}), encoding="utf-8")
        self.locked = {}
        for relative in ("data/raw/ideam_precipitacion_nacional_diaria.zip",
                         "data/raw/ideam_station_catalog.csv",
                         "data/derived/district_crop_cohort_manifest.json",
                         "data/derived/world24_district_crop_windows.csv",
                         "data/derived/world24_plot_roster.csv",
                         "twelve_upa_manifest.csv",
                         "../../src/main/resources/web/data/world.24.json"):
            self.locked[relative] = digest(self.root / relative)
        self.flush_manifests()

    def flush_manifests(self):
        self.rain_manifest.write_text(json.dumps(self.rain_document), encoding="utf-8")
        self.request_document["rainfall_manifest_sha256"] = digest(self.rain_manifest)
        self.request_manifest.write_text(json.dumps(self.request_document), encoding="utf-8")
        self.locked["data/derived/ideam_rainfall_manifest.json"] = digest(self.rain_manifest)
        self.locked["data/derived/ideam_district_request_manifest.json"] = digest(self.request_manifest)

    def file_for(self, kind):
        key = "29030080/2019/label_date"
        if kind == "rain":
            record = self.rain_document["scenarios"][key]
            return self.root / "data/raw/ideam_derived" / record["output_file"], record
        record = self.request_document["scenarios"][key]
        return self.root / "data/raw/ideam_derived/district_requests" / record["request_file"], record

    def edit_csv(self, kind, change):
        path, record = self.file_for(kind)
        with path.open(encoding="utf-8", newline="") as stream:
            reader = csv.DictReader(stream)
            fields, rows = reader.fieldnames, list(reader)
        change(rows)
        with path.open("w", encoding="utf-8", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=fields, lineterminator="\n")
            writer.writeheader()
            writer.writerows(rows)
        record["output_sha256" if kind == "rain" else "request_sha256"] = digest(path)
        if kind == "rain":
            self.request_document["scenarios"]["29030080/2019/label_date"]["rain_sha256"] = digest(path)
        self.flush_manifests()

    def test_all_synthetic_scenarios_pass_without_writing(self):
        before = {str(path): digest(path) for path in self.root.rglob("*") if path.is_file()}
        result = check(self.root, self.locked)
        self.assertEqual("structural_identity_pass", result["status"])
        self.assertEqual(16, result["scenario_count"])
        self.assertFalse(result["station_quality_certified"])
        self.assertEqual(before, {str(path): digest(path) for path in self.root.rglob("*") if path.is_file()})

    def test_missing_or_changed_file_hash_fails(self):
        path, _ = self.file_for("rain")
        path.write_text("changed\n", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "SHA-256 differs"):
            check(self.root, self.locked)
        path.unlink()
        with self.assertRaisesRegex(ValueError, "missing locked input"):
            check(self.root, self.locked)

    def test_duplicate_or_missing_rain_date_fails_after_hash_refresh(self):
        self.edit_csv("rain", lambda rows: rows[1].update(date=rows[0]["date"]))
        with self.assertRaisesRegex(ValueError, "duplicate date"):
            check(self.root, self.locked)
        self.edit_csv("rain", lambda rows: (rows[1].update(date="02/01/2022"), rows.pop()))
        with self.assertRaisesRegex(ValueError, "every day"):
            check(self.root, self.locked)

    def test_duplicate_plot_and_wrong_area_fail_after_hash_refresh(self):
        path, _ = self.file_for("request")
        with path.open(encoding="utf-8", newline="") as stream:
            second_plot = list(csv.DictReader(stream))[1]["plot_id"]
        self.edit_csv("request", lambda rows: rows[1].update(plot_id=rows[0]["plot_id"]))
        with self.assertRaisesRegex(ValueError, "missing/duplicate/unknown request plot"):
            check(self.root, self.locked)
        self.edit_csv("request", lambda rows: (rows[1].update(plot_id=second_plot),
                                               rows[0].update(area_ha="99")))
        with self.assertRaisesRegex(ValueError, "request area, depth or efficiency"):
            check(self.root, self.locked)

    def test_missing_request_plot_row_fails_after_hash_refresh(self):
        self.edit_csv("request", lambda rows: rows.pop())
        with self.assertRaisesRegex(ValueError, "request row count differs"):
            check(self.root, self.locked)

    def test_outside_crop_window_and_negative_depth_fail(self):
        path, _ = self.file_for("request")
        with path.open(encoding="utf-8", newline="") as stream:
            first_date = next(csv.DictReader(stream))["date"]
        self.edit_csv("request", lambda rows: rows[0].update(date="01/01/2022"))
        with self.assertRaisesRegex(ValueError, "outside frozen crop window"):
            check(self.root, self.locked)
        self.edit_csv("request", lambda rows: (rows[0].update(date=first_date),
                                               rows[0].update(net_demand_mm="-1")))
        with self.assertRaisesRegex(ValueError, "request area, depth or efficiency"):
            check(self.root, self.locked)

    def test_gross_demand_and_stock_inconsistency_fail(self):
        key = "29030080/2019/label_date"
        record = self.request_document["scenarios"][key]
        record["unconstrained_gross_m3"] = "1"
        self.flush_manifests()
        with self.assertRaisesRegex(ValueError, "gross demand differs"):
            check(self.root, self.locked)
        record["unconstrained_gross_m3"] = self.request_document["scenarios"]["29030080/2022/label_date"]["unconstrained_gross_m3"]
        record["source_m3_by_ratio"]["0.35"] = "1"
        self.flush_manifests()
        with self.assertRaisesRegex(ValueError, "synthetic stock arithmetic differs"):
            check(self.root, self.locked)


if __name__ == "__main__":
    unittest.main()
