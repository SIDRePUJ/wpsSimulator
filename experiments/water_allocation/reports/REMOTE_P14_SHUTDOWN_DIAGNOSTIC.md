# P14 isolated shutdown rebuild: warnings reduced, science on hold

The current branch, including P13 `ShutdownGate`, compiled from source in a new server target. One diagnostic run of each fixed seed passed the P4/P9 v2 captured-transcript gate. Missing-agent messages fell from **13 to 1 per seed** compared with P12; the remaining message names the startup `web_wpsViewer` lookup. This is a bounded observation, not general thread-safety or scientific seed qualification. No policy matrix ran.

## Identity and boundaries

| Item | Observed identity |
| --- | --- |
| Exclusive target | `/home/ubuntu/wps-water-allocation-shutdown-v1`; absent before creation |
| Branch source archive | Commit `e54f054f34b6ce9f9eb20b40cfea8bec964964a5`; `git -c core.autocrlf=false archive`; SHA-256 `b2c158a02cf23d4a44f3d6aa2a9c87585aa5db4ee5e3274bfe14a31a0281d548` |
| Frozen source | All seven P4 hashes matched before compilation and after execution. Only the new target's `world.24.json` was restored to its declared CRLF SHA-256 `244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c`. |
| Diagnostic CSV | `inputs/diagnostic_requests.csv`, SHA-256 `7349e3ca1c2b591de3cc3afdd751a7c73d08450dddce8296b3d10b546b9eeff4` |
| Auxiliary inputs | P10 `aux/` copied byte-for-byte into the exclusive target (`diff -qr` exit 0). P6 ChannelBESA preimage SHA-256 `97b443024441504d2438058534ea84325e61f9d3910b601d5d5391f72caf7b8a`; copied P10 patched source postimage `9da3fa66340e0e816c1572aec634798fa488ed9b7fcd2a2d6f2e79817285e06e`; headless replacement `7ad27f4a6e3a174bb722e9ddc6fc61e16a09322d7a9ea8df36691937b5f1d77b` |
| Build recipe | Target-specific copy of the P10 six-module source recipe, SHA-256 `d71f72c5c233b76ba8fdb45cc307cdf8d7dc8ca3a578a4f3c97586938546be7e`; no P10 target path remained |
| Java | `/usr/lib/jvm/java-21-openjdk-amd64/bin/java`, version `21.0.12.1`, SHA-256 `431f7f67aa26a7f22596f7b1b5eddcaebc024d14f4ed07d39afe669d49048a55` |
| Runtime manifest | Actual Java plus 15 ordered new-target classpath components, `inputs/build_manifest.json` SHA-256 `8ec23507da0ea79923d9b6a5844d24b9b0d8d648f1c33e72d6d8e21851180b31` |
| Exclusive outputs | `/home/ubuntu/wps-water-allocation-shutdown-v1/p14-diagnostics-271828-314159` |

The archive and transferred CSV/CRLF world file matched local SHA-256 values before extraction. The archive identified the exact branch commit; `ShutdownGate.java` was present in the extracted source (SHA-256 `4b1fff056a0ff072d306907e2214c9d6904d8d84c333ac485645491133b0d68b`). The new source working directory began without `logs/` or `rice_water_stress_.csv`. An initial read-only SSH preflight connection closed without a result; its bounded retry established the target was absent before any write. No old target, ancillary checkout, `main`, or prior capture was written; post-run readback matched the four recorded P10/P12 `capture.json` hashes.

## Build and diagnostic checks

The new target's `build/` was empty before compilation. The recipe compiled all six BESA modules (`89 + 4 + 24 + 15 + 17 + 11 = 160` sources) and **246** current WPS sources including the headless replacement and P13 gate, then copied 61 resources. The exact build invocation was `bash /home/ubuntu/wps-water-allocation-shutdown-v1/incoming/compile-source.sh 2>&1 | tee /home/ubuntu/wps-water-allocation-shutdown-v1/incoming/build.log` under `set -euo pipefail`. Exit 0, elapsed 5 seconds; build log SHA-256 `ffb3a422cbfc26bb0dcbccd98ab60d80efef3493a16fe9a69cd0834e68c9b765`. Compiler warnings concerned deprecated and unchecked API use; they did not fail compilation.

