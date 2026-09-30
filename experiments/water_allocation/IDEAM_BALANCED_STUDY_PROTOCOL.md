# Balanced IDEAM allocation screen: post-C3 design lock

**Status:** Candidate descriptive **technical screen**, fixed after the C3 pilot was inspected and before any further policy simulation. This is not preregistration before all outcomes, execution authorization, calibration, validation, or a policy-ranking claim. The seed-`12345` C3 cell and its reanalysis remain immutable pilot evidence outside the new screen. The existing C3 runner is hardcoded to one cell and is **not matrix-ready**; runner preparation, seed qualification, compute, and any full runs require later authorization.

## Quick path and comparison unit

1. Recheck the frozen manifests, every ignored daily-rain/request CSV hash, and the model/runner identity before use. Do not regenerate a differing file in place.
2. Qualify each new seed against the frozen cohort and crop windows **without inspecting allocation-rule outcomes**. Stop on a mismatch; do not substitute a seed silently.
3. Only after those gates and separate execution authorization, run complete three-rule triplets. Admit each triplet through the unchanged strict raw-to-plot join before UPA analysis. Do not interpret partial cells.

From the repository root, `python experiments/water_allocation/check_ideam_readiness.py` performs the first gate without writing inputs or launching the simulator. It checks locked hashes, scenario and station metadata, daily rainfall and dated requests, crop-window/area consistency, synthetic gross demand and source-ratio arithmetic; its bounded extrema are descriptive flags, **not** IDEAM quality certification. A passing preflight does not qualify new seeds or authorize runs.

One matched comparison is a **station × source year × date-label mapping × scarcity ratio × seed** cell. Within it, compare `EQUAL_PER_HECTARE` and `SMALL_PLOT_FLOOR` separately with `PROPORTIONAL_DEMAND`. The latter is the reference rule, not a historical district practice. All three rules use the same eligible plot/owner/area/crop-window roster, rainfall and non-rainfall climate forcing, dated requests, synthetic source stock, crop-response parameters, and seed. Use `SEASONAL_ENTITLEMENT`; `ROUND_CHRONOLOGICAL` is a different temporal institution.

## Frozen candidate factors

| Factor | Values and role |
| --- | --- |
| IDEAM station code | `29030080` Puerto Santander; `29030160` Flamenco; `29030780` Mampuján; `29035040` Nueva Florida. Treat stations as separate point-forcing sensitivity branches, not four independent household samples or a municipal areal mean. |
| Source rainfall year | `2019` and `2022`, each mapped by month/day onto simulated `2022`. These contrast observed station rainfall, not officially classified district drought or observed reservoir supply. |
| ZIP date-label interpretation | `label_date` and `previous_day`. The 07:00 accumulation interval does not resolve which day this ZIP labels. Neither mapping is selected by model fit. |
| Binding source ratio | `0.35` and `0.65` times each scenario's **synthetic unconstrained gross request**. Reuse that scenario's recorded m³ stock for all three rules and both seeds; do not hold one ratio's absolute m³ constant across different rainfall scenarios. |
| Implemented allocation rule | `PROPORTIONAL_DEMAND`, `EQUAL_PER_HECTARE`, `SMALL_PLOT_FLOOR` (plot-area floor, not measured household vulnerability or productivity optimization). |
| Fresh paired random seed | `271828` and `314159`, fixed here before new rule outcomes. The viewed `12345` C3 seed is not a screen replicate and its three runs are not counted below. |

The complete candidate is **4 × 2 × 2 × 2 × 3 × 2 = 192 new full-agent runs**: 16 rain/request variants, 32 weather–stock cells, and 64 seed-matched rule triplets. At the approximately 3.5-minute local C3 runtime, sequential compute would take about **672 minutes (11.2 hours)** before analysis; this is a cost estimate, not permission to execute. No station, mapping, ratio, rule, or seed may be dropped because its result is uninteresting, null, contradictory, or unfavorable. A failed gate stops the affected screen and is reported rather than silently pruned.

The ratio design estimates effects **conditional on the same fraction of each weather-specific synthetic request**, not effects under a common physical m³ stock across years or stations. In the current request manifest, gross demand spans `25,600–696,560 m³`; for example, `29030780/2022/label_date` has only `25,600 m³` gross demand and `8,960/16,640 m³` at ratios `0.35/0.65`. Retain that cell despite possible weak policy separation. The source is not historical reservoir storage, release, or canal delivery.

## Input and model identity to recheck

These are the **current file identities at design lock**, not proof that ignored source files will remain present or unchanged. Before any future run, verify the complete SHA-256 of each local file against the relevant manifest entry, check that the manifests still have the same hashes below, and preserve the command, classpath/model identity, scenario specification, and output hashes in a new, non-overwriting run directory.

