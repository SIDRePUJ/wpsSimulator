# Evidence audit and minimum viable study

**Status (2026-09-26): evidence preparation only.** The public data support a constrained *counterfactual* design, but not an empirical claim about which allocation rule actually operated or improved household welfare in María La Baja.

## Measured coverage

The figures below are produced by `profile_sources.py` and stored in `data/derived/source_profile.json`; they refer to municipality code 13442 unless noted.

| Source | Directly observed in this package | Consequence |
| --- | --- | --- |
| [DANE CNA 2014](https://microdatos.dane.gov.co/catalog/513) | 3,630 UPA rows with unique composite keys; 12,867 crop rows, all linked to a UPA; 543 UPA report an irrigation district as a water source; 1,648 report water difficulty due to drought in 2013. | Heterogeneity and exposure are observable, **not** water quantities, legal entitlement, or allocation decisions. |
| CNA rice subset | 295 rice crop rows; 49 with positive 2013 yield; 39 positive-yield rows/UPA also reporting district-source access. These 39 sum to 293.18 harvested ha and 593.83 t (2.025 t/ha by aggregate division); only four match a household record. | A rice-only district-source sample is too thin for credible household-inequality validation. UPA crop outcomes are the measurable micro-level alternative. |
| CNA rice harvested-area distribution | A separate, identifier-free [derived aggregate](data/derived/rice_area_distribution.json) finds 49 UPA with positive rice harvested area (355.556 ha): 19 in (0,5] ha, 19 in (5,10] ha, and 11 above 10 ha. Among the 39 that self-report a district as one water source (293.184 ha), the corresponding counts are 13, 17, and 9. Input ZIP SHA-256 and field definitions are recorded in the aggregate. | These three classes can constrain **scenario area composition**, not specify individual plot geometry, farm entitlement, or a representative district population. The positive-area subset is selected: 246/295 rice crop rows have zero harvested area, including 70/109 district-source rice rows. Do not silently generalize the 39 UPA to all 543 UPA reporting district-source access. |
| [Agronet historical EVA](https://agronet.gov.co/documentacion-estadisticas/agricola/reporte-evaluaciones-agropecuarias-eva-y-anuario-estadistico) | María La Baja 2013 irrigated rice: 3,300 harvested ha and 22,800 t across A/B periods (6.909 t/ha). | **Major same-year discrepancy** versus CNA district-source rice (2.025 t/ha and 293.18 ha). Do not use both as interchangeable calibration targets. Different populations/definitions are plausible but not verified explanations. |
| [UPRA EVA 2024 final base](https://upra.gov.co/es-co/eva/eva-2024) | 125 local municipality/crop/period rows across 2019–2024; 2024 irrigated rice totals 1,500 harvested ha and 10,368 t (6.912 t/ha). | Municipal production context and an independent broad-range check, **not** validation of plot-level water allocation. Definitions for transitory crops change around 2022 from sowing period to effective harvest period; avoid naive pooled trends. |
| [UPRA EVA 2025 base](https://upra.gov.co/es-co/eva/eva-2025) | 153 local rows across 2019–2025; 2025 irrigated rice totals 1,672 harvested ha and 10,032 t (6.000 t/ha). | Secondary context only; recent years can be revised. |
| [IDEAM catalog](https://www.ideam.gov.co/transparencia/datos-abiertos/seccion-de-datos-abiertos/catalogo-nacional-de-estaciones-del-ideam) | 49 local station entries; four active pluviometric stations (Flamenco, Mampuján, Nueva Florida, Puerto Santander). | Status and installation dates **do not** establish time-series completeness; actual observations are pending. |
| [NASA POWER](https://power.larc.nasa.gov/docs/services/api/temporal/daily/) | Daily town-point precipitation/meteorology for 2013–2024, with no missing precipitation days in the returned grid series. | Gridded MERRA-2/CERES-based forcing candidate, not station rainfall or shared-source supply. |

The CNA record linkage uses `(P_MUNIC, UC_UO, ENCUESTA, COD_VEREDA)`. The DANE dictionary maps crop code `00113202001` to *Arroz verde*. The indicator `P_S11P124_SP10` is treated only as a report that an irrigation district is one water source; `P_S11P124_SP10=1` does not prove named-district membership. Units and questionnaire wording require rechecking before publication.

The privacy-safe area aggregate is reproducible with `python derive_rice_area.py` and is tested with `python -m unittest discover -p test_derive_rice_area.py`. It sums positive `AREA_COSECHADA` records by the CNA composite UPA key *before* classifying UPA into three bins; zero, missing and non-finite areas are not imputed. No keys or record-level areas are written. The raw CNA ZIP remains local and is **not** part of the server transfer. Bin thresholds are coarse to avoid tiny published cells; the smallest reported cell contains nine UPA.

## Minimum viable journal contribution

**Question.** Under a specified finite seasonal water budget and drought forcing, how do transparent rationing rules alter aggregate crop production and the inequality of *simulated UPA production losses* relative to crop-water need?

**Competing rules.** Compare (1) proportional-to-irrigation-demand rationing, (2) equal water per eligible hectare, and (3) a small-farm-protective rule with a capped minimum allocation. Use the same finite volume, eligibility set, crop mix, and weather in every paired scenario. The rule definitions and any eligibility thresholds must be preregistered before result inspection. A no-scarcity reference is a diagnostic, not a substitute for a rule comparison; existing irrigation on/off experiments are **not** the novelty.

**Primary outcomes.** Total simulated crop production (and yield relative to the no-scarcity reference) for efficiency; Gini and lower-quartile/upper-quartile ratio of simulated UPA production losses for equity. Report both absolute production and losses to avoid calling an equal percentage loss equitable when farm sizes differ. Also report water supplied/needed and rule feasibility (non-negative deliveries, total allocation no greater than budget). Household-income inequality is **not** supported by this microdata linkage.

**Minimum calibration/verification gates.**

1. Use CNA to specify observed distribution of UPA harvested area/crop mix and a broad yield heterogeneity envelope; use the rice subset only with its small-sample limitation. Do not tune a rice response curve to the 39 UPA and assert district-wide representativeness.
2. Select one clearly defined municipal EVA crop/period benchmark for external production plausibility, after reconciling the 2013 CNA/EVA discrepancy or explicitly reporting results under **separate** CNA-anchored and EVA-anchored interpretations. Never average them silently.
3. Obtain actual IDEAM daily station series through [DHIME](https://ideam.gov.co/dhime) and assess overlap, missingness, and bias versus POWER. Until then, use POWER only as an unvalidated forcing proxy and label drought years as *relative to that series*, not officially observed local drought.
4. Represent the shared seasonal water budget as a **scenario parameter**, not an estimated reservoir volume. Explore several preregistered scarcity ratios, e.g. available water / simulated unconstrained demand, rather than inventing a historical storage series.
5. Check the water-balance invariant and run paired stochastic seeds across all rules. Predefine uncertainty intervals and sensitivity to crop-water-response coefficients, rooting/soil capacity, irrigation efficiency, eligibility, and UPA weighting. Ranking is credible only if robust over defensible parameter ranges, not one hand-picked baseline.

**Required model changes, deferred.** Add one finite shared-source state/budget, demand requests, a rule interface that allocates water without overspending the budget, and traceable per-UPA water/production outputs. Preserve current irrigation on/off behavior as a regression baseline. No source-code changes or simulations are part of this evidence task.

## Validity threats and decision gate

- **Construct validity:** UPA-level production-loss inequality is not household welfare inequality. The four linked household records in the rice district-source subset cannot support that stronger claim.
- **External validity:** “District source” is self-reported access, not verified membership; a municipality is not the irrigation command area. Missing delivery volumes prevent a historically calibrated rule evaluation.
- **Temporal validity:** CNA 2013 drought difficulty, EVA production periods, and POWER 2013–2024 are not one harmonized panel; a 2022 EVA period-definition break and potential 2025 revisions matter.
- **Structural uncertainty:** Water availability, water-response functions, conveyance loss, and effective eligibility are unobserved; report sensitivity envelopes and avoid causal language.
- **Publication gate:** Before submission, resolve or transparently bracket the 2013 rice mismatch, validate meteorological forcing against station observations, and demonstrate stable qualitative trade-offs across plausible scarcity/response ranges. If that fails, frame the paper as a simulation-based methodological scenario analysis, not an empirically validated policy recommendation.

This package deliberately concentrates on **allocation institutions under a finite shared constraint**. It does not reuse the existing irrigation on/off comparison as a new contribution, and it does not claim to implement allocation yet.
