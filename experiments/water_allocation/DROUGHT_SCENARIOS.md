# Preselected rainfall-forcing contrast

**Scope note (2026-09-28):** the 2014/2019 POWER comparison below is the **historical technical-screen protocol**, not the recommended locally grounded paper contrast. Its 12 completed runs and pre-outcome selection remain auditable; they must not be deleted or relabeled as station-observed drought effects. The evidence-led replacement proposed below is not yet an executed or frozen experiment.

## Proposed evidence-led replacement: 2019 versus 2022

Use **2019 as the lower-rainfall case and 2022 as the higher-rainfall case**, conditional on the station-quality and forcing-construction gates below. This pair was selected after viewing some 2014/2019 technical outcomes, but from [independent IDEAM rainfall](https://www.ideam.gov.co/sites/default/files/transparencia/planeacion/respuesta_radicado_no_20249050054814_gsc.pdf), not because of a favorable allocation-rule result. The change is an explicit **post-pilot amendment**, not a claim of original preregistration. The 2019A [municipal EVA yield benchmark](MUNICIPAL_BENCHMARK_2019A.md) remains a descriptive plausibility check; 2022 rainfall does not by itself provide a matched yield-validation target, particularly across the EVA period-definition break.

The acquired [IDEAM national daily precipitation archive](https://bart.ideam.gov.co/PQRS/AQTSUtils/PrecipitacionNacionalDiaria.zip) is gitignored at `data/raw/ideam_precipitacion_nacional_diaria.zip` (SHA-256 `FA695160A154A7CE9C95DEE736535A5A8CD9BBCFAFE5A6AA3DACE13A9BD03E1B`). All four local pluviometric stations have **365/365 daily dates in each of 2019 and 2022**, and **192/192 dates** in the fixed 1 February–11 August analysis window:

| Station | 2019 window (mm) | 2022 window (mm) |
| --- | ---: | ---: |
| Puerto Santander (`29030080`) | 740.2 | 1,103.3 |
| Flamenco (`29030160`) | 539.0 | 1,121.0 |
| Mampuján (`29030780`) | 527.0 | 1,382.0 |
| Nueva Florida (`29035040`) | 750.5 | 1,227.9 |

Every station is wetter in 2022 than in 2019. This is a **within-source observed rainfall contrast**, not an official drought classification, measured reservoir inflow, or evidence that the two years had the same water deliveries. The original POWER grid totals for the same window are 479.57 and 771.10 mm, respectively. The more detailed provisional station audit is in [EVIDENCE.md](EVIDENCE.md#provisional-ideam-rainfall-check). Fully observed 2018 may later serve as an intermediate-rainfall robustness case, but it is **outside the minimum pair** and must not be added based on policy outcomes.

**Gate before running rules:** (1) verify station metadata, quality status, units, duplicates, missing dates and suspicious extremes; (2) resolve the ZIP timestamp against the [IDEAM description of a pluviometric day (07:00–07:00)](https://ideam.gov.co/sites/default/files/transparencia/planeacion/respuesta_radicado_no_20259050177384_sm.pdf), which does not itself identify whether the ZIP's stamped date denotes the interval start or end; (3) predefine the spatial forcing choice and, if the date label remains ambiguous, retain both one-day mappings as timing sensitivity rather than selecting one by model fit; (4) verify the prepared 365-day fixture hashes and generate rainfall-conditioned weekly requests for each chosen forcing, fixing each request schedule and scarcity ratio **across allocation rules**; (5) only then run paired seeds and report production/inequality sensitivity. Existing 2014 and 2019 POWER runs remain technical diagnostics and are not mixed with the new station-forced study as if they shared one weather provenance.

**Preparation status:** `python prepare_ideam_rainfall.py` validates the locked source ZIP and creates 16 station/year/date-label daily input variants under gitignored `data/raw/ideam_derived/`. The committed [aggregate and SHA-256 manifest](data/derived/ideam_rainfall_manifest.json) permits byte-for-byte local regeneration without redistributing daily station values. `label_date` assigns the record stamped at 07:00 on D to D; `previous_day` assigns the record stamped at 07:00 on D+1 to D. Both are **interpretations**, not an IDEAM-confirmed mapping. These prepared files have not been used in an agent simulation, and no station or date interpretation has been designated the primary forcing. The remaining station-quality, spatial-support and paired-request gates above still apply.

## Historical POWER technical screen

**Decision:** compare a lower-tail rainfall proxy from 2014 with a near-median proxy from 2019, using the same 2022 simulation calendar, farm cohort, non-rainfall climate generator, irrigation requests, source budget and seed within each paired contrast. This is a **rainfall-only scenario contrast**, not an observed drought classification, reconstructed historical farm year, or measured district-water balance.

## Evidence and selection

The local [NASA POWER daily point series](https://power.larc.nasa.gov/docs/services/api/temporal/daily/) covers 2013–2024 near María La Baja. Its `PRECTOTCORR` parameter is corrected gridded MERRA-2 precipitation in mm/day, **not** an IDEAM station measurement. [NASA's precipitation methodology](https://power.larc.nasa.gov/docs/methodology/meteorology/precipitation/) describes comparisons and limitations relative to surface observations. The source CSV SHA-256 is `93118ab56cdba3f3847108f782802d46d74f41f11fbe3d75d6f34089d722b8bd`.

The selection window is **1 February–11 August**, spanning the first-season planting and latest eligible harvest observed in the fixed three-family pilot. In the 12 available years, 2014 is the lowest window total (**114.00 mm**, rank 1/12); 2019 is near the sample median (**479.57 mm**, rank 7/12) and nonleap. Selection precedes rainfall-policy outcome inspection. The complete annual ranking and exact source/output hashes are in [`data/derived/power_rainfall_scenarios.json`](data/derived/power_rainfall_scenarios.json).

## Reproducible transformation

Run `python prepare_power_rainfall.py` to validate every daily input, reject NASA's `-999` missing-value sentinel, and map each selected nonleap source year's month/day to the common 2022 calendar. The two resulting 365-row files are `power_rainfall_2014_as_2022.csv` and `power_rainfall_2019_as_2022.csv` under `data/derived/`. No rainfall amounts are rescaled. Regeneration refuses to overwrite different existing evidence.

The simulator accepts one file only in the opt-in physical-water mode through `-Dwps.water.dailyRainCsv=<file>`. Missing dates, date gaps, duplicate rows and negative or non-finite depths fail closed. With no property, the legacy/monthly stochastic rainfall path remains unchanged. The separate daily climate ledger (`-Dwps.water.climateCsv=<new-file>`) must confirm that each eligible plot consumed the selected sequence, and that paired rules share the same other climate variables.

## Claim and experiment boundaries

| Item | Interpretation |
| --- | --- |
| 2014 versus 2019 | Relative contrast within one POWER grid-point series, not an official local drought label. |
| Other weather drivers | Temperature, reference ET and radiation still come from the seeded simulator generator, not historical 2014/2019 POWER values. Holding them fixed isolates rainfall; it does not reconstruct either historical weather year. |
| Shared-source water | A separate scenario input; neither 114 mm rainfall nor a 50% budget ratio estimates reservoir storage or district delivery. |
| Irrigation demand | The current 1 May requests are synthetic technical fixtures. A seasonal demand schedule and efficiency uncertainty need justification before paper results. |
| External validation | Local IDEAM station observations were acquired **after this POWER screen**; see the amendment above. Plot-level delivered-water/yield pairs remain unavailable. NASA POWER is a proxy; historical rule effectiveness is not validated. |

**Technical integration gate passed:** the proportional run under each rainfall file consumed all 365 fixture days on the same six eligible plots. Equal-per-hectare and small-plot-floor runs under each file also passed farm, water, yield and climate audits. Within each rainfall case, the three rules have byte-identical daily climate files and the same plot/owner/area/planting/harvest/full-tonne cohort; see [REMOTE_SMOKE.md](REMOTE_SMOKE.md). The one-day synthetic request produced negligible rule-production differences, so these runs establish wiring and pairing only.

**Historical next gate:** this line described the state at the earlier one-day pilot. Multi-date requests and UPA aggregation have since been implemented, and 12 of 24 registered POWER technical runs were admitted. For the paper-facing weather contrast, follow the station-quality and forcing-construction gate in the amendment above. Do not calculate or publish a policy ranking from the historical technical matrix.