| New build component | SHA-256 |
| --- | --- |
| `ChannelBESA.class` | `60313647dbadf052c0c956884dc0bc667f3e15edd37d0d04314ff6affc0e2f2a` |
| `ShutdownGate.class` | `3594d9fc99a0b461fd47a2d2249a2fde0530c3022ab07bb945054934b6776b7a` |
| `PeasantFamily.class` | `5b69da5b20a065af69c4dff1e5bd642f7176ed6cfe8c3820e0b5bb6a7ecc40f8` |
| Sorted BESA class tree | `9d6f287cf6ec2d29700b7742cc1c95628c887950ff19efd0d1b016dd589573ae` |
| Sorted WPS class tree | `3f318e101d8f0926bf81c796ed8ca194bd6a481c86825a9d6331d0eddb1a3461` |
| Sorted resource tree | `6c8ed3999ee43e2f12b068bb90072a121a1a0b1225ed0f37082dbff65b3e3726` |

P5 plan used the exact execution arguments below with `--plan`; exit 0, `status=plan_only`, `writes=0`, `processes=0`, seeds in declared order, and output root still absent. The launcher verified frozen inputs, CSV, actual Java and all 15 ordered classpath bytes against the manifest.

```bash
set -euo pipefail
T=/home/ubuntu/wps-water-allocation-shutdown-v1
test ! -e "$T/p14-diagnostics-271828-314159"
test ! -e "$T/source/logs"
test ! -e "$T/source/rice_water_stress_.csv"
mapfile -t cp < "$T/build/runtime-classpath.txt"
test "${#cp[@]}" -eq 15
args=()
for p in "${cp[@]}"; do args+=(--classpath "$p"); done
python3 -B "$T/source/experiments/water_allocation/run_ideam_seed_diagnostics.py" \
  --execute --root "$T/source/experiments/water_allocation" \
  --output-root "$T/p14-diagnostics-271828-314159" \
  --java /usr/lib/jvm/java-21-openjdk-amd64/bin/java "${args[@]}" \
  --build-manifest "$T/inputs/build_manifest.json" \
  --diagnostic-source "$T/inputs/diagnostic_requests.csv" --java-option=-Xmx6g
```

The sole execute invocation exited 0 after 335 seconds, with `status=captured_transcripts_only` and natural Java exit 0 for seeds `271828` and `314159`. The P4/P9 v2 checker was then run separately:

```bash
T=/home/ubuntu/wps-water-allocation-shutdown-v1
python3 -B "$T/source/experiments/water_allocation/check_ideam_seed_qualification.py" \
  --root "$T/source/experiments/water_allocation" \
  "$T/p14-diagnostics-271828-314159/seed-271828" \
  "$T/p14-diagnostics-271828-314159/seed-314159"
```

Checker exit 0: `diagnostic_transcripts_admitted`, `real_seed_qualification=transcript_only`, `runtime_identity_scope=captured_only`, `side_effect_scope=captured_per_seed`. Each capture has 12 assigned UPA, 24 eligible plots, 96 ha and 48 registered plots. Independent post-run readback verified seven frozen hashes, Java plus 15 classpath hashes, all **470** archived regular source files except the declared CRLF world reconstruction, and each preserved side-effect file's SHA-256. All eight second-seed *before* hashes equal the first-seed *after* hashes.

| Seed | `capture.json` SHA-256 | Side effects preserved | Missing-agent messages | Uncaught-thread banners | Audit CSVs vs P12 |
| --- | --- | ---: | ---: | ---: | --- |
| 271828 | `d08cf1cf4bd5d1d3dd1eed48c0a5e0f0d74d53f2ac6b406e1a7e567639d25b07` | 8 | 1 | 0 | water, yield and climate byte-identical |
| 314159 | `2c484aa0246b5a86194b40310d3a3a7b987b48b56b6f42cac75bd77639bcdad2` | 8 | 1 | 0 | water, yield and climate byte-identical |

The eight files per seed were `logs/{Bank,Loans,Market,NaturalPhenomena,PerturbationGeneratorGuard,wpsSimulator}.csv`, `logs/wpsSimulator.log`, and `rice_water_stress_.csv`. Captured per-seed bytes and hashes passed the v2 checker and independent readback. Both stderr files contain only one missing-agent message, `web_wpsViewer` from `wpsReport.info`, rather than P12's 13 messages each. Each still has one Java `FINE` `Runtime.exit(0)` trace. Six water/yield/climate audit CSVs are byte-identical to P12. This comparison is limited to these two runs and recorded files.

## Limits and next gate

The P13 idempotent shutdown correction is consistent with removal of the 12 family-shutdown lookup warnings; this comparison does not prove sole causation under all schedules. The remaining viewer lookup and Java exit trace need a bounded explanation. Append-only second-seed side-effect snapshots include first-seed content; captured consistency is not independent process attestation or a scientific outcome. Station chronology, rainfall reporting patterns, date-label semantics and spatial support remain unresolved. Seed/scientific qualification and the 192-run policy matrix stay **ON HOLD**.

Rollback boundary: this P14 report/tracker update locally and the exclusive new server target, retained for audit. No deletion, PR, merge or `main` change is part of P14.
