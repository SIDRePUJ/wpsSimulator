"""Local, isolated regression checks for the exact-source BESA teardown patch."""

from __future__ import annotations

import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
from unittest.mock import patch as mock_patch

import apply_kernelbesa_teardown_patch as applicator
from apply_kernelbesa_teardown_patch import (
    EXPECTED_SHA256,
    RELATIVE_SOURCE,
    apply_exact_preimage,
)


REPO_ROOT = Path(__file__).resolve().parents[3]
KERNEL_ROOT = Path(os.environ.get("KERNELBESA_ROOT", REPO_ROOT.parent / "KernelBESA"))
LIB_DIR = Path(os.environ.get("BESA_LIB_DIR", REPO_ROOT.parent.parent / "lib"))
CONTRACT = Path(__file__).with_name("ChannelBESATeardownContract.java")


class ChannelBESAPatchTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.source = KERNEL_ROOT / RELATIVE_SOURCE
        if not cls.source.is_file():
            raise FileNotFoundError(f"Ancillary source missing: {cls.source}")
        cls.jars = sorted(LIB_DIR.glob("*.jar"))
        if len(cls.jars) != 12:
            raise AssertionError(f"Expected 12 local BESA dependency JARs, got {len(cls.jars)}")

    def test_exact_preimage_and_exclusive_output(self) -> None:
        with tempfile.TemporaryDirectory(prefix="besa-patch-test-") as temporary:
            output = Path(temporary) / "ChannelBESA.java"
            digest = apply_exact_preimage(self.source, output)
            self.assertEqual(digest, "9da3fa66340e0e816c1572aec634798fa488ed9b7fcd2a2d6f2e79817285e06e")
            with self.assertRaises(FileExistsError):
                apply_exact_preimage(self.source, output)
            changed = Path(temporary) / "changed.java"
            changed.write_bytes(self.source.read_bytes() + b"\n")
            with self.assertRaisesRegex(ValueError, "preimage SHA-256 mismatch"):
                apply_exact_preimage(changed, Path(temporary) / "rejected.java")
            self.assertFalse((Path(temporary) / "rejected.java").exists())
            altered_patch = Path(temporary) / "altered.patch"
            altered_patch.write_bytes((
                applicator.PATCH.read_text(encoding="utf-8").replace(
                    "+    final protected synchronized PortBESA findPort(GuardBESA guard)",
                    "+    final protected synchronized PortBESA findPort(GuardBESA guard) /* altered */",
                    1,
                )
            ).encode("utf-8"))
            with mock_patch.object(applicator, "PATCH", altered_patch):
                with self.assertRaisesRegex(RuntimeError, "Patched ChannelBESA SHA-256 mismatch"):
                    apply_exact_preimage(self.source, Path(temporary) / "wrong-postimage.java")
            self.assertFalse((Path(temporary) / "wrong-postimage.java").exists())
            self.assertEqual(
                __import__("hashlib").sha256(self.source.read_bytes()).hexdigest(),
                EXPECTED_SHA256,
            )

    def test_java_baseline_fails_and_patch_passes(self) -> None:
        with tempfile.TemporaryDirectory(prefix="besa-contract-test-") as temporary:
            temp = Path(temporary)
            baseline = self._compile_variant(temp / "baseline", patched=False)
            before = self._run_contract(baseline, full=False)
            self.assertNotEqual(before.returncode, 0, before.stdout + before.stderr)
            self.assertIn("Purge left 4 ports", before.stderr)

            corrected = self._compile_variant(temp / "corrected", patched=True)
            after = self._run_contract(corrected, full=True)
            self.assertEqual(after.returncode, 0, after.stdout + after.stderr)
            self.assertIn("ChannelBESA teardown contract passed", after.stdout)

    def _compile_variant(self, root: Path, *, patched: bool) -> Path:
        source_root = root / "src/main/java"
        shutil.copytree(KERNEL_ROOT / "src/main/java", source_root)
        if patched:
            output = root / "patched-ChannelBESA.java"
            apply_exact_preimage(self.source, output)
            shutil.copyfile(output, root / RELATIVE_SOURCE)
        classes = root / "classes"
        classes.mkdir()
        sources = sorted(source_root.rglob("*.java"))
        command = [
            "javac", "-nowarn", "-encoding", "UTF-8", "-proc:none",
            "-d", str(classes), "-cp", os.pathsep.join(map(str, self.jars)),
            *(str(path) for path in sources), str(CONTRACT),
        ]
        result = subprocess.run(command, capture_output=True, text=True, timeout=120)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        return classes

    def _run_contract(self, classes: Path, *, full: bool) -> subprocess.CompletedProcess[str]:
        command = [
            "java", "-cp", os.pathsep.join([str(classes), *(str(jar) for jar in self.jars)]),
            "BESA.Kernel.Agent.ChannelBESATeardownContract",
        ]
        if full:
            command.append("--full")
        return subprocess.run(command, capture_output=True, text=True, timeout=60)


if __name__ == "__main__":
    unittest.main()
