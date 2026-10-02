"""Plan or explicitly capture two non-overwriting district seed diagnostics.

No policy-rule matrix is run here. Execution is opt-in and requires a supplied
runtime manifest; tests use a fake process, never the actual Java simulator.
"""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys

from check_ideam_seed_qualification import (
    DIAGNOSTIC_REQUEST_SHA256, FROZEN, OUTPUTS, ROOT, SEEDS, check_argv, require, sha,
)


SCHEMA = "seed-diagnostic-build/v1"
CAPTURE_SCHEMA = "district-seed-diagnostic/v2"
DIAGNOSTIC_SOURCE = ROOT / "reports/raw/calendar-c2-local-20260928-web-osredirect/diagnostic_requests.csv"


def component(path):
    """Hash one regular file or a sorted, symlink-free directory tree."""
    path = Path(path)
    require(not any(part.is_symlink() for part in (path, *path.parents)),
            f"symlink runtime component: {path}")
    require(path.exists(), f"missing runtime component: {path}")
    if path.is_file():
        kind, digest = "file", sha(path)
    else:
        require(path.is_dir(), f"unsupported runtime component: {path}")
        members = []
        for member in sorted(path.rglob("*"), key=lambda item: item.relative_to(path).as_posix()):
            require(not member.is_symlink(), f"symlink runtime tree member: {member}")
            require(member.is_file() or member.is_dir(), f"unsupported tree member: {member}")
            if member.is_file():
                members.append([member.relative_to(path).as_posix(), sha(member)])
        require(members, f"empty runtime tree: {path}")
        encoded = json.dumps(members, separators=(",", ":"), ensure_ascii=False).encode("utf-8")
        kind, digest = "tree", hashlib.sha256(encoded).hexdigest()
    return {"path": str(path.resolve()), "kind": kind, "sha256": digest}


def runtime_identity(java, classpath):
    require(classpath, "ordered classpath is empty")
    java_identity = component(java)
    require(java_identity["kind"] == "file", "Java executable must be a file")
    components = [component(path) for path in classpath]
    paths = [entry["path"] for entry in components]
    require(len(paths) == len(set(paths)), "repeated classpath component")
    return {"schema": SCHEMA, "java": java_identity, "classpath": components}


def preflight(root, output_root, java, classpath, build_manifest, diagnostic_source):
    require(not output_root.exists() and not output_root.is_symlink(),
            "output root already exists")
    require(output_root.parent.is_dir(), "output parent directory does not exist")
    require(not build_manifest.is_symlink(), "symlink build manifest")
    require(not diagnostic_source.is_symlink() and sha(diagnostic_source) ==
            DIAGNOSTIC_REQUEST_SHA256, "diagnostic source hash mismatch")
    for name, expected in FROZEN.items():
        require(sha(root / name) == expected, f"frozen hash mismatch: {name}")
    identity = runtime_identity(java, classpath)
    manifest = json.loads(build_manifest.read_text(encoding="utf-8"))
    require(manifest == identity, "build manifest differs from runtime bytes/order")
    return identity, sha(build_manifest)


def command(java, classpath, java_options, root, directory, seed):
    return [str(java), *java_options,
            "-Dwps.water.districtRiceCalendar=true",
            "-Dwps.water.riceOnlyCohort=true",
            "-Dwps.water.discoverPlots=true",
            f"-Dwps.water.requests={directory / 'diagnostic_requests.csv'}",
            "-Dwps.water.sourceM3=19200", "-Dwps.water.rule=PROPORTIONAL_DEMAND",
            f"-Dwps.water.farmAssignments={root / 'twelve_upa_manifest.csv'}",
            f"-Dwps.water.auditCsv={directory / 'water_audit.csv'}",
            f"-Dwps.water.yieldCsv={directory / 'yield_audit.csv'}",
            "-Dwps.water.potentialYieldTpha=5", "-Dwps.water.ky=1",
            f"-Dwps.water.climateCsv={directory / 'climate_audit.csv'}",
            "-cp", os.pathsep.join(str(path) for path in classpath),
            "org.wpsim.WellProdSim.wpsStart", "-env", "local", "-mode", "web",
            "-agents", "12", "-world", "24", "-land", "2", "-years", "1",
            "-startyear", "2022", "-seed", str(seed), "-perturbation", "none"]


