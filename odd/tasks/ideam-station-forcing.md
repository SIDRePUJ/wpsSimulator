# IDEAM station forcing preparation

## Objective

Prepare reproducible, station-specific 2019 and 2022 daily rainfall inputs for the isolated water-allocation study without choosing a gauge, date interpretation or allocation-rule outcome opportunistically.

## Scope and constraints

- Authorized continuation of the user's observed-data-first water-allocation study on branch `research/water-allocation-evidence`.
- Preserve the historical POWER runs and their 2014/2019 selection. Do not run the simulator, claim a municipal rainfall field, or redistribute the IDEAM daily source in Git.
- Four catalogued local stations; two source years; two explicit interpretations of each 07:00 timestamp. Source ZIP stays under gitignored `experiments/water_allocation/data/raw/`.
- Outputs containing daily station values stay under gitignored `data/raw/ideam_derived/`; only generator, tests and an aggregate/hash manifest may be committed.
- TDD mode: no strict TDD configuration found in the existing physical-water-allocation tracker. Functional runner: `python -m unittest discover -s experiments/water_allocation -p 'test_prepare_ideam_rainfall.py'`. No full simulator run for this preparation task.
- Route: inline under the current higher-priority no-subagent restriction. Delivery strategy: `ask-on-risk`; keep the generator, tests, manifest and documentation as one reviewable work unit.

## Tasks

- [ ] **IF-01 — Validate and transform station dates.** Parse the locked IDEAM ZIP, reject duplicate/missing/negative/non-finite daily values and unexpected timestamps, map 2019/2022 to the common 2022 simulation calendar under both date-label interpretations, and refuse to overwrite different output bytes. Check: focused synthetic failure/boundary tests plus 365-day completeness and source hash.
- [ ] **IF-02 — Preserve audit and claim boundary.** Generate ignored station-specific CSVs and a committed aggregate/hash manifest, verify independent 2019/2022 window sums and idempotent regeneration, and document the unresolved station quality/spatial representativeness and day-label status. Check: test suite, `git diff --check`, no raw daily station values tracked. No agent simulation.

## Acceptance criteria

1. Every station/year/mapping produces exactly one 365-day `date,rain_mm` input without filling missing observations.
2. The manifest identifies the official source and exact output hashes, with no source observations redistributed in Git.
3. Mapping ambiguity and station selection remain explicit; generated files alone do not authorize policy-effect or historical validation claims.

## Progress

- 2026-09-28: Created the tracker before generator changes. Existing four-station 2019/2022 annual coverage is 365/365; source SHA-256 is `FA695160A154A7CE9C95DEE736535A5A8CD9BBCFAFE5A6AA3DACE13A9BD03E1B`.
- Next: implement IF-01, then IF-02; commit the complete bounded work unit after observed checks.
