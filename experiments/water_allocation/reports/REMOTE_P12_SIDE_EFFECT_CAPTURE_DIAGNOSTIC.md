# P12 v2 side-effect capture: both diagnostic transcripts admitted, science on hold

The two fixed diagnostic seeds ran once in a new isolated source directory using the read-only P10 patched runtime. The v2 checker admitted both captured transcripts and verified eight preserved working-directory files **per seed**. This validates the new capture path on the real simulator, not scientific seed qualification. The 13 missing-agent messages per seed remain; no policy matrix ran.

## Identity and boundaries

| Item | Observed identity |
| --- | --- |
| Exclusive target | `/home/ubuntu/wps-water-allocation-side-effects-v2` (absent before creation) |
| Source archive | Branch commit `c11bda49ce09653d17dc7579e520f4e78435ad21`, `git -c core.autocrlf=false archive`, SHA-256 `4e3bba56a42336b5f19442399d9c56857766a944ca784c2b727b7dac4667d582` |
| Frozen source | All seven P4 hashes match; only the new target's `world.24.json` was restored to the predeclared CRLF SHA-256 `244645e4092e3ecbf9a2830053a6c08eb67a1e3d288650f0f6640348b62ade7c` |
| Diagnostic CSV | New target `inputs/diagnostic_requests.csv`, SHA-256 `7349e3ca1c2b591de3cc3afdd751a7c73d08450dddce8296b3d10b546b9eeff4` |
| Runtime manifest | New target copy of P10's `build_manifest.json`, SHA-256 `a668c589fcac25489c9881cc81fdc853d6ff8cf9d689392be05e01ff5357bc54`; its absolute classpath intentionally points to the unchanged P10 build |
| Patched class | P10 `ChannelBESA.class` SHA-256 `60313647dbadf052c0c956884dc0bc667f3e15edd37d0d04314ff6affc0e2f2a` |
| Java and classpath | P10 Java SHA-256 `431f7f67aa26a7f22596f7b1b5eddcaebc024d14f4ed07d39afe669d49048a55`; all 15 ordered components matched the copied manifest before execution and in post-run readback |
| Exclusive outputs | `/home/ubuntu/wps-water-allocation-side-effects-v2/p12-diagnostics-271828-314159` |

The source working directory began without `logs/` or `rice_water_stress_.csv`. Transfer SHA-256 values matched the local archive, locked CSV and CRLF world file before extraction. The archive identified the expected commit. An initial read-only class-path probe exited 1 because it guessed the wrong package directory; the subsequent read-only lookup found `build/besa/BESA/Kernel/Agent/ChannelBESA.class` with the expected hash before plan or execution. The P10 build, P8/P10 captures, ancillary checkout, and `main` were not written.

## Commands and observed checks

P5 plan used the exact execution arguments below with `--plan` instead of `--execute`. It exited 0 with `status=plan_only`, `writes=0`, `processes=0`, seeds `271828` then `314159`; the exclusive output root and working-directory side-effect files remained absent. The launcher verified the seven frozen hashes, the CSV, the actual Java binary and all 15 ordered runtime components against the manifest.

```bash
set -euo pipefail
T=/home/ubuntu/wps-water-allocation-side-effects-v2
P=/home/ubuntu/wps-water-allocation-besa-teardown-v1
test ! -e "$T/p12-diagnostics-271828-314159"
test ! -e "$T/source/logs"
test ! -e "$T/source/rice_water_stress_.csv"
mapfile -t cp < "$P/build/runtime-classpath.txt"
test "${#cp[@]}" -eq 15
args=()
for p in "${cp[@]}"; do args+=(--classpath "$p"); done
python3 -B "$T/source/experiments/water_allocation/run_ideam_seed_diagnostics.py" \
  --execute --root "$T/source/experiments/water_allocation" \
  --output-root "$T/p12-diagnostics-271828-314159" \
  --java /usr/lib/jvm/java-21-openjdk-amd64/bin/java "${args[@]}" \
  --build-manifest "$T/inputs/build_manifest.json" \
  --diagnostic-source "$T/inputs/diagnostic_requests.csv" --java-option=-Xmx6g
```

The one execute invocation exited 0 after 335 seconds with `status=captured_transcripts_only`. Both runs report `termination=natural` and `java_exit=0`. The current P4/P9 checker was invoked separately:

