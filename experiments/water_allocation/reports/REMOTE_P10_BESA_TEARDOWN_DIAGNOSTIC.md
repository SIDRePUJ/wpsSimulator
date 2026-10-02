# P10 isolated BESA teardown diagnostics: transcript gate passed, science on hold

The patched BESA source compiled in a new isolated server target, and one repeat of each fixed diagnostic seed (`271828`, `314159`) passed the current P9/P4 transcript gate. Neither run printed an uncaught Java `Exception in thread` banner. **Seed/scientific qualification remains on hold:** stderr still contains 13 missing-agent messages per seed and a Java `Runtime.exit(0)` trace, and the simulator created eight files outside the capture directories. No policy matrix ran.

## Identity and scope

| Item | Observed identity |
| --- | --- |
| New target | `/home/ubuntu/wps-water-allocation-besa-teardown-v1` |
| Branch source archive | Commit `aee6d6a37595a815a8d96c8eabbaab1166194bd0`; SHA-256 `4ce08f8e99b8f24222191f17490ba57fa108e36393ce92cc764f88b13d84b19f` |
| Source reconstruction | Six frozen files retain their Git-blob LF bytes; only the new target's `src/main/resources/web/data/world.24.json` was restored to its predeclared CRLF SHA-256 `244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c`. All seven frozen hashes passed before and after the runs. |
| BESA patch | Exact ancillary preimage SHA-256 `97b443024441504d2438058534ea84325e61f9d3910b601d5d5391f72caf7b8a`; patched copy SHA-256 `9da3fa66340e0e816c1572aec634798fa488ed9b7fcd2a2d6f2e79817285e06e`. The ancillary checkout was not edited. |
| Build recipe | Target-specific copy of the P6 recipe, SHA-256 `a4520b2e07e63ce42684b3fe3c88a6837aa50d4a0c08ce982b356d705981ddf2`; no old-target path remained. |
| Java | `/usr/lib/jvm/java-21-openjdk-amd64/bin/java`, version `21.0.12.1`, SHA-256 `431f7f67aa26a7f22596f7b1b5eddcaebc024d14f4ed07d39afe669d49048a55` |
| Diagnostic CSV | `/home/ubuntu/wps-water-allocation-besa-teardown-v1/inputs/diagnostic_requests.csv`, SHA-256 `7349e3ca1c2b591de3cc3afdd751a7c73d08450dddce8296b3d10b546b9eeff4` |
| Runtime manifest | `/home/ubuntu/wps-water-allocation-besa-teardown-v1/inputs/build_manifest.json`, SHA-256 `a668c589fcac25489c9881cc81fdc853d6ff8cf9d689392be05e01ff5357bc54` |
| Exclusive output root | `/home/ubuntu/wps-water-allocation-besa-teardown-v1/p10-diagnostics-271828-314159` |

The source archive and copied P6 auxiliary tree were verified before modification in the new target. The copied auxiliary tree initially matched P6's sorted-tree SHA-256 `897cbf5f62d1e88448705457d0a83be8f845305b8c67d1dd3107585bc099d6b1`; only the new copy of `ChannelBESA.java` was replaced by the SHA-gated patch. The unmodified P6 auxiliary tree still had that hash after P10. The P8 build and captures were preserved, not overwritten.

## Build and runtime checks

The first build attempt stopped **before Java compilation** with exit `1` and three `mkdir: No such file or directory` messages: the inherited script expected its `build/` parent to exist. Its log SHA-256 is `6470c804b6a74511773d1a24fad1cf20d7e79c2c29ac89829c9cd7255757c918`. After a separate bounded correction authorized exclusive creation of that parent, the same hashed script ran once:

```bash
set -euo pipefail
T=/home/ubuntu/wps-water-allocation-besa-teardown-v1
test ! -e "$T/build"
test ! -e "$T/incoming/build-corrected.log"
mkdir "$T/build"
start=$(date +%s)
bash "$T/incoming/compile-source.sh" 2>&1 | tee "$T/incoming/build-corrected.log"
end=$(date +%s)
echo BUILD_EXIT_0_ELAPSED_SECONDS=$((end-start))
```