| Identity | Current SHA-256 / binding |
| --- | --- |
| [District request manifest](data/derived/ideam_district_request_manifest.json) | `dfd0766a42a12ad0954d44c617408ba764db4dc273b4f0cbd20eb9071065db80`; lists all 16 rain/request pairs, 408 request rows each, gross demand and all ratio stocks. All 16 `0.35`/`0.65` products were checked against manifest gross demand at lock time. |
| [IDEAM rainfall manifest](data/derived/ideam_rainfall_manifest.json) | `fa937f9486d9f8bb9ee1ab944b8f205449863ced71769006a55840454a9194d6`; lists each 365-day mapped rainfall CSV and its SHA-256. |
| [C2 cohort manifest](data/derived/district_crop_cohort_manifest.json) | `d9143e69fa6a002e7a698c0043a38219e77248219b8f60011cb377c1d580e058`; records the two identical seed-`12345` technical crop traces, not a cross-seed guarantee. |
| [Frozen district crop windows](data/derived/world24_district_crop_windows.csv) | `28c15b179ca5cb8bfd8de1195a3f065bd30dbb4b0e78638608dc4a1c5538f4f7`. |
| [Farm assignment manifest](twelve_upa_manifest.csv) | `eaf2d0fd34e72c18d3def0e2a89f8cc44f86ec7e4ba789d187cca884ad24ab3c`. |
| [Synthetic 24-cell world](../../src/main/resources/web/data/world.24.json) | `244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c`. |
| IDEAM source archive, retained outside Git | `FA695160A154A7CE9C95DEE736535A5A8CD9BBCFAFE5A6AA3DACE13A9BD03E1B`, as recorded in the request/rainfall manifests. It is not a release/delivery dataset. |

The current derived request files use `30 mm` weekly target, `0.8` prior-seven-day rainfall factor, and `0.48` delivery efficiency. These are assumed root-zone supplements, **not** observed paddy-field requirements. Hold the C3 technical vector `Ym=5 t/ha`, `Ky=1`, physical-water mode, `RICE_ONLY`, district-calendar switch, one simulated 2022 year, `-env local -mode web -agents 12 -world 24 -land 2 -perturbation none`, the same world/manifest, and `SEASONAL_ENTITLEMENT` fixed throughout this screen. The `Ym`/`Ky` values are unfitted. Do not import the earlier February/April POWER cohort's `Ym=7` or its stock files; do not tune any parameter to erase the EVA gap.

## Readiness and stop gates

| Gate | Required evidence; failure action |
| --- | --- |
| Input provenance and weather | Verify source/station metadata, units, complete dates, duplicates, missing values and suspicious extremes; retain both unresolved ZIP date mappings and disclose station-quality/spatial-support limits. Verify the 16 local rainfall and 16 district-request files byte-for-byte against manifest SHA-256; confirm each request date lies inside its plot's frozen crop window, each 408-row file's 24 eligible plots/96 ha, non-negative depths and `gross_m3 = Σ(10 × area_ha × net_mm / 0.48)`. Stop on any discrepancy. A syntactic check does not certify meteorological quality. |
| Seed/cohort qualification | Before policy-outcome inspection, independently observe each fresh seed's complete plant/owner/area/harvest trace using a cohort diagnostic. Require 12 assigned synthetic UPA in 4/5/3 area classes, the same 24 first-version eligible rice plots/96 ha and frozen per-plot dates/windows, 48 registered annual crop worlds, first plantings in January–March, and all 24 eligible harvests. A change in crop windows or aliases invalidates the frozen requests: stop and document it; neither seed replacement nor request regeneration is automatic. |
| Per-run execution and audit | Require natural Java exit `0`, complete farm/water/yield/climate files, correct startup markers and all scheduled plot registrations, no absent/failed plot or missing harvest, and matched 24 eligible plots/12 UPA/96 ha. The 2,904 daily climate rows and 408 request/water rows observed in C3 are expected structural checks, not substitutes for full same-run validation. Stop on timeout, nonzero exit, missing/duplicate audit row, altered roster or non-rule climate forcing, invalid request/application timing, over-delivery or source imbalance. Water-dependent actual ET may legitimately differ by rule. Missing outcomes are **never zeros**. |
| Matched triplet admission | Use the existing strict `build_joined_results.py` contract and then `analyze_upa_results.py`; pair all three rules on identical request/rain/seed/stock/model/cohort identities and climate bytes. Demand 24 admitted eligible plot rows per rule and 12 UPA per rule in each complete triplet. Stop on any failed join or climate mismatch; do not analyze a surviving subset as though the full 192-run screen passed. |

The C3 pilot passed these gates for **one viewed seed/cell only**; it does not qualify the new seeds or the other cells. No part of this document asserts that the planned runner or any of the 192 runs already exists.

