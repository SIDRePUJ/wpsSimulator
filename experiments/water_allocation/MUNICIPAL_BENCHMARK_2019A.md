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

## Minimum comparison output

After the 2019 three-rule technical cells pass the existing strict raw-output join, report each scenario's modeled `sum(actual_t) / sum(area_ha)` beside the **3.13 t/ha observed EVA benchmark**, with its 96-ha model support, rule, stock ratio, seed, weather source and uncertain parameters. Treat the signed numerical difference as a **descriptive gap**, not a prediction error or goodness-of-fit statistic, while any alignment gate above remains open. Never report `sum(actual_t)` as municipal production. Preserve the 2019B row as a separate period, not an opportunistic alternative target.

For a stronger municipal production claim, first obtain an independently documented 2019 irrigated-rice area/UPA distribution and local crop calendar, then construct a representative or explicitly weighted municipal cohort with weights fixed *before* viewing rule outcomes. To validate the finite-source institution itself also requires the water-command boundary and observed source releases/deliveries; municipal EVA alone cannot supply them. If those data remain unavailable, publish the allocation contrast as a **conditional simulation for synthetic water users**, with municipal yield as a limited external plausibility reference.
