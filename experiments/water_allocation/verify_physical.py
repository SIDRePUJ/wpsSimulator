"""Run only the physical research-kernel checks; not the full simulator."""

import subprocess
import sys
import tempfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
PACKAGE = Path("org/wpsim/research/water")
MAIN = ROOT / "src/main/java" / PACKAGE
TEST = ROOT / "src/test/java" / PACKAGE


def run(command):
    subprocess.run(command, check=True)


def main():
    sources = sorted(MAIN.glob("*.java")) + sorted(TEST.glob("*.java"))
    if not sources:
        raise RuntimeError("physical research sources not found")
    with tempfile.TemporaryDirectory(prefix="wps-water-check-") as output:
        run(["javac", "-d", output, *(str(path) for path in sources)])
        for test in ("SharedWaterSourceTest", "RiceYieldResponseTest", "PhysicalIrrigationPlanTest",
                     "FarmAssignmentPlanTest", "PhysicalYieldLedgerTest", "ResearchClimateRandomTest",
                     "PhysicalClimateLedgerTest", "DailyRainfallSeriesTest"):
            run(["java", "-ea", "-cp", output, f"org.wpsim.research.water.{test}"])
    run([sys.executable, "-m", "unittest", "discover", "-s",
         str(Path(__file__).resolve().parent), "-p", "test_analyze_physical_results.py", "-v"])
    run([sys.executable, "-m", "unittest", "discover", "-s",
         str(Path(__file__).resolve().parent), "-p", "test_analyze_upa_results.py", "-v"])
    run([sys.executable, "-m", "unittest", "discover", "-s",
         str(Path(__file__).resolve().parent), "-p", "test_build_joined_results.py", "-v"])
    run([sys.executable, "-m", "unittest", "discover", "-s",
         str(Path(__file__).resolve().parent), "-p", "test_derive_rice_area.py", "-v"])
    run([sys.executable, "-m", "unittest", "discover", "-s",
         str(Path(__file__).resolve().parent), "-p", "test_prepare_power_rainfall.py", "-v"])
    print("Physical research checks PASS; full WellProdSim integration not checked")


if __name__ == "__main__":
    main()
