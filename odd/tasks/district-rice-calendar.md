# District rice calendar control

## Objective and boundary

Add an opt-in research calendar guard so first rice plantings can occur only in the historically reported January–March María La Baja district window. This is a synthetic timing constraint, not measured 2019 or 2022 planting dates or validation of the 24-plot cohort. The legacy/default simulator and existing February/April technical fixtures must remain unchanged.

## Scope and controls

- Authorized branch: `research/water-allocation-evidence`; local worktree only.
- Keep the 12-family/24 first-planting-eligible-plot, 96-ha research cohort and its 48 annual world trace contract as audit expectations, not assumed outcomes.
- Local diagnostic full-agent runs are allowed for cohort verification; no policy matrix, remote execution, data downloads, push, PR, merge, manuscript edit, or historical relabeling.
- Route: delegated direct. Trigger: coordinated edits across policy, agent gates, tests, and documentation.
- TDD: off; no explicit strict-TDD setting for this ODD feature. Focused Java main-method tests plus isolated source build are the available runner. Applicable build: `experiments/water_allocation/verify_source_build.ps1`.
- Delivery: `ask-on-risk`; forecast approximately 120–220 authored lines for this work unit, below the 400-line delivery heuristic. No PR authorized.

## Tasks

- [x] **C1 — Calendar guard:** Add opt-in physical-water-only first-planting eligibility, guard preparation and actual planting, and focused tests. Accept only January–March for first planting; keep later-cycle and legacy paths unchanged. Verify focused policy tests, isolated build, and unchanged legacy outcomes at policy boundary. Runtime harness: N/A, full simulation deliberately deferred. Rollback boundary: calendar policy/agent-gate/test edits only.
- [ ] **C2 — Cohort verification:** In a separately authorized unit, observe `WATER_PLANT`/harvest/owner traces under the new mode, ensure the exact cohort and annual trace survive, and then regenerate matched requests/stocks. No paper matrix before this gate.

## Progress

- C1 verified: standalone `ResearchCropPolicyTest PASS`; `verify_source_build.ps1` compiled 245 WellProdSim files and `CropLayerIrrigationTest PASS`; `git diff --check` passed. Tests cover opt-in and legacy policy boundaries. No end-to-end crop trace was run. Work-unit commit: `d560fe7` (`feat(water): guard district rice planting window`).
- C2 pending; no claim that planting dates, 48 annual worlds, or empirical crop response are verified.
- C2 **cohort-trace subgate passed** on one corrected local diagnostic: natural Java exit 0; 12/12 assigned UPA, 24 first-version rice plots/96 ha, 4/5/3 UPA area classes, first plantings 1–14 February 2022, 48/48 annual rice worlds, 24/24 first-version harvests, 24/24 climate plots (2,904 rows), and zero missing water deliveries. See `experiments/water_allocation/LOCAL_CALENDAR_C2_AUDIT.md` for all four attempts, command/fixture hashes and limits. The first three attempts had setup/launcher faults, not admissible cohort outcomes. **C2 remains open:** matched IDEAM-conditioned requests and stocks, repeated discovery/forcing, and a full policy matrix have not been run. Technical dates are not observed district planting dates.
