# Water-allocation evidence package

This directory supports a **proposed**, not yet executed, WellProdSim study of scarce shared irrigation water in María La Baja, Bolívar (municipality code 13442). It contains public-source acquisition instructions, an aggregate-only data profile, and an evidence audit. It does **not** contain observed allocation outcomes or a validated water-allocation model.

## Reproduce locally

1. From this directory, run `python fetch_sources.py` to retrieve the listed public files. Existing files are left intact; `--force` requests fresh copies. URLs may change at source institutions.
2. Run `python .\profile_sources.py` with Python and `openpyxl` installed. The script writes `data/derived/source_profile.json` and streams the census ZIP without exporting individual records.
3. Compare the `source_files` sizes and SHA-256 hashes in that JSON to the files on disk. The recorded inventory describes downloads acquired on **2026-09-26**; newer source revisions need a new audit.
4. Read [EVIDENCE.md](EVIDENCE.md) before choosing calibration targets or claiming empirical validation.

`data/raw/` and `reports/raw/` are intentionally Git-ignored. In particular, [DANE's CNA microdata access terms](https://microdatos.dane.gov.co/catalog/513) do not authorize redistribution of the individual-level archive through this repository. The aggregate profile is not a substitute for the original data or its documentation. Do not copy raw files into Overleaf.

## Source roles and provenance

| Files | Provider and primary link | Permitted role in this study |
| --- | --- | --- |
| `dane_cna2014_bolivar_csv.zip`, questionnaire, methodological sheet, dictionary | [DANE CNA 2014 catalog](https://microdatos.dane.gov.co/catalog/513); exact downloads in `fetch_sources.ps1` | Cross-sectional UPA/crop heterogeneity, reported water-source access, reported drought difficulty; **not** water volumes or district membership. The catalog's methodological-sheet download failed with HTTP 500, so the script uses the [official DANE sheet](https://www.dane.gov.co/files/investigaciones/fichas/agropecuario/ficha_metodologica_CNA-01_V4.pdf). |
| `agronet_eva_2007_2018.xlsx` | [Agronet historical EVA download listing](https://agronet.gov.co/documentacion-estadisticas/agricola/reporte-evaluaciones-agropecuarias-eva-y-anuario-estadistico) | Same-year municipal rice area/production comparison for 2013. Not a farm-level target. |
| `upra_eva_2019_2024.xlsx`, `upra_eva_2019_2025.xlsx`, crop calendar, final report | [UPRA EVA 2024](https://upra.gov.co/es-co/eva/eva-2024), [UPRA EVA 2025](https://upra.gov.co/es-co/eva/eva-2025) | Recent municipal crop/production context and seasonality. Use the 2024 final base for the principal recent comparison; treat 2025 as provisional. |
| `ideam_station_catalog.csv` | [IDEAM open-data station catalog](https://www.ideam.gov.co/transparencia/datos-abiertos/seccion-de-datos-abiertos/catalogo-nacional-de-estaciones-del-ideam) | Identify candidate rain gauges. This is **metadata**, not a precipitation time series. |
| `nasa_power_maria_la_baja_2013_2024_daily.csv` | [NASA POWER daily API](https://power.larc.nasa.gov/docs/services/api/temporal/daily/) | Gridded meteorological forcing candidate, at a town-point coordinate. Validate rainfall against actual gauges before calling it local observed rainfall. |
| `adr_irrigation_district_operating_procedure.pdf` | [ADR operating procedure](https://www.adr.gov.co/wp-content/uploads/2021/07/Administracion-Operacion-y-Conservacion-de-los-Distritos-de-Adecuacion-de-Tierras.pdf) | Institutional context and a possible route to delivery records; it does not prove records for this district are accessible. |

The exact URLs, including the NASA query and coordinate, are preserved in `fetch_sources.py`. The [IDEAM DHIME portal](https://ideam.gov.co/dhime) is a potential official route for station observations; no observation series has been acquired here. No reservoir storage, release, canal delivery, or plot-level allocation series has been acquired.

## Boundaries

- `source_profile.json` is a reproducible **descriptive audit**. Aggregates and counts should be checked against the source workbooks before publication.
- CNA (2014) and EVA (municipal series) have different units, definitions, and coverage. The observed discrepancy in [EVIDENCE.md](EVIDENCE.md) is a calibration gate, not a result to smooth away.
- A census UPA reporting an irrigation district as one water source is not proven to belong to a named district, to have received water in 2013, or to share a particular reservoir.
- No simulation has been run and no causal effect or policy ranking has been estimated.
