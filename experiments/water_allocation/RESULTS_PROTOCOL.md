# Physical allocation experiment protocol

**Status:** analysis interface and synthetic tests are ready. Full-year one- and three-family **technical smokes** passed farm, water and eligible-yield audits; see [REMOTE_SMOKE.md](REMOTE_SMOKE.md). An exact proportional repeat initially had identical water but different yields; plot-layer climate RNG isolation now makes the tested repeated yield ledger identical. Daily forcing is not independently exported, and there is **no calibrated or robust equity–efficiency production comparison** yet. The calculations below cannot turn fixture crop-response parameters or an uncalibrated source budget into historical evidence.

## Minimum comparison

Use one rice season and the **same** eligible plots, weather sequence, water budget, uncertain-parameter vector and random seed for all allocation rules. Compare `PROPORTIONAL_DEMAND`, `EQUAL_PER_HECTARE`, and `SMALL_PLOT_FLOOR`. The full-water run is a reference for each plot, not a competing allocation policy.

In the opt-in simulator path, pin each research household to a farm with a shared manifest, then construct the complete daily request CSV **before** comparing rules. The request schedule is exogenous and must be held fixed across rules; it is not inferred from asynchronous irrigation messages. Verify that every scheduled unique land/world alias corresponds to one planted rice world and that area matches. Require exit 0, `PHYSICAL_FARM_AUDIT.valid`, and `PHYSICAL_WATER_AUDIT.valid` for every paired run. Report planned gross withdrawals, delivered net depth and any unserved or absent plots before production comparisons. Repeated discovery with the manifest must confirm that plot identities remain stable; the manifest alone is not proof.

Repeat **at least one identical within-rule scenario** and compare plot-level weather/ET/yield trajectories before interpreting between-rule production differences. A fixed global seed alone did not guarantee this in the threaded agent model: the first three-family proportional repeat had identical water-audit bytes but different harvest ET and tonnes. The opt-in plot-layer RNG repair made the tested repeated yield ledger byte-identical. Request `-Dwps.water.climateCsv=<new-path>` for an independent daily forcing trace and compare all common plot/date rows across rules; never mistake a different harvest window for different exogenous weather. The pilot's one-day irrigation produced only a tiny yield spread. Rule-specific production and inequality summaries remain diagnostics until forcing, sensitivity, calibration and UPA aggregation gates pass.

Before running, register two contrasting weather sequences using the acquired NASA POWER data as a **gridded proxy**, not station observations. Select three source-budget/demand ratios including an unconstrained diagnostic and at least two scarce levels. Define plausible `Ky`, potential yield, delivery efficiency and area-composition ranges from sources or transparent assumptions. Do not select ranges after viewing rule rankings. Ten paired seeds are an initial precision check, not a guarantee of adequate Monte Carlo precision; increase them if contrasts remain noisy.

For the minimum heterogeneous population, use the identifier-free CNA rice-area [aggregate](data/derived/rice_area_distribution.json) as a **composition constraint**: the district-source positive-area subset has 13, 17 and 9 UPA in (0,5], (5,10] and >10 harvested-ha classes. If computational limits require a smaller population, preserve approximate class proportions and state the integer rounding, rather than calling it a representative sample. Because the 39 UPA are a selected subset and harvested area is not physical farm geometry, treat the resulting plots as synthetic scenario analogues. Never export source UPA identifiers or transfer raw CNA data to the server.

## Per-plot output contract

The integrated simulator must export one row per plot/rule/weather/scarcity/seed/parameter set:

| Column | Unit or meaning |
| --- | --- |
| `weather`, `rule`, `seed`, `parameter_set`, `plot_id` | Exact paired-scenario identifiers; plot ID must not be a DANE microdata identifier in a shared artifact. |
| `scarcity_ratio` | Available gross source volume divided by total gross demand; fraction in `[0,1]`. |
| `area_ha` | Harvested/irrigated plot area in ha. |
| `full_t` | The plot's analytical full-ET reference `Ym × area_ha` in t under the same assumed potential yield; not a separate observed or agent-replayed harvest. |
| `actual_t` | Production in t under the rule; may not exceed `full_t` for this simplified one-factor response. |
| `gross_m3` | Gross source withdrawal attributable to the plot in m3. |

Run `python analyze_physical_results.py path/to/results.csv --output path/to/summary.json`. The analyzer rejects duplicate plots, nonphysical values, missing proportional baselines, or unpaired plot areas/full-water references. It reports production tonnes and ratio, worst-decile relative production loss, worst loss among the smallest quarter of plots, paired differences versus proportional allocation, and the range/sign stability of differences across parameter sets. It also reports unweighted Gini coefficients of **relative** and **absolute-tonne** production losses across plot/UPA analogues. The ratio of mean relative loss in the smallest versus largest area quartile uses ascending area with plot ID as tie-breaker and returns `null` if the largest quartile has zero mean loss; it must not be interpreted as a welfare ratio. Each quartile contains `ceil(N/4)` plots, so use a sufficiently large population before interpreting tail statistics. Its tests use **synthetic arithmetic fixtures only**; there are no claimed empirical outcomes.

The opt-in physical harvest ledger produces an **intermediate** CSV keyed by the unique plot ID. Before analysis, join it to the same run's water audit and add explicit weather, rule, seed, parameter-set and scarcity-ratio metadata; reject duplicate/missing plot joins. Do not enter any run with `NOT_HARVESTED`, a nonzero exit, or a failed farm/water/yield audit in the paired table. `Ym` and `Ky` are scenario parameters until calibrated or sensitivity-bounded, not estimates inferred from the legacy biomass output.

The harvest ledger now carries `family_alias`; aggregate eligible plot areas and production by this alias **before** computing any UPA inequality. The current analyzer still operates at plot level, so its inequality output is not yet a UPA-level result. A subsequent aggregation/paired-cohort check is required before paper claims. An optional farm-manifest `crop_area_ha_per_plot` sets an integer scenario area for each planted world; it does not directly specify a UPA's harvested area. Use CNA's three observed area classes only as composition constraints, and audit the simulated sum of eligible areas per family against the intended class.

The yield cohort is the set of plots with at least one positive scheduled net irrigation demand. Zero-demand registration rows do not enter the production inequality denominator, although their plot identities still must reconcile in the water audit. Report the eligible cohort count and verify each fixed request date is after the recorded planting date and before harvest; a stable alias alone does not establish temporal eligibility.

## Evidence and publication gates

| Claim | Evidence required | Current status |
| --- | --- | --- |
| Implementation is internally correct | Unit conversions, non-negative water, shared-source conservation, target-only delivery, matching across rules | Core kernel tested; target-only simulator integration pending. |
| Rice production scale is plausible | One clearly defined local EVA benchmark, aligned crop/period/population, plus independently checked input and weather | Pending: CNA/EVA 2013 discrepancy; legacy crop output not physical. |
| Yield response to water is validated | Independent observed yields with corresponding water deficits in more than one regime | **Unavailable** in the acquired local data. Literature `Ky` is a prior, not validation. |
| A specific historical district rule is validated | District membership, source storage/release and farm/plot delivery series | **Unavailable**. Do not make this claim. |
| Rule ranking is robust | Paired rule contrasts remain directionally stable across preregistered physical and population uncertainty | Executable analyzer ready; no model runs yet. |

This protocol keeps the new paper separate from TCSS's irrigation-access/on-off results and from the Journal of Simulation methodology paper. Its defensible near-term result, if model integration passes, is a **conditional production–loss trade-off**, not a prediction of the district's historical production or household welfare.
