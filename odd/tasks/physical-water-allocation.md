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
- TDD mode: no strict TDD setting found; source is repository inspection. Functional runner: Java 21 `javac`/`java` for dependency-free kernel tests and direct compilation of all six local BESA modules plus this simulator worktree. Maven 3.9.16 is installed locally, but its POM cannot resolve BESA 3.17/3.17.1 from public Central; the source-build route succeeds. Run focused checks per task.
- Route: inline because current session's higher-priority multi-agent restriction forbids spawning agents absent an explicit user request, despite repository delegation guidance.
- Delivery strategy: `ask-on-risk`; authored changes likely exceed 400 lines, so commit coherent work units and report the branch scope rather than silently creating PRs.

## Tasks

- [x] **PW-01 — Physical water budget and rules.** Introduced exact mm/ha/m3 conversion, request efficiency, a finite shared source and three batch allocation rules. Check: Java 21 focused harness passes conversion, conservation, cap, zero/sufficient budget and rule assertions. Runtime harness: the same standalone Java main test (no full simulator run). Rollback: remove `org.wpsim.research.water` core and its test without touching legacy behavior. Commit: `a54fe6f`.
- [x] **PW-02 — Physical rice-yield response.** Added FAO-33 seasonal relative-yield approximation with explicit `Ym`, `Ky`, ET and harvested area, separate from legacy biomass. Check: Java unit harness passes zero/full/partial stress and tonnes conversion; equation/source and limits in `PHYSICAL_MODEL.md`. Runtime harness: standalone Java main test. Rollback: remove `RiceYieldResponse` and its test/documented equation without changing legacy code. Commit: `e5d3233`.
- [ ] **PW-03 — Correct the irrigation boundary.** Make plot irrigation target-specific and map delivered volume to applied depth in an opt-in path; leave the default legacy research results reproducible. A fixed complete-request schedule and target-specific crop delivery are implemented, but integrated scenario execution, plot matching, output audit, and calibrated physical production remain pending. Check: focused code tests, isolated source build, then an integrated rice scenario. Partial work-unit commit: `ebf4b3e`; task completion pending.
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
- 2026-09-26: One-command `python experiments/water_allocation/verify_physical.py` passes both Java and all three Python tests. Corrected small-plot protection to exclude zero-demand plots in commit `22065bc`.
- 2026-09-26: Installed Apache Maven 3.9.16 under `../../tools` after matching the official SHA-512 checksum. Used `../../tools/maven-public-only-settings.xml` as both user and global Maven settings, with an isolated cache and `mirrorOf=*` restricted to `https://repo.maven.apache.org/maven2/`; no credentials or GitHub Packages were used. `mvn -DskipTests compile` reached dependency resolution but failed because all six declared BESA 3.17/3.17.1 artifacts are absent from the authorized public repository. The local sibling BESA sources are 3.5-era and cannot be substituted silently. The standalone physical verification still passes (two Java tests and three Python tests).
- 2026-09-26: Corrected the overly broad build-blocker conclusion after inspecting `../../scripts/build.ps1` and `../../scripts/build.sh`. All six BESA source trees and local `lib/*.jar` are present. A direct Java 21 build of 160 BESA files and 238 simulator files from this isolated worktree passed under `../../tools/water-allocation-build-20260926`, without overwriting root `bin` or touching the original checkout. The missing Maven artifacts do not prevent this source build.
- 2026-09-26: Added an opt-in fixed-request plan keyed by unique land/world alias (crop-cell ID is only `rice`), allocated in complete chronological batches from a finite source. The crop layer now has a target-specific net-depth API; research mode suppresses legacy household irrigation and rejects mixed legacy events. The plan's synthetic test passes. `CropLayerIrrigationTest` confirms target isolation, unchanged legacy broadcast, and a 10 mm planned delivery reducing next-day root-zone depletion by 10 mm under controlled weather. A fresh direct compilation of 239 simulator source files passed. No full agent run or empirical policy comparison yet.
- 2026-09-26: Added `experiments/water_allocation/verify_source_build.ps1` to reproduce the isolated source build without overwriting shared `bin` JARs. It compiles all six local BESA modules and 239 simulator files, then runs `CropLayerIrrigationTest`; observed PASS. Output is retained under a unique project `tools/water-allocation-build-*` directory for inspection.
- Next: exercise a small rice-only scenario with matching land aliases; audit planned versus planted plots, source withdrawals, crop ET and plot outputs. Connect the separately calibrated FAO-33 yield response before claiming production tonnes or inequality effects. Maintain legacy comparison and report any mismatch honestly.
