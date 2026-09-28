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

- [x] **IF-01 — Validate and transform station dates.** The locked ZIP parser rejects duplicate/missing/negative/non-finite records and unexpected 07:00 timestamps; both mappings produce complete 365-day 2022-calendar inputs and reject changed output bytes. Check: three new synthetic tests and two existing POWER tests pass; actual source SHA-256 matches. Work-unit commit: `11ef9a1`.
- [x] **IF-02 — Preserve audit and claim boundary.** Generated 16 ignored station CSVs and committed the aggregate/hash manifest; independent readback verified 16 × 365 rows, dates, all output SHA-256 values and the previously audited 2019/2022 label-date seasonal totals. Two regeneration runs were byte-identical. The protocol retains quality, spatial and day-label caveats. Check: three new and five related Python tests pass, `git diff --check` passes, and `git ls-files experiments/water_allocation/data/raw` is empty. Runtime harness: N/A, data preparation only; no agent simulation. Rollback: remove the generator, its test, aggregate manifest and the preparation-status paragraph without touching historical POWER fixtures. Work-unit commit: `11ef9a1`.

## Acceptance criteria

1. Every station/year/mapping produces exactly one 365-day `date,rain_mm` input without filling missing observations.
2. The manifest identifies the official source and exact output hashes, with no source observations redistributed in Git.
3. Mapping ambiguity and station selection remain explicit; generated files alone do not authorize policy-effect or historical validation claims.

## Progress

- 2026-09-28: Created the tracker before generator changes. Existing four-station 2019/2022 annual coverage is 365/365; source SHA-256 is `FA695160A154A7CE9C95DEE736535A5A8CD9BBCFAFE5A6AA3DACE13A9BD03E1B`.
- 2026-09-28: Completed IF-01/IF-02 in `11ef9a1`, with 16 ignored local CSVs, committed aggregate/hash manifest, passing focused and adjacent tests, independent fixture hash/count readback, and no tracked daily station data.
- Next: obtain station quality/date-label clarification, choose a defensible spatial forcing protocol, generate paired weekly requests and source stocks, then run rule experiments. This task does not authorize or report policy outcomes.
