# Physical-unit water allocation

## Objective

Add a physically interpretable, opt-in water-allocation research path to WellProdSim: water in mm and m3, crop output in t/ha and t, and auditable verification/sensitivity boundaries. Preserve the legacy TCSS experiment path and avoid claiming historical validation without observed district deliveries.

## Problem and rationale

The existing irrigation event is a fixed depth-like value broadcast to every crop cell, while the biomass output remains in model units. Neither can support a finite shared-water balance or direct comparison with observed yield data. A small, testable physical kernel and explicit data interfaces are needed before policy experiments.

## Scope and constraints

- Authorized by the user's "ok procede" after discussing physical units, verification, validation and sensitivity.
- Branch/worktree: `research/water-allocation-evidence` at `../wpsSimulator-water-allocation`; do not modify `main`, the seasonal-credit checkout, or manuscripts.
- Do not assert observed allocation-policy effects, household-welfare validation, or district-specific water supply. Do not commit raw DANE data.
- Prefer one-season rice research scenarios and keep legacy output unchanged unless a mode is explicitly enabled.
- TDD mode: no strict TDD setting found; source is repository inspection. Functional runner: Java 21 `javac`/`java` for dependency-free kernel tests; Maven unavailable on PATH (recheck before final). Run focused checks per task.
- Route: inline because current session's higher-priority multi-agent restriction forbids spawning agents absent an explicit user request, despite repository delegation guidance.
- Delivery strategy: `ask-on-risk`; authored changes likely exceed 400 lines, so commit coherent work units and report the branch scope rather than silently creating PRs.

## Tasks

- [x] **PW-01 — Physical water budget and rules.** Introduced exact mm/ha/m3 conversion, request efficiency, a finite shared source and three batch allocation rules. Check: Java 21 focused harness passes conversion, conservation, cap, zero/sufficient budget and rule assertions. Runtime harness: the same standalone Java main test (no full simulator run). Rollback: remove `org.wpsim.research.water` core and its test without touching legacy behavior. Commit: `a54fe6f`.
- [x] **PW-02 — Physical rice-yield response.** Added FAO-33 seasonal relative-yield approximation with explicit `Ym`, `Ky`, ET and harvested area, separate from legacy biomass. Check: Java unit harness passes zero/full/partial stress and tonnes conversion; equation/source and limits in `PHYSICAL_MODEL.md`. Runtime harness: standalone Java main test. Rollback: remove `RiceYieldResponse` and its test/documented equation without changing legacy code. Commit: `e5d3233`.
- [ ] **PW-03 — Correct the irrigation boundary.** Make plot irrigation target-specific and map delivered volume to applied depth in an opt-in path; leave the default legacy research results reproducible. Check: focused code tests plus build if tooling permits. Commit: pending.
- [x] **PW-04 — Evidence and sensitivity protocol.** Added strict paired-output analyzer, parameter-set sensitivity envelopes, synthetic tests and a preregistration-style evidence/claim matrix. Check: Python unit tests and syntax pass, source files are read-only, and raw DANE data are not included. Runtime harness: analyzer test fixture only; no integrated simulations. Rollback: remove analyzer/tests and `RESULTS_PROTOCOL.md`. Commit: `b67e2d8`.

## Acceptance criteria

1. Every research-path input/output has a physical unit and explicit area/efficiency conversion.
2. Allocations cannot exceed the shared water budget, and plot delivery affects only its target.
3. The rice response produces t/ha and t with transparent assumptions, not a renamed legacy model unit.
4. Verification, calibration, independent validation, and sensitivity are distinctly reported; unavailable validation evidence is marked pending.
5. Legacy configuration and existing manuscripts remain unchanged.

## Progress and verification

- 2026-09-26: Inspected code and FAO primary guidance. Existing CROP_IRRIGATION emits a fixed 33 depth-like units and CropLayer broadcasts it to all crop cells. Current biomass factor 0.864 cannot be treated as an SI yield conversion. Java 21 available; Maven not on PATH.
- 2026-09-26: PW-01 focused `javac` plus `java -ea org.wpsim.research.water.SharedWaterSourceTest` passed. Allocation deliberately accepts a complete synchronized batch; individual asynchronous arrivals would bias fairness.
- 2026-09-26: PW-02 focused `RiceYieldResponseTest` passed alongside PW-01. The `6 t/ha` and `Ky=1.1` test inputs are arithmetic fixtures, not locally calibrated values.
- 2026-09-26: PW-03 integration seam inspected. Full simulator compilation is blocked locally: Maven is not on PATH and the complete set of built BESA jars is unavailable, although sibling BESA source checkouts exist. Changing agent guards without a build and a synchronized delivery plan would risk legacy scenarios, so this task remains open.
- 2026-09-26: PW-04 analyzer's three synthetic unit tests passed; no real scenario outputs exist, so no sensitivity result or validation claim has been made.
- 2026-09-26: One-command `python experiments/water_allocation/verify_physical.py` passes both Java and all three Python tests. Corrected small-plot protection to exclude zero-demand plots; changes pending commit.
- Next: obtain a verifiable full build and integrate a complete synchronized allocation batch into plot-specific delivery. Only then run/interpret simulations.
