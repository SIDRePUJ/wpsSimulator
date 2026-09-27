"""Export aggregate CNA rice-area evidence without exporting UPA identifiers."""

import csv
import hashlib
import io
import json
from pathlib import Path
import zipfile


MUNICIPALITY = "13442"
RICE_CODE = "00113202001"  # DANE CNA 2014 dictionary: Arroz verde.
KEY_FIELDS = ("P_MUNIC", "UC_UO", "ENCUESTA", "COD_VEREDA")
BIN_LABELS = ("(0,5]", "(5,10]", "(10,+inf)")
ROOT = Path(__file__).resolve().parent


def upa_key(row):
    return tuple(str(row.get(field, "")).strip() for field in KEY_FIELDS)


def positive_area(row):
    try:
        area = float(row.get("AREA_COSECHADA", ""))
    except (TypeError, ValueError):
        return None
    return area if 0 < area < float("inf") else None


def summarize(areas):
    """Aggregate positive harvested hectares by UPA before binning."""
    counts = {label: 0 for label in BIN_LABELS}
    for area in areas.values():
        label = BIN_LABELS[0] if area <= 5 else BIN_LABELS[1] if area <= 10 else BIN_LABELS[2]
        counts[label] += 1
    return {
        "upa_count": len(areas),
        "positive_harvested_area_ha": round(sum(areas.values()), 6),
        "upa_count_by_harvested_area_ha": counts,
    }


def derive(archive):
    district_source = {}
    with zipfile.ZipFile(archive) as source:
        with io.TextIOWrapper(source.open("S01_15_Unidad_productora_.csv"), encoding="utf-8", newline="") as stream:
            for row in csv.DictReader(stream):
                if row["P_MUNIC"] == MUNICIPALITY:
                    key = upa_key(row)
                    if not all(key) or key in district_source:
                        raise ValueError("Missing or duplicate local UPA composite key")
                    district_source[key] = row.get("P_S11P124_SP10") == "1"

        areas_all = {}
        areas_source = {}
        rice_rows = rice_rows_source = positive_rows = positive_rows_source = 0
        unmatched = 0
        with io.TextIOWrapper(source.open("S06A_Cultivos_.csv"), encoding="utf-8", newline="") as stream:
            for row in csv.DictReader(stream):
                if row["P_MUNIC"] != MUNICIPALITY or row.get("P_S6P46") != RICE_CODE:
                    continue
                rice_rows += 1
                key = upa_key(row)
                if key not in district_source:
                    unmatched += 1
                    continue
                has_source = district_source[key]
                rice_rows_source += has_source
                area = positive_area(row)
                if area is None:
                    continue
                positive_rows += 1
                areas_all[key] = areas_all.get(key, 0.0) + area
                if has_source:
                    positive_rows_source += 1
                    areas_source[key] = areas_source.get(key, 0.0) + area

    digest = hashlib.sha256(archive.read_bytes()).hexdigest()
    return {
        "source": {"file": archive.name, "sha256": digest, "municipality_code": MUNICIPALITY,
                   "crop_code": RICE_CODE, "area_field": "AREA_COSECHADA", "district_source_field": "P_S11P124_SP10"},
        "selection": "Rice crop rows in municipality with positive finite harvested area; aggregate by CNA UPA composite key. District-source means self-report of one water source, not verified district membership.",
        "counts": {"rice_crop_rows": rice_rows, "rice_rows_reporting_district_source": rice_rows_source,
                   "positive_area_rice_rows": positive_rows, "positive_area_rice_rows_reporting_district_source": positive_rows_source,
                   "unmatched_rice_rows": unmatched},
        "all_positive_area_rice_upa": summarize(areas_all),
        "positive_area_rice_upa_reporting_district_source": summarize(areas_source),
        "limitations": "Harvested area is not owned area, physical plot geometry or water entitlement. Zero-area records are excluded, not imputed. The district-source subsample is selected and small. No UPA identifiers or record-level rows are exported.",
    }


def main():
    archive = ROOT / "data" / "raw" / "dane_cna2014_bolivar_csv.zip"
    output = ROOT / "data" / "derived" / "rice_area_distribution.json"
    result = derive(archive)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(result, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(output)


if __name__ == "__main__":
    main()