Corrected build exit: `0` in 5 seconds; log SHA-256 `e6f03d2ff30351d96a8b6270b86959d173ccc5e67e46e1a8828f5708f44de557`. It compiled six BESA modules (`89 + 4 + 24 + 15 + 17 + 11 = 160` sources), 245 current WPS sources including the headless replacement, and copied 61 current resources. Static `javap` inspection of `ChannelBESA.class` found both `findPort` overloads, `addPort`, `removePort` and `purgePorts` synchronized. This checks the compiled signature, not every teardown interleaving.

| Built component | SHA-256 |
| --- | --- |
| `ChannelBESA.class` | `60313647dbadf052c0c956884dc0bc667f3e15edd37d0d04314ff6affc0e2f2a` |
| Sorted BESA class tree | `9d6f287cf6ec2d29700b7742cc1c95628c887950ff19efd0d1b016dd589573ae` |
| Sorted WPS class tree | `5fc8e70ab4fcdcc87a3761893e926168e14aa47976444b4ce799f22d29d2cf57` |
| Sorted resource tree | `6c8ed3999ee43e2f12b068bb90072a121a1a0b1225ed0f37082dbff65b3e3726` |

The manifest hashes the actual Java binary and these **15 ordered classpath components**: WPS tree, BESA tree, `cli.jar`, `gson.jar`, `jconvert.jar`, `jfuzzy.jar`, `jgrapht.jar`, `jheaps.jar`, `joda.jar`, `json.jar`, `lbclassic.jar`, `lbcore.jar`, `slf4j.jar`, `yaml.jar`, resource tree. The 12 JARs came from the byte-matched P6 auxiliary copy. P5 recomputed every file/tree hash against the manifest. The frozen seven, CSV, and compiled tree hashes remained stable after execution.

## Diagnostic commands and result

P5 `--plan` was run first with the same paths/options as below. It exited `0` with `status=plan_only`, `processes=0`, `writes=0`, both fixed seeds in order, and the output root absent. The only subsequent launcher invocation switched to `--execute`:

```bash
set -euo pipefail
T=/home/ubuntu/wps-water-allocation-besa-teardown-v1
test ! -e "$T/p10-diagnostics-271828-314159"
mapfile -t cp < "$T/build/runtime-classpath.txt"
test "${#cp[@]}" -eq 15
args=()
for p in "${cp[@]}"; do args+=(--classpath "$p"); done
start=$(date +%s)
set +e
python3 -B "$T/source/experiments/water_allocation/run_ideam_seed_diagnostics.py" \
  --execute --root "$T/source/experiments/water_allocation" \
  --output-root "$T/p10-diagnostics-271828-314159" \
  --java /usr/lib/jvm/java-21-openjdk-amd64/bin/java "${args[@]}" \
  --build-manifest "$T/inputs/build_manifest.json" \
  --diagnostic-source "$T/inputs/diagnostic_requests.csv" --java-option=-Xmx6g
rc=$?
set -e
end=$(date +%s)
echo P10_LAUNCHER_EXIT=$rc P10_ELAPSED_SECONDS=$((end-start))
exit $rc
```

Launcher exit `0`, elapsed 334 seconds, `status=captured_transcripts_only`. Both captures record `termination=natural` and `java_exit=0`. The current P9/P4 checker was then run separately:

```bash
T=/home/ubuntu/wps-water-allocation-besa-teardown-v1
python3 -B "$T/source/experiments/water_allocation/check_ideam_seed_qualification.py" \
  --root "$T/source/experiments/water_allocation" \
  "$T/p10-diagnostics-271828-314159/seed-271828" \
  "$T/p10-diagnostics-271828-314159/seed-314159"
```

Checker exit `0`, `status=diagnostic_transcripts_admitted`, `real_seed_qualification=transcript_only`, `runtime_identity_scope=captured_only` for both seeds. Each had 12 assigned UPA, 24 eligible plots/96 ha, 48 registered plots, 48 water-audit rows, 24 yield-audit rows, and 2,904 climate-audit rows. Every one of the seven captured output hashes matched its corresponding closed file.

