# Ubuntu full-agent water smoke

**Outcome:** A complete 2022 research-mode run exited 0 with no missing or failed plot registrations and two planned 10-mm deliveries applied. This verifies the integration boundary for one synthetic household; it is not a policy contrast, crop-yield validation, or a result for publication.

## Reproducible scenario

| Input | Value |
| --- | --- |
| Simulator source | `research/water-allocation-evidence`, commit `419c985` (compiled locally with Java 21) |
| Remote environment | Isolated user-owned directory on Ubuntu; Java 21 runtime, no system packages installed |
| Agent/world/year | One household, `world.20`, 2022, seed `12345`, `-mode web`, `-land 2` |
| Farm population | `smoke_farm_manifest.csv`: `MAS_PeasantFamily1` assigned `farm_1_small` |
| Irrigation requests | `smoke_requests_seed12345.csv`: two first-season 1-ha rice plots each request 20 mm on 1 March; two later plot versions have zero requests |
| Scarcity/rule | 200 gross m3; `PROPORTIONAL_DEMAND`; efficiency 1 |

Two independent discovery runs with this farm manifest produced the same four rice plot aliases (`land_5_2`, `land_6_2`, `land_5_2_3`, `land_6_2_3`). The physical run used exactly those aliases. The three complete remote run directories and logs were retrieved to the project-level `results/water-allocation-server-20260927/` directory; no raw DANE microdata were transferred.

## Observed checks

- Process exit: `0` after 191 seconds.
- Farm audit: one planned and one assigned family; zero assignment failures.
- Water audit: four planned and four registered plots; zero absent plots, failed registrations, or missing deliveries.
- Each first-season plot withdrew 100 gross m3 and received 10 net mm. Total source withdrawal was exactly 200 m3. Later plot versions received no water.
- Retrieved `audit.csv` SHA-256: `bbefc11da11947de74446399b6a7174a606cc3e4248540ee15d2b62ee6ac7237`, matching the server copy.

The stderr contains viewer/agent-alias logging warnings despite the zero exit code. The physical audit checks water delivery and population matching, not every subsystem's output quality.

## Not yet a paper experiment

All plots in this fixture have equal area and the positive requests are equal; the allocation rules cannot yield an informative equity contrast. The 200-m3 budget and 20-mm demands are synthetic fixtures, not measured district supply or UPA demand. At the time of this first smoke, no `t/ha` production export was wired to the full agent model; that integration is described below. No external water-deficit/yield validation has been performed. The next gate is a heterogeneous, empirically anchored UPA population, followed by paired institutional-rule runs and sensitivity analysis.

## Follow-up: physical yield integration (commit `679fbc5`)

The first yield-enabled full-year run reused the March request fixture, set `-perturbation none`, and used **fixture** values `Ym = 6 t/ha` and `Ky = 1.1`. It exited **2**, correctly rejecting one positive delivery that was not applied and one zero-demand second-season plot that had not harvested. Its logs and audits are retained under the project-level `results/water-allocation-server-20260927/yield-smoke-27c41cc-seed12345/`; they are invalid as study results.

The corrected research path keeps all four plot aliases in the water-registration audit but limits the one-season production cohort to plots with positive scheduled demand. Using the committed synthetic `smoke_requests_yield_may_seed12345.csv`, a second isolated Ubuntu run exited **0**:

| Gate | Observed result |
| --- | --- |
| Source and execution | Commit `679fbc5`; same one household, world 20, year 2022, seed `12345`; `-perturbation none` |
| Farm audit | One planned and one assigned family; no failures |
| Water audit | Four planned and four registered plots; two 10-mm deliveries on 1 May; 200 gross m3 total, zero missing deliveries |
| Yield audit | Two positive-demand eligible plots, both harvested; planting dates 1 February and 13 April, both before 1 May delivery |
| Physical arithmetic | Independent local recalculation from ETa/ETm matched `Ym × max(0, 1 − Ky(1 − ETa/ETm))`; actual tonnes matched yield times area |
| Retrieval integrity | Water audit SHA-256 `ac4cfe9fc4f4e6b9a7d92575869f5ff75c71f33188954cc938238a14b28374e6`; yield CSV SHA-256 `70318c242cdc93bfc082633be5a7fe4a9b291202e5d93fdba6d5dd18e2b38253`, both matching server copies |

Full logs and CSV are under project-level `results/water-allocation-server-20260927/yield-may-679fbc5-seed12345/`. The two eligible plots are equal-area synthetic fixtures; their conditional t/ha values verify the wiring only. They do **not** estimate historical rice production, validate `Ym` or `Ky`, or provide an equity–efficiency comparison. Stderr still contains viewer/agent-alias logging warnings despite exit 0; the three explicit research audits pass. The next gate remains a reproducible heterogeneous one-season population and paired-rule comparison.

## Heterogeneous three-family pilot and reproducibility gate (commit `d36f56e`)

