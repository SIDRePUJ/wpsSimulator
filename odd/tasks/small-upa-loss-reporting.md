# Small-UPA loss reporting

## Objective and boundary

Report relative-loss mean and maximum over **all** UPA tied for the minimum eligible area, rather than interpreting an identifier-tiebroken three-of-four quartile as the complete small-UPA class. Reanalyze only the already-admitted C3 pilot from its existing joined output. Preserve the original hashed report and all simulation inputs and raw outputs.

## Scope and controls

- Authorized branch: `research/water-allocation-evidence`; local worktree only.
- Authorized change: UPA analysis, focused tests, a new ignored C3 analysis report, and closely related evidence/protocol documentation. No new simulation, data download, remote execution, Overleaf manuscript edit, push, PR, or merge.
- Retain legacy quartile fields for older plot diagnostics and reproducibility; add explicit all-minimum-area-ties UPA fields instead of silently relabeling the old P90.
- Route: delegated direct. Trigger: coordinated non-trivial analyzer, test, and evidence-document edits; reading prepares the write.
- TDD: off; no explicit strict-TDD setting for this ODD feature. Focused runner: `python -m unittest discover -s experiments/water_allocation -p test_analyze_upa_results.py` from repository root.
- Delivery: `ask-on-risk`; forecast approximately 120–220 authored changed lines, below the 400-line planning heuristic. No PR authorized.

## Task

- [x] **M1 — Define and recalculate small-UPA losses:** Add a tie-inclusive minimum-area UPA count, mean relative loss, and maximum relative loss to UPA summaries and paired contrasts. Test a cohort whose fourth tied-smallest UPA has the worst loss. Reanalyze the frozen C3 `joined.csv` into a *new* ignored report without overwriting `upa_report.json`; independently check the four-member group, paired cohort, and reported numbers. Update the C3 audit and metric protocol/registry with the new definition while retaining the legacy pilot values as historical output. No new policy run. Rollback boundary: UPA analyzer/test additions and the related metric documentation; the new ignored report may be removed without affecting frozen inputs or the original report.

## Acceptance and checks

- Focused UPA tests pass, including the tied-area regression case; broader analyzer tests run if shared behavior changes.
- New report comes only from the existing admitted C3 joined table; original joined table and legacy report hashes remain unchanged.
- All four 2-ha UPA are included per rule, with mean and maximum relative loss and paired contrasts recorded accurately.
- `git diff --check` passes; no simulator run or manuscript edit is reported as performed.
- Runtime harness: existing-input UPA reanalysis only; record the exact command, exit status, output path, and audit hash. No full-agent simulation is applicable.

## Progress

- Work-unit commit: `da29c11b2603f2ca6644731628166bc76113e4c3` (`fix(water-allocation): include tied smallest UPA in loss metrics`). It changes the UPA analyzer, focused test, C3 audit, results protocol, and scenario register. The generic quartile fields remain unchanged.
- Focused check: `python -m unittest discover -s experiments/water_allocation -p test_analyze_upa_results.py` passed 8/8; the generic analyzer suite passed 4/4. An independent verifier reran the focused command (8/8) and independently recomputed all small-UPA values and paired contrasts from the frozen joined CSV. `git diff --check` passed before the work-unit commit.
- Runtime harness: existing-input reanalysis only, exit 0: `python experiments/water_allocation/analyze_upa_results.py experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/joined.csv --output experiments/water_allocation/reports/raw/pilot-c3-29030080-2019-label-065/upa_report_all_smallest.json`. New ignored report SHA-256: `775b72d7f40f3d61d8c012d1ecfa0ea4987c33eab81ac228e3a0a428da951d8a`. The frozen joined CSV (`b0e6d915836578b2a3e6194f996642917a4012cc8e35501bcff0beed64f525f3`) and historical report (`462d42ccf40c9c928bad0f8acd72d20fec3d27e9d053f3ac2f8e7627309ba974`) retain their audited hashes. No full-agent simulation ran.
- Under `SMALL_PLOT_FLOOR`, the four-small-UPA mean loss is `0.108828` versus `0.162213` for `PROPORTIONAL_DEMAND`, while the maximum is `0.168504` versus `0.163347`. This reverses the former three-of-four tail impression for the worst small UPA. The C3 pilot remains a single descriptive technical cell, not empirical validation or a paper-wide result.
- Receipt-driven development is off by the default user-owned switch (`disabled/unmanaged`); no native review was started. A native risk-assessment attempt could not classify the commit because the untracked task file required an explicit inventory declaration, so the verification path treated it as unassessable/high and used an independent verifier. No PR or delivery gate was invoked.

## Next step

Predeclare the balanced policy-study matrix using the corrected all-four-small-UPA metrics before running any new scenarios. Reconcile the Overleaf manuscript's hypotheses and metric definitions separately; neither action is authorized by this completed metric work unit.
