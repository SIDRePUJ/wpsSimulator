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

## Direct daily-forcing audit (commit `3a80efa`)

An opt-in `climate.csv` now records the rainfall, reference ET, temperature and radiation **consumed by each eligible crop plot on each crop day**. The isolated compiled archive matched SHA-256 `3d788b1d63458ffab4cb2c83165839df1f391c89163faa8e5514b7ffba4439e4` before deployment. Seven Java tests, six Python tests, all six BESA modules, 243 simulator files and the controlled crop test passed locally.

Six full-year runs at commit `3a80efa` exited 0: proportional twice each at seeds `12345` and `24680`, plus equal-per-hectare and small-floor at seed `12345`. Every run passed farm 3/3, water 12/12, yield 6/6 and climate 6/6 audits; each climate file has **726 unique plot/date rows**, no missing plots, gaps or duplicates. Both exact repeats at a given seed had byte-identical water, climate and yield CSVs. At seed `12345`, all three allocation rules shared the same climate CSV SHA-256 `c02207b19fddbbbe645a517f2a2ffb57ad5cdfde8e559df750e90af2b86da1c6` while their water audits differed as expected. At seed `24680`, the two proportional repeats shared climate SHA-256 `6ac2a40aea7ec25a6b929aac0823154df442ef4da7d7db25792bc056a967ce07` and yield SHA-256 `17b1aa0a553808cce3578fa33ea4a806fcca843ed095d72ef4f98def85cf6186`. Independent local parsing confirmed the same six plot IDs, owners, areas and planting dates in all six runs; the two seeds produced distinct climate files.

This closes the **technical common-forcing/repeatability gate for the tested population and two seeds**, not a statistical convergence study. The one-event synthetic budget and uncalibrated yield parameters still do not support a publishable drought-policy ranking. Retrieved files are under the project-level `results/water-allocation-server-20260927/forcing-3a80efa-*/` directories; no raw CNA records were transferred.

## Preselected rainfall-proxy matrix (commit `33e582f`)

The public NASA POWER 2014 lower-tail and 2019 near-median daily rainfall sequences were mapped to the same 2022 calendar **before** viewing rule outcomes. A new isolated compiled build and only the two derived rainfall CSVs were transferred; raw CNA data were not transferred. Six full-year runs used the same three synthetic families, 1 May request schedule, 4,600-m3 shared source, seed `12345`, `Ym=6 t/ha`, `Ky=1.1` and `-perturbation none`.

| Rainfall proxy | Rule | Modeled production (t) | Climate SHA-256 prefix |
| --- | --- | ---: | --- |
| 2014 | Proportional demand | 36.279998 | `728bfd68d86014ee` |
| 2014 | Equal per hectare | 36.279540 | `728bfd68d86014ee` |
| 2014 | Small-plot floor | 36.279818 | `728bfd68d86014ee` |
| 2019 | Proportional demand | 97.659074 | `492cbd2145916322` |
| 2019 | Equal per hectare | 97.670653 | `492cbd2145916322` |
| 2019 | Small-plot floor | 97.663720 | `492cbd2145916322` |

All six processes exited 0 and reported farm 3/3, water registration 12/12, six applied deliveries, eligible harvests 6/6 and daily-climate plots 6/6 (726 rows, zero gaps/duplicates). Each rule withdrew exactly 4,600 gross m3. Independent local parsing confirmed identical plot IDs, owners, areas, planting/harvest dates and full-tonne references within each rainfall case. The three rules' climate CSVs are byte-identical within each case, while water-audit files differ by rule. For the two proportional runs, all 726 recorded rainfall values matched their respective derived daily fixture, and non-rainfall climate variables matched across common plot/date rows. Retrieved logs and CSVs are under project-level `results/water-allocation-server-20260927/power-rain-33e582f-*/`.

**Interpretation:** rainfall forcing changes modeled production markedly in this synthetic setup, but the policy-production differences are only −0.00046/−0.00018 t for equal/floor versus proportional in 2014 and +0.01158/+0.00465 t in 2019. That scale and sign change show why this one-day demand fixture cannot support a rule ranking. POWER precipitation is a gridded proxy, non-rainfall weather is simulated, and the source budget, crop response and three-family composition are uncalibrated. No historical district drought or allocation effect has been validated.

