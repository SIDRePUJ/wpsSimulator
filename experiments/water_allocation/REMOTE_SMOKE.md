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

All plots in this fixture have equal area and the positive requests are equal; the allocation rules cannot yield an informative equity contrast. The 200-m3 budget and 20-mm demands are synthetic fixtures, not measured district supply or UPA demand. No `t/ha` production export is wired to the full agent model, and no external water-deficit/yield validation has been performed. The next gate is a heterogeneous, empirically anchored UPA population with stable IDs and physical crop-output traces, followed by paired institutional-rule runs and sensitivity analysis.
