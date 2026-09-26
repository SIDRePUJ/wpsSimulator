# Physical-unit research kernel

The research kernel is **separate from the legacy WellProdSim harvest equation**. It makes a finite source, plot water, and a seasonal rice-yield response auditable without implying that a historical irrigation policy has been validated.

## Quick check

Compile the dependency-free Java files under `src/main/java/org/wpsim/research/water/` together with the two `src/test/java/org/wpsim/research/water/` classes, then run `SharedWaterSourceTest` and `RiceYieldResponseTest` with assertions enabled. The tests cover exact unit conversions, source conservation, delivery losses, rule behavior, and limiting yield cases. They do not run the full simulator.

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

For any area `A` in hectares, depth `d` in millimetres and delivery efficiency `e`, net plot water is `10 × A × d` m3 and source withdrawal is `10 × A × d / e` m3. The allocator takes a **complete batch** of requests per decision round and never withdraws more than the remaining stock. Its small-plot rule gives the smallest quarter of plots (by area, ties broken by ID) priority for up to half their request, then divides residual water in proportion to unmet need. That definition is a testable scenario assumption, not an observed local institution.

The rice module uses the [FAO-33 relative-yield equation](https://www.fao.org/4/X5647E/x5647e0e.htm): `1 − Ya/Ym = Ky(1 − ETa/ETm)`, bounded to `[0, 1]` relative yield. `Ym` (t/ha) and `Ky` remain explicit inputs; the test's `6 t/ha` and `Ky = 1.1` are arithmetic fixtures, **not** estimates for María La Baja. This simpler seasonal response does not reproduce [AquaCrop's](https://www.fao.org/aquacrop/overview/calculation-scheme/) canopy, transpiration, biomass and harvest-index dynamics.

## What the kernel proves—and does not

- **Verified here:** conversion, non-negative/capped allocation, shared-stock conservation and equation implementation for unit-test cases.
- **Not yet calibrated:** local `Ym`, `Ky`, irrigation efficiency, soil water parameters and source volume. The [evidence audit](EVIDENCE.md) explains why CNA and EVA rice yields cannot be combined silently.
- **Not yet independently validated:** rice response under observed water-deficit regimes, district deliveries, or effects of any historical allocation rule. FAO's [AquaCrop validation guidance](https://www.fao.org/4/i2800e/i2800e.pdf) warns against treating agreement at one yield level as validation of water-response behavior.
- **Not yet integrated:** legacy farm clocks are asynchronous. A scientific rule comparison requires all competing farms' requests to be collected at the same decision epoch before `allocateRound` is called.

The next integration task must not relabel the current biomass output as tonnes. A new explicit research output must take actual and potential ET from the crop water balance, apply an independently chosen potential yield/response factor, and remain separate from legacy monetary calculations unless the complete economic calibration is revisited.
