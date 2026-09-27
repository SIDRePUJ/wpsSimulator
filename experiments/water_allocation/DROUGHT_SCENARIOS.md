# Preselected rainfall-forcing contrast

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
| External validation | Local IDEAM station observations and plot-level water/yield observations remain unavailable here. NASA POWER is a proxy; historical rule effectiveness cannot be validated. |

**Technical integration gate passed:** the proportional run under each rainfall file consumed all 365 fixture days on the same six eligible plots. Equal-per-hectare and small-plot-floor runs under each file also passed farm, water, yield and climate audits. Within each rainfall case, the three rules have byte-identical daily climate files and the same plot/owner/area/planting/harvest/full-tonne cohort; see [REMOTE_SMOKE.md](REMOTE_SMOKE.md). The one-day synthetic request produced negligible rule-production differences, so these runs establish wiring and pairing only.

**Next gate:** register source/demand, `Ym`, `Ky`, efficiency and area-composition sensitivity ranges; implement UPA-level aggregation; then run paired seeds and a multi-date irrigation schedule. Do not calculate or publish a policy ranking from the technical matrix.