## Outcomes and descriptive analysis

All outcomes refer to the admitted **24 eligible first-version rice plots aggregated to 12 synthetic UPA**. For each rule triplet, compute alternative minus proportional-demand paired differences using the same cell and seed. Report raw levels as well as differences, by station/year/mapping/ratio/seed. Do not pool plots as if they were independent families.

| Role | Prespecified measure and interpretation |
| --- | --- |
| Primary — production | Total simulated rice tonnes across the 24 eligible plots. Negative alternative-minus-proportional difference means less modeled production. |
| Primary — overall distribution | Unweighted Gini of UPA **relative** production loss, where `relative_loss = 1 − actual_t / full_t` and `full_t = Ym × eligible_area_ha` is an analytical full-ET reference. This is not household income inequality. |
| Primary — smallest UPA | Mean **and maximum** relative loss among **all** UPA tied at the minimum aggregated eligible area (within `1e-9 ha`). The locked cohort has four 2-ha UPA. Report both paired differences; a lower mean can coexist with a higher maximum. |
| Secondary | Unweighted Gini of UPA absolute-tonne loss, gross withdrawal/request/delivery and source mass balance. Source conservation is also an admission requirement, not an efficiency or welfare finding. |
| Historical diagnostic only | The earlier `smallest_quartile_p90_loss` selected three of four tied 2-ha UPA by identifier. Preserve it for legacy comparison, but never substitute it for the all-four mean/maximum or use its old sensitivity envelope as their uncertainty. |

Show scenario-wise and seed-wise paired differences, their ranges and sign reversals, and null or practically small contrasts alongside production–distribution plots. With two fresh seeds and only two selected rainfall years, report **descriptive variation**, not p-values, population confidence intervals, an empirical efficiency–equity frontier, or a policy winner. Stations, years and the two date-label interpretations are uncertainty/scenario axes, **not independent household or Monte Carlo replicates**. The synthetic UPA and their nested plots are not an observed probability sample. Do not select a favorable station, date mapping or seed after inspecting outcomes.

An optional ratio-`1.00` run is a separately registered arithmetic/no-scarcity diagnostic outside these 192 runs. It tests whether the synthetic scheduled requests can be met; it does **not** prove full ET, zero simulated relative loss, or historical adequate-water conditions. `full_t` is not an agent-replayed harvest from that case.

## Evidence ceiling and later work

Observed IDEAM point rainfall conditions one input only; temperature, radiation and ET forcing remain simulator-generated. All four gauges were wetter in 2022 than 2019 in the candidate envelopes, but neither year is an official district-drought classification and rainfall does not fix reservoir stock. The finite supply, weekly demand, efficiency, synthetic crop calendar/geometry and `Ym`/`Ky` are assumed. The model omits explicit flooded-paddy storage, saturation and percolation. Its `SMALL_PLOT_FLOOR` uses **plot** area rather than measured UPA vulnerability or an observed allocation policy.

The C3 simulated yield exceeded the [municipal EVA 2019A benchmark](MUNICIPAL_BENCHMARK_2019A.md) of `3.13 t/ha` by about `1.05–1.06 t/ha` under the three rules. That is an unresolved **external-plausibility warning**, not prediction error: synthetic 96 ha are not an expansion of the municipality's 1,147.5 harvested ha; actual water deliveries, non-rainfall weather, management and response parameters are unmatched. Do not tune `Ym`, `Ky` or stocks to this EVA period and then call the same period independent validation. There are no paired local water-deficit/yield records or district reservoir-release/delivery series to calibrate or validate the institutional effects.

Any precision extension is a **separate future design and authorization**, not an outcome-dependent subset of this screen. If pursued to ten paired seeds while retaining the same 32 weather–stock cells, it would require eight additional pre-fixed seeds and `32 × 3 × 8 = 768` further runs (960 total including this screen), plus seed qualification and the same gates; do not extend only favorable cells. Predeclare any efficiency, weekly-depth, alternative population or `Ym`/`Ky` sensitivity before seeing its result. Recompute `Ym`/`Ky` from audited ETa/ETm only if the ledger contract permits and independently check the arithmetic; changing efficiency, demand or population requires new agent runs and recomputed stock. None of these extensions is authorized by this document.

## Next step

Readiness review: build a manifest-driven, non-overwriting runner; qualify the two locked fresh seeds; confirm input quality/identity and compute budget. Only then seek separate authorization for local matrix execution. The [C3 audit](LOCAL_IDEAM_C3_PILOT_AUDIT.md), [results contract](RESULTS_PROTOCOL.md), [rainfall boundary](DROUGHT_SCENARIOS.md), and [calendar gate](CROP_CALENDAR_PROTOCOL.md) remain the detailed evidence sources.