## UPA aggregation check on the six-run technical matrix

The new `analyze_upa_results.py` groups the already-joined eligible plot rows by `family_alias` **before** calculating inequality. Synthetic tests cover two plots per UPA, duplicate/missing plots, changed ownership, area and full-tonne references. A local diagnostic join of the six retrieved runs validated exit and research-audit markers, matched each of 36 eligible harvest rows to its same-run applied-water row, and fed the analyzer: every scenario aggregated six plots into three UPA and reproduced its independently checked total tonnes. The local joined CSV is under project-level `results/water-allocation-server-20260927/power-rain-33e582f-upa-technical-join.csv` and contains only synthetic agent aliases.

That initial ad hoc check was **not** an end-to-end audited data-preparation pipeline. The diagnostic join was not committed, and three invented UPA with one-day synthetic requests cannot support an inequality effect size for publication. The next section records the subsequent reproducible admission gate.

## Manifest-backed admission and UPA join

`build_joined_results.py` now implements that gate. A local manifest of the six retrieved rainfall-proxy runs identifies the run directory, common request and farm fixtures, the selected rainfall fixture, scenario keys and the 4,600-m3 source. The builder checks exit status, exactly one complete farm/water/yield/climate audit per run, each raw CSV against requests and eligible harvests, date/area/depth/tonne arithmetic, source conservation, complete daily climate traces and exact rainfall-forcing values. It then requires identical request, farm, rainfall, climate and source fingerprints across rules within each weather/seed/parameter pair, plus UPA analyzer pairing checks, before publishing a joined table.

All six existing technical runs were admitted: **36 eligible plot rows, three UPA per scenario**. The local manifest SHA-256 is `982a74120d8b060e9f88c01a01635fa44039f016095c6cdb8b982c0f25842808`; admitted CSV SHA-256 is `335d772da0542ef7ae967a0513cb7b76404c0cb9f6c02897a122575dbae54af8`; UPA JSON SHA-256 is `c7c1cd82be62079ba551b8692a716f970401572c4500a574dbb3da33d810a3bab`. Files live only under project-level `results/water-allocation-server-20260927/`, with no raw DANE data. Synthetic negative tests reject failed runs, missing audits/climate days, rainfall mismatches, changed water area, source overwithdrawal and unequal paired climate. The outputs remain **technical diagnostics, not publication estimates**: population, demand and response parameters are still synthetic or uncalibrated.

## Seasonal-entitlement one-day equivalence smoke

The opt-in `SEASONAL_ENTITLEMENT` horizon was compiled locally with all six BESA modules and 244 simulator source files; the controlled crop test and eight Java/25 Python focused checks passed. Only the compiled research build (archive SHA-256 `a95e7c1c9ce2d7592ce8071569e16912135e20071a56e9e25f648edbd446346a`) was transferred to a new isolated server directory. One full-year 2014-rainfall proportional run used the previous three-family **single-date** fixture with the new horizon marker. Java exited **0** and passed farm 3/3, water 12/12, yield 6/6 and climate 6/6 (726 rows). Its water, yield and climate CSVs are byte-identical to the old one-date proportional run (SHA-256 prefixes `2bfc32f4`, `3c02114f`, `728bfd68`). The strict join admitted its six eligible plots with the explicit `SEASONAL_ENTITLEMENT` marker. This verifies one-date backwards equivalence and startup wiring, **not** multi-date full-agent validity or a new policy result. The PowerShell-piped helper itself returned shell code 1 due a trailing carriage return after reporting the completed run; the persisted Java `exit.txt=0`, complete audits and retrieved matching hashes establish the run outcome.

## Twelve-UPA fixed-crop technical gate

The controlled `world.24.json` is an exact 2×12, 24-cell land subset of committed `world.100.json` (SHA-256 `244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c`), not a district map. The `twelve_upa_manifest.csv` fixes twelve two-cell small farms with per-plot areas 1/4/8 ha, producing 4/5/3 UPA at 2/8/16 ha. Two isolated full-year discovery runs with the same seed exited 0 and created 12 farms each. Their 48 planting/owner/date/area traces matched, but rice/roots crop choices differed. That is a failed fixed-crop gate under ordinary price-based selection, not a harmless output-format difference.

