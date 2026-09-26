"""Download public source files listed for the water-allocation evidence audit."""

import argparse
import os
from pathlib import Path
import tempfile
from urllib.request import Request, urlopen


ROOT = Path(__file__).resolve().parent
SOURCES = {
    "data/raw/dane_cna2014_bolivar_csv.zip": "https://microdatos.dane.gov.co/index.php/catalog/513/download/8762",
    "reports/raw/dane_cna2014_questionnaire.pdf": "https://microdatos.dane.gov.co/index.php/catalog/513/download/8748",
    "reports/raw/dane_cna2014_ficha_metodologica.pdf": "https://www.dane.gov.co/files/investigaciones/fichas/agropecuario/ficha_metodologica_CNA-01_V4.pdf",
    "reports/raw/dane_cna2014_data_dictionary.xlsx": "https://microdatos.dane.gov.co/index.php/catalog/513/download/8750",
    "data/raw/agronet_eva_2007_2018.xlsx": "https://agronet.gov.co/sites/default/files/2025-08/Base%20Agr%C3%ADcola%20EVA%202007-2018_MADR.xlsx",
    "data/raw/upra_eva_2019_2024.xlsx": "https://upra.gov.co/sites/default/files/2025-07/20250617_BaseAgricola20192024.xlsx",
    "data/raw/upra_eva_2019_2025.xlsx": "https://upra.gov.co/sites/default/files/2026-05/20260526_BaseAgricola20192025.xlsx",
    "data/raw/upra_eva_2025_crop_calendar.xlsx": "https://upra.gov.co/sites/default/files/2026-08/20260526_ConsolidadoCalendariosEVA2025.xlsx",
    "reports/raw/upra_eva_2024_final.pdf": "https://upra.gov.co/sites/default/files/2025-10/01_EVAS_20250530.pdf",
    "reports/raw/adr_irrigation_district_operating_procedure.pdf": "https://www.adr.gov.co/wp-content/uploads/2021/07/Administracion-Operacion-y-Conservacion-de-los-Distritos-de-Adecuacion-de-Tierras.pdf",
    "data/raw/ideam_station_catalog.csv": "https://www.datos.gov.co/api/views/hp9r-jxuu/rows.csv?accessType=DOWNLOAD",
    "data/raw/nasa_power_maria_la_baja_2013_2024_daily.csv": "https://power.larc.nasa.gov/api/temporal/daily/point?parameters=PRECTOTCORR,T2M,T2M_MAX,T2M_MIN,RH2M,WS2M,ALLSKY_SFC_SW_DWN&community=AG&longitude=-75.3021802&latitude=9.984454&start=20130101&end=20241231&format=CSV",
}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true", help="replace existing downloads")
    args = parser.parse_args()
    for relative_path, url in SOURCES.items():
        destination = ROOT / relative_path
        if destination.exists() and not args.force:
            print(f"SKIP {relative_path}")
            continue
        destination.parent.mkdir(parents=True, exist_ok=True)
        temporary = None
        try:
            request = Request(url, headers={"User-Agent": "WellProdSim-evidence/1.0"})
            with urlopen(request, timeout=240) as response, tempfile.NamedTemporaryFile(
                dir=destination.parent, prefix=destination.name + ".", suffix=".download", delete=False
            ) as output:
                temporary = Path(output.name)
                while chunk := response.read(1024 * 1024):
                    output.write(chunk)
            if temporary.stat().st_size == 0:
                raise ValueError("empty download")
            os.replace(temporary, destination)
            print(f"OK {relative_path}")
        finally:
            if temporary is not None:
                temporary.unlink(missing_ok=True)


if __name__ == "__main__":
    main()
