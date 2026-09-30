# IDEAM balanced policy-study design lock

## Objective and boundary

Predeclare a balanced, paired technical screen for the three implemented water-allocation rules before observing any further policy outcomes. This work unit creates the design and readiness gates only; it does not qualify new seeds, build a matrix runner, execute simulations, or claim empirical validation.

## Scope and controls

- Authorized branch: `research/water-allocation-evidence`; local documentation changes only.
- Authorized files: a new study protocol, this tracker, and a concise reference in the scenario register. Do not alter frozen inputs, simulator behavior, raw outputs, the Overleaf manuscript, or another paper.
- No remote access, data download, new simulation, push, PR, or merge. Future remote execution requires separately explicit authorization for its operation and credential.
- Route: delegated direct. Trigger: the design requires reconciling more than four evidence/protocol files and writing coordinated documentation.
- TDD: off, based on the current ODD feature context; no code is changed. Applicable checks are structural readback, arithmetic and manifest cross-checks, and `git diff --check`; test/runtime harness: N/A for documentation-only design lock.
- Delivery: `ask-on-risk`; forecast under 300 authored changed lines. The 400-line heuristic is advisory, not a reason to omit methodological detail. No PR authorized.

## Task

- [ ] **P1 — Freeze the post-pilot screen:** Record the 4 stations × 2 years × 2 date-label mappings × 2 binding stock ratios × 3 implemented rules × 2 fresh paired seeds = 192 *new* candidate runs, explicitly separate viewed C3 pilot seed `12345`, and predeclare input hashes, seed/cohort qualification, paired analysis units, outcomes, failure/stopping rules, limitations and conditional extension. Make the scenario register point to the protocol without implying that the study has run. Runtime harness: N/A, no simulations authorized. Rollback boundary: the new protocol/tracker and the scenario-register cross-reference only.

## Acceptance and checks

- Factor arithmetic, per-scenario stock interpretation and input-manifest identity are verified against current files.
- Current corrected all-four-minimum-area-UPA mean and maximum are specified; the old three-of-four P90 remains historical, not a primary endpoint.
- New seeds are fixed before outcomes and require cohort identity qualification before policy runs; a failure stops rather than triggering outcome-guided replacement.
- The protocol distinguishes a technical/descriptive screen from independent replication, calibration, validation, causal effects or a policy winner.
- The C3 pilot stays excluded from the new screen and its outputs remain immutable.
- `git diff --check` and final structural readback pass; no run or source mutation is reported.

## Progress

- Pending protocol write and verification.

## Next step

After this design lock, assess readiness separately. Runner implementation, seed qualification, compute spending and any actual policy matrix require a later authorized work unit.