The opt-in `RICE_ONLY` cohort then fixed the physical arm's crop portfolio without changing the default or legacy selection. A **separate synthetic one-date wiring fixture**, derived from the matched planting traces, assigned 20 net mm to each of 24 first-season plots, zero to 24 second-season rows, efficiency 1 and an unconstrained 19,200-m3 source; its SHA-256 is `f02bfe335f3931051bde338f5e3fb719ea60b152fab1b390efd82abe09b52bf9`. Two independent full-year proportional runs under 2014 POWER rain and `SEASONAL_ENTITLEMENT` exited 0, each with farm 12/12, water 48/48 (24 applied), eligible yield 24/24 and climate 24/24 (2,904 daily rows, no gaps). Their retrieved water, climate and yield CSVs were byte-identical between runs and matched server SHA-256: `0c1367b23dea99017c708f0909ee3a06bea38e517e50b1c66f177115a6d012e5`, `6ebf98600e6d9ec4dc4599c5d3f1ea35433d25613ed6978fa0676aee3a8a1773`, `81c3b1598ae162df3120c3cc88d8ae220e29ef7a132dfad015aa4a72f161d89f`. The strict horizon/cohort/world-aware join admitted 24 eligible plots; UPA aggregation recovered all twelve families and 4/5/3 area classes. This is **population, rice-cohort and reproducibility verification only**: no multi-date request, scarce-source contrast, rule comparison or calibrated rice response has yet passed this gate.

## First multi-date integration and terminal-week correction

The weekly generator uses the frozen 48-plot roster, 24 first-season crop windows and complete daily rainfall proxies. Its first 2014 schedule had 432 rows and included requests on 31 May / 10 August, one day before the technical farmer-harvest dates. The isolated 2014/0.35 proportional full-year run exited **2**: 384 deliveries applied, 24 positive deliveries not applied, 24 later rice worlds absent, no eligible harvests and an invalid climate/yield audit. CropLayer's growing-degree-day maturity guard rejects those final positive depths before the farmer harvest event. This **rejected** run is not a policy observation or a zero-yield scenario.

The schedule was amended with a seven-day terminal buffer **before viewing paired-rule contrasts**, then regenerated and frozen. The corrected [2014](data/derived/world24-weekly-2014-guarded.csv) and [2019](data/derived/world24-weekly-2019-guarded.csv) requests have 408 rows each (24 plots × 16 dates plus 24 zero-depth registration rows), with SHA-256 `d1df8e0257a4905aa1859fd53eeba67e9417fb3996f286fe696aa6114dca3006` and `783cd57af523761cdd45e7f014a37ee37e7587cf013bdeadb6c81b01292933b9`. At central efficiency 0.48, derived unconstrained gross demand is 850,360 m3 for 2014 and 589,040 m3 for 2019; these are synthetic stock-scaling denominators, not measured withdrawals. The 0.35/0.65 stocks are 297,626/552,734 m3 (2014) and 206,164/382,876 m3 (2019).

One corrected 2014/0.35 proportional remote full-year run (seed 12345, `Ym=7`, `Ky=1`) exited **0**: farm 12/12, water 48/48 with 384 applied deliveries and zero missing, yield 24/24, climate 24/24 with 2,904 daily rows and zero gaps. Local retrieved raw SHA-256 values matched the server: audit `599ffa7b0cbb80a667bc9b055811ef7ff5e900340fff8654db2fc81b4d7becb3`, climate `6ebf98600e6d9ec4dc4599c5d3f1ea35433d25613ed6978fa0676aee3a8a1773`, yield `744890fb8003865d79a431b96a9fee57d7703e42451108aeaa2d621f2ae9e480`. The strict horizon/cohort/world-aware join admitted 24 eligible plots. **Only one rule/weather/scarcity/seed combination passed here**; there is no paired rule effect, 2019 integration proof, uncertainty interval, or empirical validation from this smoke.
