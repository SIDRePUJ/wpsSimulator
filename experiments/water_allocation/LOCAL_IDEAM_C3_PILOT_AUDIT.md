# C3 local IDEAM rule pilot: technically admitted, not a paper result

**Decision:** one predeclared three-rule cell passed natural execution, the unchanged strict raw-to-plot join, UPA aggregation, and an independent identity/water-balance check. It demonstrates that the district-calendar pipeline can produce a paired **technical diagnostic**. It does **not** validate historical irrigation deliveries, source storage, yield, the representativeness of one station/year/seed, or a superior allocation institution.

## Frozen cell and replay path

| Field | Locked value |
| --- | --- |
| Weather | IDEAM station `29030080`, source year 2019, `label_date` mapped to simulated 2022 days |
| Finite source | 0.65 × 696,560 m³ synthetic unconstrained requests = **452,764 m³**; not measured reservoir stock |
| Rules | `PROPORTIONAL_DEMAND`, `EQUAL_PER_HECTARE`, `SMALL_PLOT_FLOOR` (actual Java enum names) |
| Cohort and seed | 12 synthetic UPA; 24 first-version eligible rice plots / 96 ha; seed 12345; one 2022 year |
| Model | physical-water, rice-only, district calendar, `SEASONAL_ENTITLEMENT`, `Ym=5 t/ha`, `Ky=1`, delivery efficiency 0.48 in the request fixture |
| Launcher | `-env local -mode web -agents 12 -world 24 -land 2 -years 1 -startyear 2022 -seed 12345 -perturbation none` |

The ignored raw root is `reports/raw/pilot-c3-29030080-2019-label-065/`. Its `predeclared_setup.json` was written before the first run (SHA-256 `3c59216e07fa77194026270d675532ee5b76418bf7148fc177e0ebbf81f61e95`). Run the preserved `run_pilot.py` only in a **new empty directory**: it refuses to overwrite any rule run. Each `rule/argv.json` contains the complete Java executable, classpath and argument vector; `rule/stdout.txt`, `stderr.txt`, `exit.txt`, `audit.csv`, `yield.csv`, and `climate.csv` retain the raw outcome. Direct process-file redirection, not a PowerShell captured-output buffer, preserved complete logs. The C2 compiled `ResearchCropPolicy.class` SHA-256 remained `671aa78c79a4ac9dfe0f27068f0648aca8edbe7a9003cc3d7c93b5a29bd79f7a`.

Input identities: rainfall SHA-256 `62ccee0ee53cd62e0a4c61d792ed46bd764b3b0b578744a2ac3b698bb1835a91`; matched request SHA-256 `5a4d9ff9996581cc3bde58407c1394efb158bb93f9a29b325300a39c4222ba2b`; farm manifest SHA-256 `eaf2d0fd34e72c18d3def0e2a89f8cc44f86ec7e4ba789d187cca884ad24ab3c`; synthetic world SHA-256 `244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c`. Daily IDEAM-derived rainfall and requests remain ignored, not redistributed in Git. The committed aggregate request manifest separately records their provenance and the 452,764 m³ stock.

## Admission gates

1. `python experiments/water_allocation/build_joined_results.py experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/run_manifest.json --output experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/joined.csv` admitted **72 plot rows** (24 per rule). Its unchanged contract rejects nonzero exits, missing/duplicate farm-water-yield-climate audits, request/water/crop/rain mismatches, over-delivery, source imbalance, unpaired inputs or forcing, and plot/owner/area/full-yield mismatches. Manifest SHA-256 `6aec90ea1bf32061f7ef312b6371a2c38da975ef067343252aef27ec7b426a3a`; joined CSV SHA-256 `b0e6d915836578b2a3e6194f996642917a4012cc8e35501bcff0beed64f525f3`.
2. `python experiments/water_allocation/analyze_upa_results.py experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/joined.csv --output experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/upa_report.json` produced 12 UPA rows per rule (report SHA-256 `462d42ccf40c9c928bad0f8acd72d20fec3d27e9d053f3ac2f8e7627309ba974`). Its inequality unit is **UPA**, not plot.
3. `python experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/independent_audit.py` passed: all three natural exits 0; 48 plant records/24 first-version eligible plots/96 ha/12 UPA/24 harvests per rule; canonical plot-owner-area-plant-harvest roster unchanged from C2 and across rules; 4/5/3 UPA classes; exactly paired climate bytes; 408 water/request rows; withdrawal 452,764 m³ per rule within `10^-6` m³ numeric tolerance. Independent audit JSON SHA-256 `665659947d4cf4d409698b7c324dcedda7c2bfba122e4e463ff1e5f67b0f3115`. Initial audit-script failures compared `1` with `1.0` text and a floating sum of `452763.99999999994` against integer stock; only the independent check was corrected to compare numeric area and allow `10^-6` m³. No simulation, input, or result was changed or rerun in response.

