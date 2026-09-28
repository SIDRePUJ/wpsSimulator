# Local C2 cohort audit: no admissible trace yet

**Status:** blocked at full-agent execution, not failed or passed crop-calendar validation. No observed plot planting, crop startup, harvest, or allocation-policy outcome can be inferred from these attempts. The raw local diagnostics are retained under ignored `experiments/water_allocation/reports/raw/calendar-c2-local-20260928*` directories.

## Frozen diagnostic setup

- Existing `world.24.json`, `twelve_upa_manifest.csv`, frozen 48-plot roster, seed 12345, one 2022 simulated year, physical water, rice-only cohort, and `-Dwps.water.districtRiceCalendar=true`.
- One synthetic **wiring diagnostic**, not an IDEAM-conditioned demand series: 20 net mm on 7 April 2022 for each of the 24 previously identified first-version plots, plus zero-depth registration rows for 24 later versions on 1 January; delivery efficiency 1 and non-scarce 19,200 m³ source. Fixture SHA-256: `7349e3ca1c2b591de3cc3afdd751a7c73d08450dddce8296b3d10b546b9eeff4`.
- One `PROPORTIONAL_DEMAND` rule solely to initialize the physical plan and yield ledger; **no policy comparison**. Potential yield 5 t/ha and `Ky=1` are technical ledger inputs, not calibrated values. Exact Java commands are in each ignored run's `command.txt`.

## Invocation record

| Attempt | Change to invocation | Observed result | Verdict |
| --- | --- | --- | --- |
| `calendar-c2-local-20260928` | Initial command lacked `-env local`. | Natural Java exit **1** before simulation: missing `server_null_single.xml`. | Setup failure; no cohort trace. |
| `calendar-c2-local-20260928-setup-corrected` | Added only `-env local`; still lacked `-land 2`. | Startup reported **0 farms, 0 lands**; process then remained idle for about five minutes, with BESA threads waiting. Interrupted; wrapper exit **1**, not a natural model audit verdict. | Setup failure; `CivicAuthorityState.createFarms()` requires `params.land == 2` for two-cell small farms. |
| `calendar-c2-local-20260928-land-corrected` | Added only `-land 2`; fixture unchanged. | Startup reported **12 small farms, 24 lands**, then remained idle for about four minutes. A local thread dump showed the main thread gone and BESA threads waiting. Interrupted; wrapper exit **1**, not a natural model audit verdict. No year-end water, yield, or climate CSV was written. | Full-agent harness did not complete. Do not interpret startup topology as crop-world verification. |

The corrected invocation's essential arguments were `-env local -mode single -agents 12 -world 24 -land 2 -years 1 -startyear 2022 -seed 12345 -perturbation none`, with the research switches and diagnostic paths recorded above. It used the already compiled isolated build; no code or model parameter was altered after observing outcomes. The two corrections addressed pre-outcome startup errors only.

## C2 acceptance accounting

| Required observation | Result |
| --- | --- |
| Farm topology | 12 small farms / 24 lands reported at startup in the final attempt; no completed farm-assignment audit. |
| 24 first-version plot IDs, 96 rice ha, 4/5/3 UPA area classes | **Unverified**; these are expected from the frozen manifest/roster, not observed in this run. |
| January–March planting date minimum/maximum | **Unavailable**; no completed `WATER_PLANT` trace. |
| 48 annual crop worlds, crop type, plot-owner links | **Unverified**; no completed registration/owner audit. |
| Harvests and crop-response ledger | **Unavailable**; no year-end yield ledger. |
| Rainfall and water consistency | **Unavailable**; no year-end climate or water audit. |

**Next gate:** diagnose the local full-agent startup/shutdown problem without changing planting dates or diagnostic demand based on policy outcomes. Then rerun a fresh, immutable diagnostic invocation and require natural exit 0, complete year-end audits, and first-version crop startup/harvest before generating new IDEAM-conditioned schedules. The old February/April technical cohort and requests remain unsuitable as a district-calendar validation claim.
