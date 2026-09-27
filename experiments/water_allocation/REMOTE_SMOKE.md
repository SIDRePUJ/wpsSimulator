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