All three climate CSVs are byte-identical (SHA-256 `1cfe24a2bd2bd1381af59ef6b6461814793b8561ee5fd9b22c9876054a5f3e69`), with **2,904 daily rows**, 24/24 observed eligible plots and zero gaps/duplicates. Farm audits assign 12/12; water audits register 48/48 with zero missing; yield audits harvest 24/24. Water and yield CSVs differ by rule as expected. Full per-rule hashes and observed decimal withdrawal totals are in ignored `independent_audit.json`.

## Descriptive contrast only

| Rule | Rice production (t) | Gini of UPA relative loss | Gini of UPA absolute loss (t) | P90 loss among smallest-area UPA |
| --- | ---: | ---: | ---: | ---: |
| `PROPORTIONAL_DEMAND` | 402.204 | 0.0054 | 0.3572 | 0.1627 |
| `EQUAL_PER_HECTARE` | 401.653 | 0.0053 | 0.3572 | 0.1638 |
| `SMALL_PLOT_FLOOR` | 402.072 | 0.1032 | 0.3904 | 0.0896 |

Against proportional demand, equal-per-hectare changes production by **−0.550 t** and relative-loss Gini by **−0.000094**; the plot-floor rule changes production by **−0.132 t**, relative-loss Gini by **+0.09784**, but smallest-area-UPA P90 relative loss by **−0.07308**. This is a real distributional tension *inside this synthetic cell*, not a general equity-efficiency frontier or a statistical result. The floor is awarded by **plot area**, not verified UPA entitlement; its label must not imply an observed district allocation practice. Gini of losses and smallest-UPA tail loss answer different questions and may move in opposite directions. A single seed and one weather/date interpretation provide no uncertainty interval.

## External plausibility check, not validation

The independently recorded [UPRA EVA 2019A municipal benchmark](MUNICIPAL_BENCHMARK_2019A.md) is **3.13 t/ha** for María La Baja irrigated rice on **1,147.5 harvested ha**. Dividing each strict UPA report's 24-plot production total by its **96 simulated eligible ha** gives:

| C3 rule | Modeled production on 96 ha (t) | Modeled yield (t/ha) | Signed gap from rounded EVA 2019A (t/ha) |
| --- | ---: | ---: | ---: |
| `PROPORTIONAL_DEMAND` | 402.203693 | 4.189622 | +1.059622 |
| `EQUAL_PER_HECTARE` | 401.653285 | 4.183888 | +1.053888 |
| `SMALL_PLOT_FLOOR` | 402.071556 | 4.188245 | +1.058245 |

These positive gaps are an **unresolved external-plausibility warning**, not prediction errors or a goodness-of-fit score. The modeled plots are an invented 96-ha district-water-user subset, not a sample or expansion of all 1,147.5 municipal irrigated-rice hectares; actual 2019 plot dates, areas, management and water deliveries are not matched. IDEAM 2019 rainfall was mapped onto a **2022 simulation calendar**, while other climate drivers remain synthetic. The finite source, request depth, `Ym=5 t/ha`, `Ky=1`, soil/paddy-water processes and farm management have not been calibrated to those EVA observations. Do not tune `Ym`, `Ky` or the stock to erase this gap and then reuse EVA 2019A as independent validation. Nor should modeled tonnes be read as municipal production.

## What still blocks paper claims

- IDEAM rain forcing is observed at a station, but its date-label interpretation and spatial representativeness remain uncertain. Only rainfall was forced; ET, temperature and radiation remain simulator-generated, seeded inputs.
- The 0.65 stock ratio, weekly crop demand, `Ym=5 t/ha`, `Ky=1`, 0.48 efficiency, homogeneous rice cohort, synthetic UPA geometry and planting dates are **assumptions**. No observed district deliveries, reservoir trajectory, on-farm crop calendar or UPA yields calibrate them yet.
- One 2019 station-year cannot itself be called drought. No full station-year/date-mapping/stock/seed/parameter sensitivity matrix has been run. Do not promote this pilot's signs, ranks or small differences to paper conclusions.
- Next: predeclare the minimum **balanced** replication and sensitivity design with all source-year/station/date interpretations needed for robustness, obtain or explicitly disclose missing district-water and yield observations, then run paired rules and report both UPA tail loss and inequality alongside tonnes. Revisit whether the plot-floor rule corresponds to the paper's intended **UPA-level** equity institution before interpreting it as such.
