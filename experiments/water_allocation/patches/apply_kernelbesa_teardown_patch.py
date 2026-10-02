"""Apply the reviewable ChannelBESA patch only to an exclusive output file.

The source is SHA-gated; neither the ancillary repository nor an existing
server build is modified by this command. A later build can consume the output
inside a separately copied, exclusive source tree.
"""

from __future__ import annotations

import argparse
import hashlib
from pathlib import Path
import subprocess
import tempfile


EXPECTED_SHA256 = "97b443024441504d2438058534ea84325e61f9d3910b601d5d5391f72caf7b8a"
EXPECTED_PATCHED_SHA256 = "9da3fa66340e0e816c1572aec634798fa488ed9b7fcd2a2d6f2e79817285e06e"
RELATIVE_SOURCE = Path("src/main/java/BESA/Kernel/Agent/ChannelBESA.java")
PATCH = Path(__file__).with_name("kernelbesa-channel-teardown-v1.patch")


def apply_exact_preimage(source_file: Path, output_file: Path) -> str:
    source_bytes = source_file.read_bytes()
    digest = hashlib.sha256(source_bytes).hexdigest()
    if digest != EXPECTED_SHA256:
        raise ValueError(f"ChannelBESA preimage SHA-256 mismatch: {digest}")
    if output_file.exists():
        raise FileExistsError(f"Refusing to overwrite {output_file}")
    if not output_file.parent.is_dir():
        raise FileNotFoundError(f"Output parent does not exist: {output_file.parent}")

    with tempfile.TemporaryDirectory(prefix="kernelbesa-teardown-") as temporary:
        root = Path(temporary)
        staged_source = root / RELATIVE_SOURCE
        staged_source.parent.mkdir(parents=True)
        staged_source.write_bytes(source_bytes)
        staged_patch = root / "channel.patch"
        staged_patch.write_bytes(PATCH.read_bytes().replace(b"\r\n", b"\n"))
        for flags in (("--check",), ()):
            result = subprocess.run(
                ["git", "-c", "core.autocrlf=false", "apply", *flags, str(staged_patch)],
                cwd=root,
                capture_output=True,
                text=True,
                check=False,
            )
            if result.returncode:
                raise RuntimeError(f"Patch application failed: {result.stderr.strip()}")
        patched_bytes = staged_source.read_bytes()
        if patched_bytes == source_bytes:
            raise RuntimeError("Patch did not change ChannelBESA")
        patched_digest = hashlib.sha256(patched_bytes).hexdigest()
        if patched_digest != EXPECTED_PATCHED_SHA256:
            raise RuntimeError(f"Patched ChannelBESA SHA-256 mismatch: {patched_digest}")
        with output_file.open("xb") as destination:
            destination.write(patched_bytes)
    return patched_digest


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source-file", required=True, type=Path)
    parser.add_argument("--output-file", required=True, type=Path)
    args = parser.parse_args()
    try:
        digest = apply_exact_preimage(args.source_file, args.output_file)
    except (OSError, RuntimeError, ValueError) as error:
        parser.exit(1, f"{error}\n")
    print(f"Patched ChannelBESA SHA-256: {digest}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