| Captured file SHA-256 | Seed 271828 | Seed 314159 |
| --- | --- | --- |
| `capture.json` | `87eef5918f0454aa598f940efd32959be8bddcafd03fc379a460504c245e2796` | `885aa581c2b070faa12762e5e155b461a45212422cd9cc6882887ba80ec7c21e` |
| `command.txt` | `2542410853d2ccbddb8bf806e67908c7ccbf32965431822516c9a2c545398171` | `6fc2b7e19baa9c4d48679a9336164a6d35a1a43906f05a6bb7ae17470775522c` |
| `exit.txt` | `220e046653ca7f5eb515f452c1b0c307868bf02fe87c626598a9dee508a1270a` | `220e046653ca7f5eb515f452c1b0c307868bf02fe87c626598a9dee508a1270a` |
| `stdout.txt` | `8becc62eb309e6a6b6581862bfdd552badc13ac2a9f512c537d706f70185ea32` | `1225ae6ed4c7005843456a7c04fc9e9fbaf7e4f7b9b55433e57456fda6a19531` |
| `stderr.txt` | `6a3fa93f249010353339061e21c092d146b392286b49dfe5dcd6192a37afbbdd` | `cc6bcf473d0ff952f9c998bcf185e84d3b7c62280322ff4fd01286ab09dd1a34` |
| `water_audit.csv` | `45f55a56f9d779c33dc2ab0a98c9541a63f269324b86c06dc168c4b104b1ce72` | `45f55a56f9d779c33dc2ab0a98c9541a63f269324b86c06dc168c4b104b1ce72` |
| `yield_audit.csv` | `a0bd874414d019f5ecf309aaa70c5bb02b367491a4bf7a5298ca5ebdaf373b97` | `ea57eaafb19202174a7bead15aa7ac5d367199948068aa5d2c0df4a542af1916` |
| `climate_audit.csv` | `c994c2fbf21833563d7945005c0acef594c526d06143d2726ccbb6d2395e66c1` | `4383af7277d870d9aaee833e54d3341f5c2cdd5ba0f3218136093253aa7865e2` |

An independent read-only comparison reported byte identity for the **six** P8-versus-P10 audit CSVs (water, yield, and climate for both seeds). This means the observed diagnostics did not change those six outputs; it does not establish that the BESA patch has no effect under other schedules or policies. P8's original captures remain preserved, including its seed-314159 uncaught exceptions. P10 stderr had **zero** uncaught-thread banners for both seeds, but 13 `Agent with alias ... not found` messages per seed plus a Java 21 `FINE` trace for `Runtime.exit(0)`.

## Side effects, limits, and next gate

Against the 466-member branch archive, all source members remained byte-identical except the declared `world.24.json` CRLF reconstruction. The simulator added eight files in the new target's `source/`: `logs/Bank.csv`, `logs/Loans.csv`, `logs/Market.csv`, `logs/NaturalPhenomena.csv`, `logs/PerturbationGeneratorGuard.csv`, `logs/wpsSimulator.csv`, `logs/wpsSimulator.log`, and `rice_water_stress_.csv`. The post-run source-tree SHA-256 is `7db32f4dc04f8f3c91deb96caa5d46a57adee18f31843f1b96d7ebd59e6b6485` (pre-run `dfdb89f4e5f04bba7c1ce4356d015f99f5794c47df5cdd12af9c4b616cac45fc`). Missing-agent warnings and these side effects need a bounded explanation before a policy matrix.

The checker authenticates captured transcript consistency, not independent Java-process attestation, measured weather or farm outcomes, causal attribution to this patch, general BESA race freedom, or scientific calibration/validation. The station-installation chronology, rainfall reporting-pattern flags, date-label semantics and spatial support also remain unresolved. Do not infer seed/scientific qualification or run the predeclared 192-run policy matrix from this transcript gate alone.

Rollback boundary: this report and the P10 tracker update locally; on the server, only `/home/ubuntu/wps-water-allocation-besa-teardown-v1` is P10-owned. Retain it for audit rather than deleting or overwriting it. The older P6/P8 directories and local ancillary `KernelBESA` checkout are outside the P10 rollback boundary.
