# Water-allocation evidence package

## Objective

Prepare a reproducible, source-backed public-data package for a finite shared-irrigation-water study centered on Maria La Baja, Bolivar. Keep this study isolated from the seasonal-credit branch and do not change simulator behavior yet.

## Problem and rationale

The proposed equity-efficiency comparison needs observed farm heterogeneity, crop production, and drought forcing. Available public sources do not jointly observe plot-level delivered irrigation volumes and yields. The package must separate measured inputs from hypothetical allocation policies.

## Scope and constraints

- Authorized: download public DANE, UPRA, and IDEAM data/reports; profile acquired data; document provenance, coverage, limitations, and a feasible calibration/validation workflow.
- Excluded: simulation runs, simulator code changes, confidential data, remote Git operations, and claims of observed policy effects.
- Worktree: `../wpsSimulator-water-allocation`, branch `research/water-allocation-evidence`, based on `main` at `330e8ef`.
- Evidence folder: `experiments/water_allocation/`.
- Estimated authored change: approximately 200–350 lines of documentation/scripts, excluding third-party downloaded files. Delivery strategy: `ask-on-risk`.
- TDD: no project TDD mode or test runner was established for this evidence-only work. The two data-preparation scripts received syntax, idempotent-fetch, archive, hash, and aggregate-assertion checks; no simulator tests or simulations were run.

## Tasks

- [x] **WA-01 — Acquire official sources.** Downloaded 12 public DANE, EVA, IDEAM catalog, NASA POWER, and ADR files. Exact URLs and 2026-09-26 sizes/SHA-256 hashes are in `fetch_sources.py` and `source_profile.json`; terms and the failed DANE methodological-sheet endpoint are in `README.md`. Route: inline, source URLs and folder layout were identified. Check: all 12 files exist, hashes match, and census ZIP passes integrity test. Commit: `779fc84`.
- [x] **WA-02 — Profile local study viability.** Profiled DANE UPA/crop/household joins and missingness, EVA municipal rice and calendar coverage, IDEAM station metadata, and NASA daily coverage. Route: inline, bounded data inspection in one experiment folder. Check: reproducible aggregate profile, record-count assertions, and manual spot check. Actual IDEAM station time series remain unavailable and are explicitly not treated as acquired. Commit: `779fc84`.
- [x] **WA-03 — Synthesize a minimum evidence design.** `EVIDENCE.md` separates observed aggregates from allocation counterfactuals, states the rice discrepancy, calibration/validation gates, sensitivity parameters, and threats. Route: inline, one concise report. Check: source-linked assertions and explicit gaps. Commit: `779fc84`.

## Acceptance criteria

1. The evidence package can be retrieved and audited from official sources without another research search.
2. The local sample/crop choice is justified by observed coverage, not assumed from metadata.
3. The report does not present municipal or climatic data as household-level allocation validation.
4. The seasonal-credit checkout, main branch, and other manuscripts remain unchanged.

## Progress and verification

- 2026-09-26: Created isolated worktree from `main`; initial branch was clean.
- 2026-09-26: Verified 12 downloaded-file SHA-256 hashes and ZIP integrity; profile totals and rice-household linkage assertions pass. Re-run of `fetch_sources.py` skipped all existing files; both scripts compile. The first PowerShell fetch helper was not executable under the host policy and was replaced by the checked Python helper.
- Next: obtain actual IDEAM station series and district delivery/storage data if accessible; reconcile or bracket the 2013 CNA/EVA rice discrepancy before any allocation-model calibration. No simulator implementation or run is authorized in this task.