def working_tree_inventory(cwd, output_root):
    """Hash regular cwd files, excluding only the exclusive capture output tree."""
    require(cwd != output_root and output_root not in cwd.parents,
            "output root contains simulator working directory")
    inventory = {}
    for parent, dirs, files in os.walk(cwd, followlinks=False):
        parent = Path(parent)
        dirs[:] = sorted(directory for directory in dirs
                         if parent / directory != output_root)
        for name in [*dirs, *files]:
            require(not (parent / name).is_symlink(),
                    f"symlink in simulator working directory: {parent / name}")
        for name in sorted(files):
            path = parent / name
            require(path.is_file(), f"non-file in simulator working directory: {path}")
            inventory[path.relative_to(cwd).as_posix()] = sha(path)
    return inventory


def preserve_side_effects(cwd, directory, before, after):
    require(before.keys() <= after.keys(), "simulator deleted a working-directory file")
    changed = {name: {"before_sha256": before.get(name), "after_sha256": digest}
               for name, digest in sorted(after.items()) if before.get(name) != digest}
    archive = directory / "side_effects"
    archive.mkdir(exist_ok=False)
    for name, entry in changed.items():
        source, target = cwd / name, archive / name
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, target)
        require(sha(target) == entry["after_sha256"],
                f"side-effect file changed during preservation: {name}")
    return {"working_directory": str(cwd), "changed": changed}


def run(root, output_root, java, classpath, build_manifest, *, execute=False,
        java_options=(), timeout_seconds=300, synthetic=False,
        diagnostic_source=DIAGNOSTIC_SOURCE):
    root = Path(root).resolve()
    output_root = Path(output_root).absolute()
    java = Path(java).absolute()
    classpath = [Path(path).absolute() for path in classpath]
    build_manifest = Path(build_manifest).absolute()
    diagnostic_source = Path(diagnostic_source).absolute()
    cwd = root.parents[1]
    require(timeout_seconds > 0, "timeout must be positive")
    if not synthetic:
        require(all(re.fullmatch(r"-Xm(?:x|s)\d+[mMgG]", value) for value in java_options),
                "real Java options are limited to heap sizing")
    identity, manifest_hash = preflight(root, output_root, java, classpath,
                                        build_manifest, diagnostic_source)
    plan = [{"seed": seed, "directory": str(output_root / f"seed-{seed}"),
             "argv": command(java, classpath, java_options, root,
                             output_root / f"seed-{seed}", seed),
             "capture": str(output_root / f"seed-{seed}/capture.json"),
             "capture_files": ["diagnostic_requests.csv", "build_manifest.json",
                               *OUTPUTS, "side_effects/", "capture.json"]}
            for seed in SEEDS]
    for item in plan:
        check_argv(item["argv"], Path(item["directory"]), root, item["seed"])
    if not execute:
        return {"status": "plan_only", "writes": 0, "processes": 0,
                "runtime_identity": identity, "runs": plan}

    output_root.mkdir(parents=False, exist_ok=False)
    completed = []
    for item in plan:
        # Recheck before each process; a changed input never authorizes the next seed.
        current, current_manifest_hash = preflight_inputs(root, java, classpath,
                                                          build_manifest, diagnostic_source)
        require(current == identity and current_manifest_hash == manifest_hash,
                "input/runtime drift before process")
        directory = Path(item["directory"])
        directory.mkdir(exist_ok=False)
        shutil.copyfile(diagnostic_source, directory / "diagnostic_requests.csv")
        shutil.copyfile(build_manifest, directory / "build_manifest.json")
        require(sha(directory / "diagnostic_requests.csv") == DIAGNOSTIC_REQUEST_SHA256
                and sha(directory / "build_manifest.json") == manifest_hash,
                "copied input drift")
        argv = item["argv"]
        before = working_tree_inventory(cwd, output_root)
        (directory / "command.txt").write_text(" ".join(argv) + "\n", encoding="utf-8")
        try:
            with (directory / "stdout.txt").open("wb") as stdout, \
                    (directory / "stderr.txt").open("wb") as stderr:
                process = subprocess.run(argv, cwd=cwd, stdout=stdout,
                                         stderr=stderr, timeout=timeout_seconds, check=False)
        except subprocess.TimeoutExpired as error:
            (directory / "exit.txt").write_text("TIMEOUT\n", encoding="utf-8")
            raise ValueError(f"seed {item['seed']} timed out; later seed not started") from error
        (directory / "exit.txt").write_text(f"JAVA_EXIT={process.returncode}\n", encoding="utf-8")
        require(process.returncode == 0, f"seed {item['seed']} exited nonzero; later seed not started")
        current, current_manifest_hash = preflight_inputs(root, java, classpath,
                                                          build_manifest, diagnostic_source)
        require(current == identity and current_manifest_hash == manifest_hash,
                "input/runtime drift after process; later seed not started")
        require(sha(directory / "diagnostic_requests.csv") == DIAGNOSTIC_REQUEST_SHA256
                and sha(directory / "build_manifest.json") == manifest_hash,
                "copied input drift after process")
        after = working_tree_inventory(cwd, output_root)
        side_effects = preserve_side_effects(cwd, directory, before, after)
        capture = {"schema": CAPTURE_SCHEMA,
                   "kind": "synthetic_fixture" if synthetic else "real_seed_diagnostic",
                   "seed": item["seed"], "termination": "natural", "java_exit": 0,
                   "argv": argv, "frozen_sha256": FROZEN,
                   "runtime_identity": identity, "build_manifest_sha256": manifest_hash,
                   "diagnostic_requests_sha256": sha(directory / "diagnostic_requests.csv"),
                   "side_effects": side_effects,
                   "output_sha256": {name: sha(directory / name) for name in OUTPUTS}}
        (directory / "capture.json").write_text(json.dumps(capture, indent=2) + "\n",
                                                    encoding="utf-8")
        completed.append({"seed": item["seed"], "directory": str(directory)})
    return {"status": "captured_transcripts_only", "real_seed_qualification":
            "not_run" if synthetic else "transcript_only", "runs": completed}


