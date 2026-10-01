"""No Java process is launched: execution tests use Python as a fake child."""

import json
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

import check_ideam_seed_qualification as gate
import run_ideam_seed_diagnostics as launcher


SOURCE = Path(__file__).resolve().parent


class DiagnosticLauncherTests(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name) / "repo/experiments/water_allocation"
        self.root.mkdir(parents=True)
        for name in gate.FROZEN:
            target = self.root / name
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(SOURCE / name, target)
        self.diagnostic_source = self.root / "diagnostic_requests.csv"
        shutil.copyfile(launcher.DIAGNOSTIC_SOURCE, self.diagnostic_source)
        self.java = Path(sys.executable).resolve()
        self.jar = self.root / "fake.jar"
        self.jar.write_bytes(b"fake jar")
        self.classes = self.root / "fake-classes"
        self.classes.mkdir()
        (self.classes / "A.class").write_bytes(b"first")
        nested = self.classes / "nested"
        nested.mkdir()
        (nested / "B.class").write_bytes(b"second")
        self.classpath = [self.classes, self.jar]
        self.manifest = self.root / "build_manifest.json"
        self.manifest.write_text(json.dumps(launcher.runtime_identity(self.java, self.classpath)),
                                 encoding="utf-8")
        self.output = self.root / "future-diagnostics"

    def fake_script(self, *, mode="success"):
        # The child receives Java-shaped options as ordinary Python script arguments.
        drift_path = self.root / "data/derived/world24_district_crop_windows.csv"
        lines = [
            "import sys,pathlib,time",
            "args=sys.argv",
            "seed=args[args.index('-seed')+1]",
            "props=dict(x[2:].split('=',1) for x in args if x.startswith('-Dwps.water.'))",
            "[pathlib.Path(props['wps.water.'+key]).write_text('fake audit\\n',encoding='utf-8') "
            "for key in ('auditCsv','yieldCsv','climateCsv')]",
            "print('SEED: '+seed)",
            "print('fake stderr',file=sys.stderr)",
        ]
        if mode == "nonzero":
            lines.append("sys.exit(7)")
        elif mode == "timeout":
            lines.append("time.sleep(1)")
        elif mode == "drift":
            lines.append(f"pathlib.Path({str(drift_path)!r}).write_bytes(b'drift')")
        return ";".join(lines)

    def invoke(self, *, execute=False, mode="success", timeout=3):
        return launcher.run(self.root, self.output, self.java, self.classpath,
                            self.manifest, execute=execute,
                            java_options=("-c", self.fake_script(mode=mode)),
                            timeout_seconds=timeout, synthetic=True,
                            diagnostic_source=self.diagnostic_source)

    def cli(self, *, execute=False):
        argv = [sys.executable, str(SOURCE / "run_ideam_seed_diagnostics.py"),
                "--execute" if execute else "--plan", "--root", str(self.root),
                "--output-root", str(self.output), "--java", str(self.java),
                "--classpath", str(self.classes), "--classpath", str(self.jar),
                "--build-manifest", str(self.manifest),
                "--diagnostic-source", str(self.diagnostic_source), "--java-option=-c",
                f"--java-option={self.fake_script()}", "--synthetic-fixture"]
        return subprocess.run(argv, capture_output=True, text=True)

    def test_plan_cli_writes_nothing_and_records_exact_order(self):
        self.assertNotEqual(self.diagnostic_source.resolve(), launcher.DIAGNOSTIC_SOURCE.resolve())
        before = {str(path) for path in self.root.rglob("*")}
        result = self.cli()
        self.assertEqual(result.returncode, 0, result.stderr)
        plan = json.loads(result.stdout)
        self.assertEqual(plan["status"], "plan_only")
        self.assertEqual((plan["writes"], plan["processes"]), (0, 0))
        self.assertEqual([run["seed"] for run in plan["runs"]], list(gate.SEEDS))
        self.assertEqual(plan["runs"][0]["capture_files"],
                         ["diagnostic_requests.csv", "build_manifest.json",
                          *gate.OUTPUTS, "capture.json"])
        self.assertEqual([entry["path"] for entry in plan["runtime_identity"]["classpath"]],
                         [str(path) for path in self.classpath])
        self.assertEqual({str(path) for path in self.root.rglob("*")}, before)
        self.assertFalse(self.output.exists())

    def test_plan_cli_rejects_changed_supplied_diagnostic_source_without_writes(self):
        self.diagnostic_source.write_bytes(b"changed request")
        before = {str(path) for path in self.root.rglob("*")}
        result = self.cli()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("diagnostic source hash mismatch", result.stderr)
        self.assertEqual({str(path) for path in self.root.rglob("*")}, before)
        self.assertFalse(self.output.exists())

    def test_fake_process_cli_captures_closed_files_and_actual_argv(self):
        result = self.cli(execute=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(json.loads(result.stdout)["real_seed_qualification"], "not_run")
        for seed in gate.SEEDS:
            directory = self.output / f"seed-{seed}"
            capture = json.loads((directory / "capture.json").read_text(encoding="utf-8"))
            self.assertEqual(capture["kind"], "synthetic_fixture")
            self.assertEqual(capture["seed"], seed)
            self.assertEqual(capture["java_exit"], 0)
            self.assertEqual(capture["termination"], "natural")
            self.assertEqual(capture["argv"][0], str(self.java))
            self.assertEqual(capture["output_sha256"],
                             {name: gate.sha(directory / name) for name in gate.OUTPUTS})
            self.assertEqual(capture["build_manifest_sha256"],
                             gate.sha(directory / "build_manifest.json"))
            self.assertIn(f"SEED: {seed}", (directory / "stdout.txt").read_text())
            self.assertIn("fake stderr", (directory / "stderr.txt").read_text())

    def test_existing_output_root_is_never_overwritten(self):
        self.output.mkdir()
        sentinel = self.output / "sentinel.txt"
        sentinel.write_text("keep", encoding="utf-8")
        with self.assertRaisesRegex(ValueError, "output root already exists"):
            self.invoke(execute=True)
        self.assertEqual(sentinel.read_text(encoding="utf-8"), "keep")

    def test_nonzero_child_stops_before_second_seed(self):
        with self.assertRaisesRegex(ValueError, "exited nonzero"):
            self.invoke(execute=True, mode="nonzero")
        self.assertEqual((self.output / "seed-271828/exit.txt").read_text().strip(), "JAVA_EXIT=7")
        self.assertFalse((self.output / "seed-271828/capture.json").exists())
        self.assertFalse((self.output / "seed-314159").exists())

    def test_timeout_stops_before_second_seed(self):
        with self.assertRaisesRegex(ValueError, "timed out"):
            self.invoke(execute=True, mode="timeout", timeout=0.05)
        self.assertEqual((self.output / "seed-271828/exit.txt").read_text().strip(), "TIMEOUT")
        self.assertFalse((self.output / "seed-314159").exists())

    def test_frozen_input_drift_stops_before_second_seed(self):
        with self.assertRaisesRegex(ValueError, "frozen hash mismatch"):
            self.invoke(execute=True, mode="drift")
        self.assertFalse((self.output / "seed-271828/capture.json").exists())
        self.assertFalse((self.output / "seed-314159").exists())

    def test_tree_bytes_and_classpath_order_are_manifest_bound(self):
        (self.classes / "nested/B.class").write_bytes(b"changed")
        with self.assertRaisesRegex(ValueError, "build manifest differs"):
            self.invoke()
        (self.classes / "nested/B.class").write_bytes(b"second")
        self.classpath.reverse()
        with self.assertRaisesRegex(ValueError, "build manifest differs"):
            self.invoke()

    def test_runtime_symlink_is_rejected_when_platform_supports_it(self):
        link = self.root / "linked.jar"
        try:
            link.symlink_to(self.jar)
        except OSError:
            self.skipTest("filesystem does not permit symlink creation")
        with self.assertRaisesRegex(ValueError, "symlink runtime component"):
            launcher.component(link)

    def test_runtime_symlink_parent_is_rejected_when_platform_supports_it(self):
        link = self.root / "linked-classes"
        try:
            link.symlink_to(self.classes, target_is_directory=True)
        except OSError:
            self.skipTest("filesystem does not permit directory symlink creation")
        with self.assertRaisesRegex(ValueError, "symlink runtime component"):
            launcher.component(link / "nested/B.class")

    def test_real_capture_rejects_fake_interpreter_options(self):
        with self.assertRaisesRegex(ValueError, "real Java options"):
            launcher.run(self.root, self.output, self.java, self.classpath,
                         self.manifest, execute=True,
                         java_options=("-c", self.fake_script()),
                         diagnostic_source=self.diagnostic_source)
        self.assertFalse(self.output.exists())


if __name__ == "__main__":
    unittest.main()
