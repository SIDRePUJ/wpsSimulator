# Physical-unit research kernel

The research kernel is **separate from the legacy WellProdSim harvest equation**. It makes a finite source, plot water, and a seasonal rice-yield response auditable without implying that a historical irrigation policy has been validated.

## Quick check

From this directory run `python verify_physical.py` with Python 3 and Java 21 `javac`/`java` on PATH. It compiles the dependency-free Java files into a temporary directory, runs `SharedWaterSourceTest`, `RiceYieldResponseTest`, and `PhysicalIrrigationPlanTest` with assertions enabled, and runs the synthetic paired-output analyzer tests. The tests cover exact unit conversions, source conservation, delivery losses, complete request rounds, rule behavior, and limiting yield cases. They do not run the full simulator.

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

The rice module uses the [FAO-33 relative-yield equation](https://www.fao.org/4/X5647E/x5647e0e.htm): `1 − Ya/Ym = Ky(1 − ETa/ETm)`, bounded to `[0, 1]` relative yield. `Ym` (t/ha) and `Ky` remain explicit inputs; the test's `6 t/ha` and `Ky = 1.1` are arithmetic fixtures, **not** estimates for María La Baja. This simpler seasonal response does not reproduce [AquaCrop's](https://www.fao.org/aquacrop/overview/calculation-scheme/) canopy, transpiration, biomass and harvest-index dynamics.

## What the kernel proves—and does not

- **Verified here:** conversion, non-negative/capped allocation, shared-stock conservation and equation implementation for unit-test cases. A separate focused `CropLayerIrrigationTest` also verifies targeted delivery and a controlled 10 mm reduction in next-day root-zone depletion; this is not a full-agent run.
- **Not yet calibrated:** local `Ym`, `Ky`, irrigation efficiency, soil water parameters and source volume. The [evidence audit](EVIDENCE.md) explains why CNA and EVA rice yields cannot be combined silently.
- **Not yet independently validated:** rice response under observed water-deficit regimes, district deliveries, or effects of any historical allocation rule. FAO's [AquaCrop validation guidance](https://www.fao.org/4/i2800e/i2800e.pdf) warns against treating agreement at one yield level as validation of water-response behavior.
- **Partly integrated, not yet exercised in a full run:** an opt-in plan preallocates complete daily request batches and passes net millimetres to the matching crop world. This avoids arrival-order bias from asynchronous family clocks. No integrated scenario output has yet been verified.

## Opt-in research mode

Set these Java system properties before launching WellProdSim; without them the legacy irrigation path is unchanged:

```text
-Dwps.water.requests=<absolute-path-to-requests.csv>
-Dwps.water.sourceM3=<finite seasonal source volume in m3>
-Dwps.water.rule=PROPORTIONAL_DEMAND|EQUAL_PER_HECTARE|SMALL_PLOT_FLOOR
```

The CSV header is exactly `date,plot_id,area_ha,net_demand_mm,delivery_efficiency`. Dates use `dd/MM/yyyy`, the simulator's configured format; `plot_id` is the unique **land/world alias**, not the crop-cell ID (`rice` for every rice world). Every rice plot must have at least one row, including a zero-demand row if necessary. A day's rows form one complete allocation round; the finite source is shared across all dates in chronological order. The same request file, eligible plots and source volume must be reused for each institutional rule. Each planned plot area must equal the world's crop area. The research path rejects non-rice crops, legacy irrigation events, unknown plots and irrigation on initialization/after harvest days rather than silently mixing modes.

This is an **exogenous request schedule**, not a claim that observed households submitted these exact demands. The source is debited when the full plan is formed, so a planned plot that never materializes would leave a discrepancy; compare actual crop IDs and outputs with the request file before interpreting any experiment. Root-zone depletion consumes the scheduled depth within the existing water-balance update, so a day's delivery affects subsequent stress rather than retroactively changing that day's ET. Plot-level production in tonnes still requires the separate FAO-33 response and calibration; legacy biomass must not be relabelled as yield.

The next integration task must not relabel the current biomass output as tonnes. A new explicit research output must take actual and potential ET from the crop water balance, apply an independently chosen potential yield/response factor, and remain separate from legacy monetary calculations unless the complete economic calibration is revisited.