def preflight_inputs(root, java, classpath, build_manifest, diagnostic_source):
    require(not build_manifest.is_symlink() and not diagnostic_source.is_symlink(),
            "symlink input")
    require(sha(diagnostic_source) == DIAGNOSTIC_REQUEST_SHA256,
            "diagnostic source hash mismatch")
    for name, expected in FROZEN.items():
        require(sha(root / name) == expected, f"frozen hash mismatch: {name}")
    identity = runtime_identity(java, classpath)
    require(json.loads(build_manifest.read_text(encoding="utf-8")) == identity,
            "build manifest differs from runtime bytes/order")
    return identity, sha(build_manifest)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument("--plan", action="store_true")
    mode.add_argument("--execute", action="store_true")
    parser.add_argument("--root", type=Path, default=ROOT)
    parser.add_argument("--output-root", type=Path, required=True)
    parser.add_argument("--java", type=Path, required=True)
    parser.add_argument("--classpath", type=Path, action="append", required=True,
                        help="Repeat in Java classpath order; files and directories are supported")
    parser.add_argument("--build-manifest", type=Path, required=True)
    parser.add_argument("--diagnostic-source", type=Path, required=True,
                        help="Locked C2-style diagnostic request CSV supplied at an explicit path")
    parser.add_argument("--java-option", action="append", default=[])
    parser.add_argument("--timeout-seconds", type=float, default=300)
    parser.add_argument("--synthetic-fixture", action="store_true",
                        help="Mark fake-process captures as fixtures, never real qualification")
    args = parser.parse_args()
    try:
        result = run(args.root, args.output_root, args.java, args.classpath,
                     args.build_manifest, execute=args.execute,
                     java_options=args.java_option, timeout_seconds=args.timeout_seconds,
                     synthetic=args.synthetic_fixture,
                     diagnostic_source=args.diagnostic_source)
    except (OSError, ValueError, json.JSONDecodeError) as error:
        print(f"diagnostic capture stopped: {error}", file=sys.stderr)
        return 1
    print(json.dumps(result, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