```bash
T=/home/ubuntu/wps-water-allocation-side-effects-v2
python3 -B "$T/source/experiments/water_allocation/check_ideam_seed_qualification.py" \
  --root "$T/source/experiments/water_allocation" \
  "$T/p12-diagnostics-271828-314159/seed-271828" \
  "$T/p12-diagnostics-271828-314159/seed-314159"
```

Checker exit 0: `diagnostic_transcripts_admitted`, `real_seed_qualification=transcript_only`, `runtime_identity_scope=captured_only`, `side_effect_scope=captured_per_seed` for each seed. Each had 12 assigned UPA, 24 eligible plots, 96 ha and 48 registered plots. Independent post-run readback confirmed all seven frozen hashes, Java plus 15 ordered classpath component hashes, 467 archive regular files unchanged except the declared CRLF world reconstruction, and each preserved side-effect byte hash.

| Seed | `capture.json` SHA-256 | Changed files | Missing-agent messages | Uncaught-thread banners | P10 audit CSV comparison |
| --- | --- | ---: | ---: | ---: | --- |
| 271828 | `0d272a1bb87399d23926ba0f32a55e67c28a1317c7cfec8e20e103ae375a31d6` | 8 | 13 | 0 | water, yield and climate byte-identical |
| 314159 | `dc81bc0e7f6aeb522015e03ffa300bd7159121bfd8919f4f59a69dbc73b6c6d6` | 8 | 13 | 0 | water, yield and climate byte-identical |

The v2 archives contain the eight files below for each seed; every copied byte matched its captured SHA-256. For all eight, seed 314159's recorded *before* hash exactly equals seed 271828's recorded *after* hash.

| Working-directory file | Seed 271828 after SHA-256 | Seed 314159 after SHA-256 |
| --- | --- | --- |
| `logs/Bank.csv` | `824b12994d1cfe0c116fc73106f630510c4c6b69ef51c0fcb2a2fc16335be6c8` | `c1178fd1173275dc23386648518d6b13775bb48907ab45b5c748fbbd3a478de4` |
| `logs/Loans.csv` | `fd4f07aa5791267d494fb75a73cd0791cd8d35104aa91af4995459075c9fa07b` | `fc72cedb2fd4e1439a9ad62dd76ca7f46f8efde416f55eeb281e917904f4e35e` |
| `logs/Market.csv` | `a403ee8156c40dfb1dd1793931279b736e0621506aa10872632243d8b0c4ee66` | `67a86752bbb111d4403646943474f53b73a16c9e82617451bb79614080400c51` |
| `logs/NaturalPhenomena.csv` | `dde349ff5f227d2d420dfb09b5926b6ef65e9a667e59d48a6b010081b3dca596` | `4c39c50af9b8bed768b65c660037a02282d3dc297c54933f45e8d6ffe2e26536` |
| `logs/PerturbationGeneratorGuard.csv` | `824b12994d1cfe0c116fc73106f630510c4c6b69ef51c0fcb2a2fc16335be6c8` | `c1178fd1173275dc23386648518d6b13775bb48907ab45b5c748fbbd3a478de4` |
| `logs/wpsSimulator.csv` | `3445065b7973bdf9267658d07791118aabeb4ca94b67c8de55d58df46404c48a` | `ad6ba714a0ef987ef38acaec61a824c1f7959732cf35dcba9cf26f9c09790674` |
| `logs/wpsSimulator.log` | `5690fae67ce01aeeb5a620b2429bccc8c981663f464c1f9fe2e25deafbb23fc2` | `a93ccac25eae75d72cb4dcd4bad127956abdda2a865162ff2567937c020a18bf` |
| `rice_water_stress_.csv` | `2f71183db8c83ba88af393dc55609278fb10b87e91da941af9b422180861ee86` | `634335885799bb4d6c35c419dda23cccfd024a0095dfc0ad5794a97f4d2d9e34` |

## Limits and next gate

The per-seed archives recover the first seed's overwritten log bytes and distinguish both seeds' working-directory states. The second snapshot of append-only files still includes first-seed content; the capture does not parse or causally attribute individual log records. The six P12 audit CSVs are byte-identical to P10, but this does not independently attest the process or establish reproducibility under all schedules. The 13 missing-agent messages and one Java `FINE` `Runtime.exit(0)` trace per seed remain unresolved. Station provenance, reporting patterns, date labels and spatial support also remain open. Keep seed/scientific qualification and the 192-run policy matrix **ON HOLD**.

Rollback boundary: this report and P12 tracker entry locally; the exclusive P12 server target remotely, retained for audit. No deletion, PR, merge or `main` change is part of P12.
