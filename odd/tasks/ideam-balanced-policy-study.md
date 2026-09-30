# IDEAM balanced policy-study design lock

## Objective and boundary

Predeclare a balanced, paired technical screen for the three implemented water-allocation rules before observing any further policy outcomes, then establish local, read-only input readiness. These work units do not qualify new seeds, build a matrix runner, execute simulations, or claim empirical validation.

## Scope and controls

- Authorized branch: `research/water-allocation-evidence`; local design documentation and a read-only P2 verification utility only.
- P1 authorized documentation files were the study protocol, this tracker, and the scenario register. P2 additionally authorizes one read-only input checker, focused tests, and a short protocol note. Do not alter frozen inputs, simulator behavior, raw outputs, the Overleaf manuscript, or another paper.
- No remote access, data download, new simulation, push, PR, or merge. Future remote execution requires separately explicit authorization for its operation and credential.
- Route: delegated direct. Trigger: the design and checker require reading more than four evidence/code files and coordinated non-trivial writes.
- TDD: off, based on the current ODD feature context; P2 still requires focused tests. P1 checks were structural readback, arithmetic, manifest cross-checks and `git diff --check`. P2 runner: `python -m unittest discover -s experiments/water_allocation -p test_check_ideam_readiness.py` from the repository root, followed by the read-only checker CLI and `git diff --check`.
- Delivery: `ask-on-risk`; the user selected `feature-branch-chain`, with all work on `research/water-allocation-evidence` and no change to `main`. P1 authored 120 changed lines; P2 exceeds the 400-line PR budget as a cohesive checker-plus-tests unit. The 400-line per-task heuristic is advisory, not a reason to omit tests or detail. No PR or remote push is authorized by this tracker.

## Task

- [x] **P1 — Freeze the post-pilot screen:** Record the 4 stations × 2 years × 2 date-label mappings × 2 binding stock ratios × 3 implemented rules × 2 fresh paired seeds = 192 *new* candidate runs, explicitly separate viewed C3 pilot seed `12345`, and predeclare input hashes, seed/cohort qualification, paired analysis units, outcomes, failure/stopping rules, limitations and conditional extension. Make the scenario register point to the protocol without implying that the study has run. Runtime harness: N/A, no simulations authorized. Rollback boundary: the new protocol/tracker and the scenario-register cross-reference only.
- [x] **P2 — Verify locked IDEAM inputs read-only:** Implement a checker that proves the current source, roster, world, crop windows and manifest identities, all 16 rainfall/request file hashes and key shapes, per-scenario crop-window/area/demand arithmetic, and 0.35/0.65 stock arithmetic without regenerating or modifying inputs. Add deterministic malformed/mutated fixtures that fail closed. Report descriptive rain extrema and unresolved quality limits without claiming meteorological certification. Record the exact checker result on present local inputs. Runtime harness: checker CLI only, no full-agent simulation. Rollback boundary: checker, focused tests, and narrow protocol note; frozen inputs remain untouched.

## Acceptance and checks

- Factor arithmetic, per-scenario stock interpretation and input-manifest identity are verified against current files.
- Current corrected all-four-minimum-area-UPA mean and maximum are specified; the old three-of-four P90 remains historical, not a primary endpoint.
- New seeds are fixed before outcomes and require cohort identity qualification before policy runs; a failure stops rather than triggering outcome-guided replacement.
- The protocol distinguishes a technical/descriptive screen from independent replication, calibration, validation, causal effects or a policy winner.
- The C3 pilot stays excluded from the new screen and its outputs remain immutable.
- `git diff --check` and final structural readback pass; no run or source mutation is reported.

## P2 acceptance and checks

- Focused tests pass, including mismatched hash, missing/duplicate date or plot, invalid crop-window, negative depth, and stock/demand inconsistency cases.
- The CLI is read-only and checks the 16 locked station/year/date-mapping pairs against manifests; missing or altered inputs fail nonzero. No preparation script, simulation, data download, remote access or output rewrite is invoked.
- The checker distinguishes structural/identity success from unresolved station quality, date-label semantics and spatial representativeness.
- Record exact test and CLI results, input identities and `git diff --check` in this tracker before marking P2 complete.

## Progress

- Work-unit commit: `8560e4c2ae2c1d7edad3de6fba00a89445938003` (`docs(water-allocation): lock balanced IDEAM study design`). It adds `experiments/water_allocation/IDEAM_BALANCED_STUDY_PROTOCOL.md`, updates the current IDEAM status in `SCENARIO_REGISTER.md`, and creates this tracker.
- Structural verification passed: 4 × 2 × 2 × 2 × 3 × 2 = 192 new candidate runs; the 16-scenario request manifest SHA-256 is `dfd0766a42a12ad0954d44c617408ba764db4dc273b4f0cbd20eb9071065db80`, rainfall manifest SHA-256 is `fa937f9486d9f8bb9ee1ab944b8f205449863ced71769006a55840454a9194d6`, and the frozen cohort/window hashes match the protocol. Eleven relative links were checked, source claims read back, and staged `git diff --check` exited 0. Test and runtime harness: N/A, documentation-only design lock; no simulation or seed qualification ran.
- Receipt-driven development is off by the default user-owned switch. Native risk assessment of the committed work unit returned `passive` (`non_executable_only`, `review_due=false`, reason `passive`); structural readback was the applicable check. No review, PR, push or merge was performed.
- Execution remains blocked on input-quality review, qualification of seeds `271828`/`314159` against frozen crop windows, a manifest-driven non-overwriting runner, and separate authorization for compute and any remote access.
- P2 independent verification: `python -m unittest discover -s experiments/water_allocation -p test_check_ideam_readiness.py` passed 7 tests; `python experiments/water_allocation/check_ideam_readiness.py` returned `structural_identity_pass` for all 16 scenarios, `station_quality_certified=false`, maximum daily rain 87.8 mm and no day at or above 100 mm. Forty-one input hashes were unchanged before/after (`874ad27503a3f693d4dbef9f498dcf8bd7b16a956457be3515a37817e00d9768`); parent CLI spot check and staged `git diff --check` passed. This verifies structural identity only, not station quality or date-label semantics; no seed/policy run occurred.
- P2 is a cohesive checker, its tests and protocol note; an honest split cannot separate the tests from the checked behavior merely to fit a PR budget. A future PR for this unit needs explicit `size:exception` approval or a genuinely independent slice. The Engram tracker mirror remains pending unavailable session-attributed memory write capability.

## Next step

Keep the verified P2 local input check on the feature branch and synchronize its Engram mirror when a registered runtime session is available. Seed qualification, runner implementation, compute spending and any actual policy matrix require later bounded work units; the future matrix gate must separately enforce complete rule triplets and frozen per-run crop windows, because the current strict join alone does not prove either.
