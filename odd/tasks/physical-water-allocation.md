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

- [x] **PW-01 — Physical water budget and rules.** Introduced exact mm/ha/m3 conversion, request efficiency, a finite shared source and three batch allocation rules. Check: Java 21 focused harness passes conversion, conservation, cap, zero/sufficient budget and rule assertions. Runtime harness: the same standalone Java main test (no full simulator run). Rollback: remove `org.wpsim.research.water` core and its test without touching legacy behavior. Commit: pending.
- [ ] **PW-02 — Physical rice-yield response.** Add a crop-specific, unit-bearing response to observed/potential ET and potential yield, without relabeling legacy biomass; test zero/full/partial stress and tonnage conversion. Check: Java unit harness and FAO equation traceability. Commit: pending.
- [ ] **PW-03 — Correct the irrigation boundary.** Make plot irrigation target-specific and map delivered volume to applied depth in an opt-in path; leave the default legacy research results reproducible. Check: focused code tests plus build if tooling permits. Commit: pending.
- [ ] **PW-04 — Evidence and sensitivity protocol.** Provide executable or machine-readable verification, calibration/validation split, and sensitivity scenario definitions grounded in the local evidence package; report missing data honestly. Check: file integrity, scripted assertions, no raw data tracked. Commit: pending.

## Acceptance criteria

1. Every research-path input/output has a physical unit and explicit area/efficiency conversion.
2. Allocations cannot exceed the shared water budget, and plot delivery affects only its target.
3. The rice response produces t/ha and t with transparent assumptions, not a renamed legacy model unit.
4. Verification, calibration, independent validation, and sensitivity are distinctly reported; unavailable validation evidence is marked pending.
5. Legacy configuration and existing manuscripts remain unchanged.

## Progress and verification

- 2026-09-26: Inspected code and FAO primary guidance. Existing CROP_IRRIGATION emits a fixed 33 depth-like units and CropLayer broadcasts it to all crop cells. Current biomass factor 0.864 cannot be treated as an SI yield conversion. Java 21 available; Maven not on PATH.
- 2026-09-26: PW-01 focused `javac` plus `java -ea org.wpsim.research.water.SharedWaterSourceTest` passed. Allocation deliberately accepts a complete synchronized batch; individual asynchronous arrivals would bias fairness.
- Next: implement PW-02 physical rice response without changing the legacy biomass path.