The synthetic `pilot_three_family_manifest.csv` pins three families to farms and specifies 1, 4 and 8 ha **per planted crop world**. Two independent full-year discovery runs (seed `12345`, 2022, three families, world 20, `-perturbation none`) exited 0 and had the same sorted `WATER_PLOT`/`WATER_PLANT` SHA-256: `bf9a8e8390fe9fa664e2840a3648a93ede043aba83cac417fabbd586dfc10774`. Six first-season rice plots were planted on 1 February or 13 April; the eligible family areas are 2, 8 and 16 ha. Six second-season versions were registered for reconciliation only.

`pilot_three_family_requests_may.csv` is an **invented technical fixture**, not observed farm demand: on 1 May the first-season plots request 20, 30 or 40 net mm by area class, with delivery efficiency 1. The six positive requests total 9,200 gross m3; the shared source holds 4,600 m3 (scarcity ratio 0.5). The six second-season rows have zero demand. All three allocation rules used the same fixture, manifest, seed, `Ym=6 t/ha` and `Ky=1.1`. These yield parameters are uncalibrated fixture values.

| Rule/run | Exit | Farm audit | Water audit | Eligible yield audit | Gross withdrawal |
| --- | ---: | --- | --- | --- | ---: |
| Proportional | 0 | 3/3 | 12/12, six deliveries | 6/6 | 4,600 m3 |
| Equal per hectare | 0 | 3/3 | 12/12, six deliveries | 6/6 | 4,600 m3 |
| Small-plot floor | 0 | 3/3 | 12/12, six deliveries | 6/6 | 4,600 m3 |
| Exact proportional repeat | 0 | 3/3 | 12/12, six deliveries | 6/6 | 4,600 m3 |

The water audits show distinct allocations and conserve the source. The proportional run and its exact repeat have **identical water-audit bytes** (SHA-256 `2bfc32f4527aa0d2a65152aef73eaf2ea454f2eb6eea072a9ee064b89b6d6b33`) but **different yield ledgers** (SHA-256 `4613ed47694424cf667b029bcf33040ef10a6b1348d2536f87adcabf70a199a9` versus `6779272ebaa44bab7ba59a587980db2c0becf177e0aa7448f389d06ec8aae8fe`). For example, the same `land_9_2` plot produced 35.895 versus 27.359 t under the same proportional rule and applied water. The code's shared `SimRandom` explicitly notes that threaded agents are not bitwise reproducible with a fixed seed; climate layers consume that shared RNG. This is a plausible mechanism, **not a proven complete root cause**.

Therefore the three-rule **water allocation wiring** passes its technical audit, but the apparent tonnes or inequality contrasts are **not interpretable as policy effects**. They must not enter the paper's results table. The next gate is to make exogenous daily weather and other yield-relevant randomness stable by plot/scenario, then repeat the within-rule and cross-rule checks before UPA aggregation or sensitivity analysis. Server runs and retrieved outputs are isolated under `results/water-allocation-server-20260927/pilot-three-family-*-d36f56e/`; no raw CNA data were transferred.

## First reproducibility repair and rerun (commit `db4f228`)

The physical research path now derives independent climate RNG streams from seed, plot alias and layer class. A locally compiled archive was transferred to a new isolated server build after matching SHA-256 `f2b52b1b63e1a86ffa4aba035ff78e08c3c86b68f0fed8c3c3b1a3d54949f0af`; no legacy executable was replaced. Six Java tests, six Python tests, all six BESA modules, 242 simulator files and the controlled crop test passed locally.

Two full-year **identical proportional** runs exited 0 and produced identical water-audit SHA-256 `2bfc32f4527aa0d2a65152aef73eaf2ea454f2eb6eea072a9ee064b89b6d6b33` and identical yield-ledger SHA-256 `963804df25e90235e19eb9b37991824dbe6123e67ff8fbf9deb09dfd7df886de`. The equal-per-hectare and small-floor runs on the new build also exited 0 with farm 3/3, water 12/12, six deliveries and eligible yield 6/6. All four runs withdrew 4,600 m3 and had the same six plot IDs, owners, areas, planting dates and harvest dates. Independent local checks reproduced every `actual_t = area_ha × actual_t_ha` and FAO-33 ledger calculation. No daily weather trajectory file was exported, so the strongest observed repeatability statement concerns the yield ledger, not a bytewise daily-forcing audit.

This **technical pilot** yielded 118.5263 t under proportional, 118.6420 t under equal-per-hectare and 118.5463 t under small-floor. The total spread is only about 0.116 t across 26 eligible ha despite a 50% gross-water shortage. These are uncalibrated single-seed, single-day, three-family fixture outputs, **not** an empirical drought effect or a robust equity--efficiency result. Do not promote a ranking from them. The next study design must define and verify actual drought/weather sequences, credible demand/source budgets, multiple paired seeds and parameter sensitivities, followed by UPA-level (not plot-level) inequality analysis.
