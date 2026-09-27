# Physical-unit research kernel

The research kernel is **separate from the legacy WellProdSim harvest equation**. It makes a finite source, plot water, and a seasonal rice-yield response auditable without implying that a historical irrigation policy has been validated.

## Quick check

From this directory run `python verify_physical.py` with Python 3 and Java 21 `javac`/`java` on PATH. It compiles the dependency-free Java files into a temporary directory, runs eight research-kernel Java tests with assertions enabled, and runs the synthetic paired-output, CNA-aggregate and POWER-rainfall preparation tests. The tests cover exact unit conversions, source conservation, delivery losses, complete request rounds, rule behavior, limiting yield cases, yield-ledger completeness, independent climate RNG streams, daily-forcing audit gaps and fail-closed dated rainfall input. They do not run the full simulator.

On Windows, `powershell -File verify_source_build.ps1` also compiles all six local BESA source modules and this isolated simulator worktree, then runs `CropLayerIrrigationTest`. It creates a unique directory under the project-level `tools` folder and does not overwrite the shared `bin` JARs. This is a source-build and controlled crop-water-balance check, not a full agent simulation.

## Unit contract

| Quantity | Unit | Meaning |
| --- | --- | --- |
| Plot area | ha | Area receiving the irrigation event. |
| Net irrigation need/delivery | mm | Water depth reaching the root zone; not a source withdrawal. |
| Gross request/allocation/source stock | m3 | Shared-source water before delivery losses. |
| Delivery efficiency | fraction in (0, 1] | Net plot volume divided by gross withdrawal. |
| Actual/potential crop ET | mm per season | Inputs to the seasonal rice response; should come from the crop water balance, not rainfall alone. |
| Potential/actual rice yield | t/ha | Crop output per harvested hectare. |
| Rice production | t | Yield multiplied by harvested hectares. |

For any area `A` in hectares, depth `d` in millimetres and delivery efficiency `e`, net plot water is `10 × A × d` m3 and source withdrawal is `10 × A × d / e` m3. The allocator takes a **complete batch** of requests per decision round and never withdraws more than the remaining stock. The equal-per-hectare rule equalizes **gross source m3/ha**, subject to request caps. Its small-plot rule gives the smallest quarter of **positive-demand** plots (by area, ties broken by ID) priority for up to half their request, then divides residual water in proportion to unmet need. Those definitions are testable scenario assumptions, not observed local institutions.

