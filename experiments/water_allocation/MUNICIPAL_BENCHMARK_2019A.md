# Municipal rice benchmark: María La Baja, 2019A

**Decision:** use municipality × irrigated rice × 2019A as the first *observed production-scale comparator*, while keeping the finite-source allocation experiment at its explicitly synthetic UPA/plot scale. This is a benchmark definition, **not** a calibrated municipal simulation or validation result. It was recorded before any guarded 2019 multi-date allocation outcomes were inspected; the observed EVA value itself is known and must not be tuned against and then reused as an independent test.

## Observed target and provenance

| Measure | EVA 2019A, María La Baja, `Arroz riego` |
| --- | ---: |
| Sown area | 1,148.0 ha |
| Harvested area | **1,147.5 ha** |
| Production | **3,591.68 t** |
| Yield | **3.13 t/ha** (production / harvested area, rounded) |

Values are the municipality/crop/period row in the locally audited [UPRA EVA 2019–2024 profile](data/derived/source_profile.json), derived from `data/raw/upra_eva_2019_2024.xlsx` (SHA-256 `9143ae74790be059eff6fd671f4a2219284caef68a1384cb3cc245da16a36814`). [UPRA's EVA 2019 page](https://upra.gov.co/es-co/eva/eva-2019) identifies municipal, semester-specific area, production and yield outputs. The separate 2019B row is 155.5 harvested ha, 1,057.40 t and 6.80 t/ha; **do not pool the two semesters** for this first-period comparison.

## Alignment gate before any model–observation error

| Requirement | Current evidence | Gate |
| --- | --- | --- |
| Crop and period | All 24 eligible model rice plots are planted in the first semester: 12 on 1 February and 12 on 13 April (48 ha each). Technical harvests are 1 June and 11 August. [UPRA's cross-semester FAQ](https://upra.gov.co/es-co/atencion-al-ciudadano/preguntas-frecuentes) says it follows the sown area even when harvest crosses semesters. | **Provisional period-A alignment by sowing, not a verified match to the local 2019 rice calendar.** Check the EVA 2019 crop calendar and the exact period definition before scoring yield. Do not discard the August harvest merely because it falls in semester B. |
| Weather | The 2019 [NASA POWER rainfall fixture](data/derived/power_rainfall_2019_as_2022.csv) maps 2019 month/day precipitation to the model's 2022 calendar; the Feb 1–Aug 11 window contains 479.57 mm. Other meteorological drivers remain seeded simulator output. | Only rainfall year is aligned. Compare against quality-controlled local IDEAM daily series before claiming local-weather validation; non-rainfall drivers require their own alignment or a disclosed uncertainty envelope. |
| Population and hectares | The model has 12 invented UPA, 24 eligible plots and **96 ha**. The observed 2019A municipal harvested area is 1,147.5 ha (11.953125 times larger). The available CNA 2013 rice district-source subset has 39 selected UPA/293.184 ha and is neither a 2019 municipal farmer frame nor a water-command-area map. | **No municipal expansion weight is justified.** Do not multiply the model's tonnes or water stock by 11.953125, and do not call its UPA inequality municipal. Compare t/ha only as a conditional plausibility diagnostic until a defensible municipal population/area frame is acquired. |
| Water regime and crop response | The registered 2019 stocks (0.35/0.65 of synthetic gross request) are **206,164/382,876 m3**, not observed municipal or district deliveries. `Ym=7 t/ha`, `Ky=1` and delivery efficiency 0.48 are scenario assumptions; the model omits flooded-paddy ponding and percolation. | No historical water-regime match or external water–yield validation. Report simulated yield conditional on each explicit stock and parameter vector, not an EVA prediction error or a calibrated municipal policy effect. |

### Departmental 2019 crop-calendar check

The official [UPRA EVA 2019 Bolívar sowing/harvest calendar](https://upra.gov.co/sites/default/files/2025-04/Calendario%20siembras%20y%20cosechas%20departamental%202019%20v2.xlsx) was downloaded read-only to gitignored `data/raw/upra_calendar_departmental_2019.xlsx` (260,943 bytes; SHA-256 `6f1c14b1ce08dcb1f3837a025672347269d722af4610477fb9bb79b6d4c55521`). Its `Bolívar / ARROZ` sowing shares sum to approximately 100% over the year. February is **1.134%** and April **24.844%** of annual departmental rice sowings; among first-semester sowings (53.528% of annual), these are about **2.12%** and **46.41%**. The synthetic cohort assigns **50% of its eligible area to February and 50% to April**. The departmental harvest shares are 3.574% in June and 13.363% in August, while the model harvests half its area in each month.

This is evidence that the model's two-date **50/50 calendar is not supported by the available departmental pattern**. It does **not** quantify a María La Baja irrigated-rice mismatch: the UPRA calendar aggregates all rice and all municipalities in Bolívar, and its monthly sowing/harvest marginal shares do not link each harvest to a sowing cohort. Do not reweight the simulation to this departmental row and call it a municipal calibration. The missing evidence is a municipality- and irrigation-system-specific 2019 sowing/harvest schedule. Until obtained, period-A alignment remains provisional even though both model sowings occur in semester A.

### District-specific historical calendar check

A more relevant, but still retrospective, source is Barón Valbuena and Mogollón Gómez, ["Distritos de riego, soporte de la producción arrocera en Colombia," *Revista Arroz* 69 (May–June 2021), p. 34](https://fedearroz.com.co/documents/286/Revista_552.pdf). Drawing on the DANE–Fedearroz–FNA mechanized-rice survey's district records for 2000–2020, it describes rice planting **inside the María La Baja irrigation district** as occurring in January–March of the first semester, conditional on reservoir levels. The local read-only copy is gitignored at `data/raw/fedearroz_revista_552.pdf` (12,011,180 bytes; SHA-256 `e666a445820337d6b8dc38fcd48a013ec4c44839e6507bff2861385306747945`).

This narrower population changes the interpretation of the departmental comparison: a **February** planting is consistent with the reported district window despite February's small department-wide all-rice share; the model's **April** planting on 48 of 96 eligible hectares is not supported by that historical district pattern. Neither source establishes actual plot-level dates or reservoir levels in 2019 or 2022. Thus the present February/April cohort and its IDEAM-conditioned requests remain **technical scenarios**, not a locally validated district crop calendar. Before paper-facing rule comparisons, either construct and verify a January–March district-style cohort without changing it based on rule outcomes, or retain the current cohort only as an explicitly off-calendar sensitivity. Do not apply a district-specific allocation interpretation to all municipal rice hectares.

## Minimum comparison output

After the 2019 three-rule technical cells pass the existing strict raw-output join, report each scenario's modeled `sum(actual_t) / sum(area_ha)` beside the **3.13 t/ha observed EVA benchmark**, with its 96-ha model support, rule, stock ratio, seed, weather source and uncertain parameters. Treat the signed numerical difference as a **descriptive gap**, not a prediction error or goodness-of-fit statistic, while any alignment gate above remains open. Never report `sum(actual_t)` as municipal production. Preserve the 2019B row as a separate period, not an opportunistic alternative target.

### First guarded 2019 technical result (added after benchmark lock)

The 2019 POWER/r0.35/seed12345 runs were executed after this benchmark was committed as `d57d8ef`. All three rules exited 0, conserved the **206,164-m3 synthetic source**, and passed farm 12/12, water 48/48 with 324 positive deliveries and no missing delivery, yield 24/24, and climate 24/24 with 2,904 gap-free rows. All three climate files have the same SHA-256, `dc0c3596852446f151a0e3b32a9a990066214559f9f1457ec24b9170642afeac`. The strict manifest `world24-weekly-2019-r035-three-run-manifest.json` under the project-level `results/water-allocation-server-20260927/` directory admitted 72 eligible plot rows and 12 synthetic UPA per rule. This is **12 of 24 technical runs** across the full register, not a completed sensitivity study.

| Rule | Modeled production on 96 ha | Modeled t/ha | Difference from EVA 2019A's 3.13 t/ha |
| --- | ---: | ---: | ---: |
| Proportional demand | 523.605 t | 5.454 | +2.324 t/ha |
| Equal per hectare | 518.755 t | 5.404 | +2.274 t/ha |
| Small-plot floor | 523.228 t | 5.450 | +2.320 t/ha |

**Interpretation:** the modeled yield is substantially above the municipal EVA 2019A figure under every rule, so this is an **unresolved external-plausibility warning**, not evidence that one rule was used historically. The numerical difference is *not* a validation error: the municipality has 1,147.5 observed harvested ha versus the synthetic 96 ha; actual 2019 source deliveries, non-rainfall weather, management, soil and paddy-water processes are not matched, and period-A crop-calendar alignment remains provisional. Do not tune `Ym`, `Ky` or source ratio to erase this gap and then report EVA 2019A as independent validation. Preserve both the gap and the model-version/assumption ledger for the paper.

For a stronger municipal production claim, first obtain an independently documented 2019 irrigated-rice area/UPA distribution and local crop calendar, then construct a representative or explicitly weighted municipal cohort with weights fixed *before* viewing rule outcomes. To validate the finite-source institution itself also requires the water-command boundary and observed source releases/deliveries; municipal EVA alone cannot supply them. If those data remain unavailable, publish the allocation contrast as a **conditional simulation for synthetic water users**, with municipal yield as a limited external plausibility reference.
