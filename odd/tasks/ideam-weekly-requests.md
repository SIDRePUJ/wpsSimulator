# IDEAM-conditioned weekly requests

## Objective

Prepare auditable, station-specific weekly irrigation requests for every locked IDEAM rainfall variant, without running allocation rules or publishing daily station-derived values.

## Scope and constraints

- Work on `research/water-allocation-evidence`; preserve historical POWER requests and simulator results.
- Four stations × 2019/2022 × both 07:00 date mappings; no favored station or mapping.
- Existing 48-plot roster, 24 eligible crop windows, and request parameters (30 mm target, 0.8 rain factor, 0.48 delivery efficiency) remain fixed; these are scenario assumptions, not measured deliveries.
- Request CSVs can reveal daily rainfall through the demand formula and must stay in ignored `data/raw/ideam_derived/`; commit only aggregate quantities and hashes.
- No strict TDD mode is configured in the existing tracker. Run focused Python unit tests and independently check generated hashes/row counts. No agent simulation or remote transfer.
- Route: inline under current no-subagent restriction. Delivery strategy: `ask-on-risk`; one self-contained code/tests/manifest/docs work unit, with 400 authored lines advisory only.

## Tasks

- [x] **IR-01 — Batch preparation with provenance.** The generator validates the 16 locked rainfall identities and hashes, the common roster/windows, then writes immutable ignored request CSVs and one aggregate/hash manifest. Three focused tests pass, covering missing variant, changed rainfall, idempotence and overwrite refusal.
- [x] **IR-02 — Audit and boundary.** Two local regenerations were byte-identical. Independent readback verified 16 schedules × 408 rows, request hashes, physical gross-demand arithmetic and all 0.35/0.65/1.00 stock calculations. Three adjacent weekly-request tests passed; `git diff --check` passed; `git ls-files experiments/water_allocation/data/raw` is empty. Runtime harness: N/A, request preparation only; no agent simulation or remote transfer. Rollback: remove the batch generator, focused test, aggregate manifest and request-status paragraph; historical POWER schedules remain untouched.

## Acceptance criteria

1. Exactly 16 station/year/mapping request schedules with the same cohort and parameters.
2. Manifest links each request SHA-256 to its rainfall SHA-256 and records gross m3 plus 0.35/0.65/1.00 scenario stocks.
3. No source, rainfall, or request daily values are tracked in Git; no simulator run or empirical allocation claim.

## Progress

- 2026-09-28: Tracker created before source changes.
- 2026-09-28: Completed IR-01/IR-02. Across all date mappings, synthetic gross demand spans 430,320–563,120 m³ (2019) and 50,000–325,600 m³ (2022); these are model-derived demands, **not** observed source water. Final work-unit commit recorded after commit creation.
- Next: resolve or bracket station quality and ZIP date labeling, freeze paired-rule matrix, then run simulator. Do not call these schedules empirical delivery or validation data.