The rice module uses the [FAO-33 relative-yield equation](https://www.fao.org/4/X5647E/x5647e0e.htm): `1 − Ya/Ym = Ky(1 − ETa/ETm)`, bounded to `[0, 1]` relative yield. `Ym` (t/ha) and `Ky` remain explicit inputs; the test's `6 t/ha` and `Ky = 1.1` are arithmetic fixtures, **not** estimates for María La Baja. The integrated opt-in path now accumulates ETa from the existing stressed crop water balance and ETm from standard crop ET on the same crop days. `full_t = Ym × area_ha` is an analytical full-ET reference for that plot, **not** a separate observed or agent-replayed full-water harvest. This simpler seasonal response does not reproduce [AquaCrop's](https://www.fao.org/aquacrop/overview/calculation-scheme/) canopy, transpiration, biomass and harvest-index dynamics.

## What the kernel proves—and does not

- **Verified here:** conversion, non-negative/capped allocation, shared-stock conservation and equation implementation for unit-test cases. A separate focused `CropLayerIrrigationTest` also verifies targeted delivery and a controlled 10 mm reduction in next-day root-zone depletion; this is not a full-agent run.
- **Not yet calibrated:** local `Ym`, `Ky`, irrigation efficiency, soil water parameters and source volume. The [evidence audit](EVIDENCE.md) explains why CNA and EVA rice yields cannot be combined silently.
- **Not yet independently validated:** rice response under observed water-deficit regimes, district deliveries, or effects of any historical allocation rule. FAO's [AquaCrop validation guidance](https://www.fao.org/4/i2800e/i2800e.pdf) warns against treating agreement at one yield level as validation of water-response behavior.
- **Verified in one full-agent technical smoke:** an opt-in plan preallocates complete daily request batches and passes net millimetres to the matching crop world. An additional opt-in harvest ledger writes one row per positive-demand eligible plot, including `NOT_HARVESTED`; an incomplete ledger invalidates the run. The one-family May fixture exited 0 with both positive deliveries and both eligible harvests audited; see [REMOTE_SMOKE.md](REMOTE_SMOKE.md). This does not establish paired-rule comparability by itself.

## Opt-in research mode

Set these Java system properties before launching WellProdSim; without them the legacy irrigation path is unchanged:

```text
-Dwps.water.requests=<absolute-path-to-requests.csv>
-Dwps.water.sourceM3=<finite seasonal source volume in m3>
-Dwps.water.rule=PROPORTIONAL_DEMAND|EQUAL_PER_HECTARE|SMALL_PLOT_FLOOR
-Dwps.water.auditCsv=<absolute-path-to-a-new-audit.csv>
-Dwps.water.farmAssignments=<absolute-path-to-family-farm.csv>
-Dwps.water.yieldCsv=<absolute-path-to-a-new-yield.csv>
-Dwps.water.potentialYieldTpha=<positive Ym scenario value in t/ha>
-Dwps.water.ky=<positive Ky scenario value>
-perturbation none
```

The final four entries enable physical production output; omit the three yield-related system properties to run a water-delivery-only technical check. A configured yield CSV requires both numeric parameters and the `none` perturbation setting. The simulator's default is `disease`: using it with FAO-33 would mix disease loss into a water-only yield response, so research yield mode refuses that combination. This changes neither legacy simulations nor the earlier water-only smoke.

The yield ledger includes only plots with at least one **positive net-demand** request. Zero-demand rows still identify/register other crop worlds for the water audit, but do not define the one-season production cohort. The ledger records planting and harvest dates so a proposed fixed request date can be checked against actual crop eligibility; a request before planting remains `NOT_APPLIED` and invalidates the run. The separate `smoke_requests_yield_may_seed12345.csv` is a revised **synthetic** timing fixture, not an observed irrigation schedule.

The farm manifest header is `family_alias,farm_name` or, for an explicit heterogeneous scenario, `family_alias,farm_name,crop_area_ha_per_plot`. The optional crop-area value is a **positive integer hectares per planted crop world**, applied to that family's research profile before agent creation. It is not the UPA's total harvested area: sum eligible harvested plot areas by family to obtain that quantity. The two-column manifest preserves the prior profile behavior. Every research household must have exactly one row, and no farm can be assigned twice. The authority assigns the listed farm regardless of agent arrival order; an absent family, unknown/unavailable farm, or duplicate request invalidates the run through `PHYSICAL_FARM_AUDIT`. This pins the eligible land population, but it does **not** guarantee identical asynchronous planting dates or random draws. All paired rules must reuse the same manifest.

The committed `smoke_farm_manifest.csv` and `smoke_requests_seed12345.csv` are a synthetic one-household integration fixture only. Their equal-area plots cannot establish an equity–efficiency trade-off. The observed full-agent check and its limits are recorded in [REMOTE_SMOKE.md](REMOTE_SMOKE.md).

The CSV header is exactly `date,plot_id,area_ha,net_demand_mm,delivery_efficiency`. Dates use `dd/MM/yyyy`, the simulator's configured format; `plot_id` is the unique **land/world alias**, not the crop-cell ID (`rice` for every rice world). Every rice plot must have at least one row, including a zero-demand row if necessary. A day's rows form one complete allocation round; the finite source is shared across all dates in chronological order. The same request file, eligible plots and source volume must be reused for each institutional rule. Each planned plot area must equal the world's crop area. The research path rejects non-rice crops, legacy irrigation events, unknown plots and irrigation on initialization/after harvest days rather than silently mixing modes.

For a seed/world/household configuration, `-Dwps.water.discoverPlots=true` without a request schedule prints `WATER_PLOT` aliases, crop types, and areas plus `WATER_PLANT` rows with the plot ID, family alias, planting date and area. Use the same farm manifest for discovery and policy runs. This diagnostic and the physical mode round profile crop hectares to the nearest positive integer; the legacy mode retains its original truncation, which can make a nominal 1-ha profile into a zero-area crop world. The positive 1-ha minimum is an explicit scenario assumption, not a measured cadastral area. Discovery is not a policy result. **Do not assume the same seed alone reproduces the same aliases or dates:** earlier full-agent smoke runs produced different plot IDs with the same seed and arguments. The farm manifest removes one source of this mismatch; repeated discovery and a complete exit-0 water audit must verify whether it suffices.

This is an **exogenous request schedule**, not a claim that observed households submitted these exact demands. The source is debited when the full plan is formed. At normal shutdown, the research mode creates (without overwriting) a plot/date CSV with planned gross m3 and net mm, actual applied net mm, and `APPLIED`, `NO_DELIVERY`, `NOT_APPLIED`, or `PLOT_ABSENT` status. Failed attempts to register another crop world appear as `PLOT_REGISTRATION_FAILED`. Missing plots, positive deliveries, or failed registrations return exit code 2; they must not enter a policy comparison. An abnormal termination may produce no audit and is also unusable evidence. A crop world must contain exactly one crop in this research mode. Root-zone depletion consumes the scheduled depth within the existing water-balance update, so a day's delivery affects subsequent stress rather than retroactively changing that day's ET. With yield mode enabled, a second non-overwriting CSV reports `family_alias`, seasonal ETa/ETm, t/ha, actual tonnes and full-ET reference tonnes per unique plot; any missing harvest returns exit code 2. These are conditional model outputs until Ym, Ky and forcing are calibrated or sensitivity-bounded. Legacy biomass remains separate.

The first three-family rule pilot exposed a reproducibility failure: an exact proportional repeat had identical water audit but different ET/yields. The opt-in physical path now gives each climate layer an independent pseudorandom stream keyed by scenario seed, plot ID and layer type; the legacy shared RNG remains unchanged. In the tested three-family configuration, exact full-agent repeats at two seeds now produce identical forcing, water and harvest CSVs, and all three rules at one seed consume byte-identical forcing. This does not turn stochastic synthetic weather into observed weather or guarantee every untested configuration is deterministic.

Optional `-Dwps.water.climateCsv=<new-path>` writes a separate non-overwriting CSV of the daily rainfall, reference ET, temperature and short-wave radiation consumed by each positive-demand rice plot. At normal shutdown its audit rejects missing plots, duplicate plot/days and gaps between each plot's first and last observed day. This ledger allows an independent exact-repeat or overlapping plot/date comparison between rules; it does not establish that the synthetic forcing represents observed station weather.

Optional `-Dwps.water.dailyRainCsv=<file>` replaces stochastic rainfall **only in physical research mode** with a complete dated mm/day CSV (`date,rain_mm`). The rainfall layer rejects missing days and never silently falls back to random draws. The preselected [POWER rainfall scenarios](DROUGHT_SCENARIOS.md) are gridded proxy inputs, not observed district water supply; temperature, reference ET and radiation remain the seeded simulator climate in this initial rainfall-only contrast.

The exit-0 full-agent yield-output smoke used explicit **fixture** Ym/Ky values and `-perturbation none`. The next gate is realistic drought forcing and source/demand scenario design with sensitivity bounds, then UPA-level analysis. The harvest ledger must be joined to the water audit and scenario metadata before passing rows to `analyze_physical_results.py`; it is not itself a paper-results table. No monetary outcome is calibrated by this research path.
