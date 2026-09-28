# Calendar gate for the observed-rainfall allocation study

**Decision:** do not promote the existing February/April rice cohort or its 16 IDEAM-conditioned request schedules from technical fixtures to paper-facing María La Baja district scenarios. First verify a research-only planting calendar within the historically reported January–March district window, while keeping exact 2019/2022 planting dates and reservoir levels explicitly unknown.

## Evidence and claim boundary

| Evidence | Supports | Does not support |
| --- | --- | --- |
| [Fedearroz–FNA, *Revista Arroz* 69 (2021), p. 34](https://fedearroz.com.co/documents/286/Revista_552.pdf), summarizing ENAM district records from 2000–2020 | María La Baja district rice planting was reported in January–March of the first semester, contingent on reservoir water. | A measured monthly share, actual planting day, or reservoir stock for each 2019/2022 user. |
| [UPRA EVA 2019 Bolívar departmental rice calendar](https://upra.gov.co/sites/default/files/2025-04/Calendario%20siembras%20y%20cosechas%20departamental%202019%20v2.xlsx) | All-rice departmental planting shares include February 1.134% and April 24.844%. | Calendar frequencies for María La Baja's district irrigators; it combines municipalities and rice systems. |
| [UPRA EVA municipal agricultural base](https://upra.gov.co/es-co/eva/eva-2019) | María La Baja irrigated-rice 2019A reported 1,147.5 harvested ha and 3.13 t/ha. | Plot dates, plot-water deliveries, or a district-only population frame. For transitory crops, EVA's harvested-output period definition changes in 2022; do not pool 2019A and 2022A as one directly matched outcome period. |
| [IDEAM local daily precipitation](https://www.ideam.gov.co/sites/default/files/transparencia/planeacion/respuesta_radicado_no_20249050054814_gsc.pdf) | Four stations offer observed 2019 and 2022 rainfall inputs, with explicit station/date-label sensitivity. | Historical reservoir inflow, canal delivery, or all-farm rainfall. |

The frozen technical cohort has 12 synthetic UPA, 24 eligible plots and 96 rice ha: 48 ha plant on 1 February and 48 ha on 13 April, each with an approximately 120-day technical crop window. February falls inside the reported district window; the April half does not. This is a **population/calendar mismatch**, not a reason to erase the existing technical runs or tune their dates after viewing allocation outcomes.

## What the weather contrast can show

The table below sums the four locked `label_date` IDEAM rainfall fixtures. It is a **diagnostic for a candidate January–July crop envelope**, not a climatological normal or a finalized crop-window selection; the one-day date-label alternative remains unresolved.

| IDEAM station | Jan–Mar 2019 / 2022 (mm) | Jan–Jul 2019 / 2022 (mm) |
| --- | ---: | ---: |
| Puerto Santander | 27.0 / 76.8 | 703.9 / 985.3 |
| Flamenco | 10.0 / 123.0 | 469.0 / 1,046.0 |
| Mampuján | 131.0 / 471.0 | 497.0 / 1,352.0 |
| Nueva Florida | 44.7 / 167.3 | 695.4 / 1,159.0 |

All four gauges show more rain in 2022 than 2019 in both candidate envelopes. This does **not** classify 2019 as an official drought or imply a historical shared-source shortage. The wide between-station spread remains an input-validity threat. The prior 1 February–11 August comparison was defined around the old crop cohort; do not reuse it automatically after changing crop timing.

## Minimum implementation and verification gate

1. **Declare the population.** Model a synthetic group of potential district-water users *within* María La Baja, not every municipal rice producer or a verified sample of district members. Preserve the 12-UPA, 4/5/3 rice-area-class composition unless independently justified evidence changes it.
2. **Control planting in the model, not only in the request CSV.** `PrepareLandTask` changes a plot from `NONE` to `PLANTING` after work is completed; `PlantCropGoal` and `PlantCropTask` then initiate the crop on the agent's date. A shifted external request cannot move those agent events. Implement an opt-in research calendar mechanism and audit its actual `WATER_PLANT`, harvest and plot-owner traces before generating new requests. Keep the legacy path unchanged.
3. **Predeclare dates before outcomes.** Fedearroz supports a January–March *window*, not a day or monthly distribution. Choose one transparent within-window technical calendar before inspecting rule outcomes, and test earlier/later within-window dates as sensitivity. Do not call these dates observed 2019/2022 plantings.
4. **Rebuild inputs as one matched unit.** After crop dates are verified, regenerate plot windows and all rainfall-conditioned weekly requests. Reject any request preceding its plot's planting or continuing beyond maturity/harvest. Recompute gross m³ and each 0.35/0.65/1.00 scenario stock from that same schedule; old hashes and stock amounts must not be reused.
5. **Pair rules and report limitations.** Within a station/year/date-mapping/scarcity/seed cell, all three rules use identical plots, dates, rainfall, non-rainfall climate draws, requests and finite source stock. Report UPA-level production-loss inequality, total rice tonnes, and water conservation only after the existing strict join passes. Without observed releases/deliveries and crop-response calibration, results remain conditional simulations, not historical rule validation.

**Release criterion for the paper matrix:** the new cohort passes identical-repeat and three-rule pairing checks with zero out-of-window plantings, all expected UPA/plots and areas, complete rainfall hashes, physically consistent requests, and no missing water/harvest/climate audit rows. If the model cannot produce that cohort without changing other behavior, stop and report the technical limitation rather than relabeling the April cohort as locally validated.

## Research switch (implementation, not validation)

`-Dwps.water.districtRiceCalendar=true` is an opt-in first-planting guard. It requires both physical-water mode and `-Dwps.water.riceOnlyCohort=true`; default/legacy behavior is unchanged. The first planting of each plot (initial `LandInfo` version 1) may be prepared and started in January–March. Later versions retain their technical seasons. This is only a **window constraint**: it does not force a specific day or establish reservoir availability. The two-run cohort trace and matched-input gates have now passed for one synthetic seed; see the [C2 audit](LOCAL_CALENDAR_C2_AUDIT.md). Full IDEAM-forced agent runs and paired allocation rules remain untested, so these prepared requests are **not yet paper results**.
