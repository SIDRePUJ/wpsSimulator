# Water-allocation handoff: diagnostics captured; policy study on hold

**Resume at the latest `origin/research/water-allocation-evidence`; P14 ended at `cbf07ffecaf18442fe0829458bacc030810f349c`.** The branch was clean and synchronized with its remote before this handoff. The [study protocol](../IDEAM_BALANCED_STUDY_PROTOCOL.md) freezes **192 candidate policy runs**, but none has been executed. Only diagnostic seeds `271828` and `314159` were run in bounded attempts. Their latest P14 transcripts passed structural capture checks; **neither seed is scientifically qualified**. Do not start the policy matrix, touch `main`, merge, or create a PR.

## Read first

1. [ODD task tracker](../../../odd/tasks/ideam-balanced-policy-study.md) — scope, work-unit evidence, active holds and task H1.
2. [Frozen study protocol](../IDEAM_BALANCED_STUDY_PROTOCOL.md) — candidate factors, input hashes, cohort and triplet gates, and claims ceiling.
3. [P14 report](REMOTE_P14_SHUTDOWN_DIAGNOSTIC.md) — latest isolated build, two captures and exact verification evidence. Read [P12](REMOTE_P12_SIDE_EFFECT_CAPTURE_DIAGNOSTIC.md) and [P10](REMOTE_P10_BESA_TEARDOWN_DIAGNOSTIC.md) for the preceding boundaries.

## What is established

| Area | Verified result and limit |
| --- | --- |
| Frozen design | Four stations × two source years × two date mappings × two synthetic stock ratios × three rules × two paired seeds = **192 new runs**. Pilot seed `12345` is excluded. This is a descriptive technical screen, not empirical validation or policy ranking. |
| P8 | First two-seed diagnostic under `/home/ubuntu/wps-water-allocation-4ff0235-build`; seed `314159` had two uncaught BESA thread exceptions despite Java exit 0. Historical output is preserved, not retroactively accepted by the later gate. |
| P9 and P10 | P9 rejects an anchored uncaught Java-thread banner after verifying stderr's captured hash; local gate tests passed **31/31** and launcher tests **11/11**. P10's SHA-gated ancillary `ChannelBESA` teardown patch passed focused local tests **2/2** and compiled in isolated `/home/ubuntu/wps-water-allocation-besa-teardown-v1`. Its two diagnostic captures had zero uncaught banners, but 13 missing-agent warnings per seed and unbound working-directory side effects. |
| P11 and P12 | P11 preserved per-seed snapshots of changed working-directory files without changing simulator cwd; fake-process tests passed **12/12** and offline-gate tests **34/34**. P12 validated that v2 capture with two real diagnostic seeds in `/home/ubuntu/wps-water-allocation-side-effects-v2`: eight side-effect files preserved per seed, all hashes checked, but 13 missing-agent warnings per seed remained. Historical P8/P10 v1 captures are not upgraded. |
| P13 and P14 | P13 made family shutdown retry-safe and idempotent at the notification/kill boundary. Its Java harness, **34/34** gate tests, **12/12** launcher tests and local six-BESA-module/246-WPS-source compile passed. P14 rebuilt current WPS plus patched BESA in `/home/ubuntu/wps-water-allocation-shutdown-v1`; both fixed-seed captures passed P4/P9 v2 and independent hash checks. Missing-agent warnings fell **13 → 1 per seed**, consistent with the shutdown correction but not proof of sole causation or all-schedule safety. |
| Audit continuity | The six water/yield/climate audit CSVs (three per seed) were byte-identical across P8/P10/P12/P14. This is a narrow file comparison, **not** scientific qualification or evidence that policy outcomes were produced. |

Preserve all four isolated targets and their captures. P14 still has one startup `web_wpsViewer` missing-agent lookup and one Java `FINE` `Runtime.exit(0)` trace per seed. Do **not** label either message harmless without a bounded code-and-evidence explanation. The captured runtime identity is `captured_only`, not independent process attestation. Append-only second-seed side-effect snapshots can include first-seed content.

## Why the study is still blocked

The [raw-station audit](../LOCAL_IDEAM_STATION_QUALITY_AUDIT.md) structurally passed, but station chronology and reporting-pattern flags remain unresolved; the 07:00 ZIP date-label semantics and district-scale spatial support are not established. No official observation-level quality flags or paired water-delivery/yield validation data were established. Separately, the current strict result join does **not** by itself enforce complete three-rule triplets and frozen per-run crop windows. Seed/scientific qualification and the policy matrix therefore remain **ON HOLD**, regardless of transcript admission or identical diagnostic CSVs.

## Next safe step and authorization gates

**Next local step:** read-only trace the P14 startup viewer lookup and `Runtime.exit(0)` path against the captured logs and current source; record what is proven, what remains ambiguous and whether a *minimal* local correction is warranted. In parallel or afterward, seek authoritative station/date-label/spatial evidence without modifying the frozen inputs. Do not infer harmlessness or select stations from modeled outcomes.

Any further server transfer, build, probe or run needs **new operation-specific authorization** naming destination, operation and credential/session. Do not reuse the prior P8/P10/P12/P14 authorization as a blanket grant. Before any future run, independently recheck frozen inputs and actual Java/classpath identities, run a zero-write plan, use a fresh exclusive output target, and preserve earlier evidence. A policy matrix additionally requires explicit execution authorization, qualified seeds, complete rule triplets and per-run crop-window gates. Git work stays on the feature branch and is synchronized there; `main`, PR and merge remain out of scope.

The ODD tracker is the local recovery record. Its Engram mirror is **pending** because no registered session identity was available; do not claim a session-attributed save or invent an identity. This handoff is documentation, not approval to execute.
