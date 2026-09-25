#!/usr/bin/env python3
"""Run the hidden conformance tests on every generated implementation.

Reads results/runs.jsonl and writes results/evaluation.csv with one row per run:
model, condition, mechanism, replication, status, tests_total, tests_passed, all_pass,
failed_tests. `status` is one of: ok, import_error (syntax or missing names), timeout.
"""
import csv, json, os, shutil, subprocess, sys, tempfile
import xml.etree.ElementTree as ET

HERE = os.path.dirname(os.path.abspath(__file__))
CFG = json.load(open(os.path.join(HERE, "config.json")))


def evaluate(mech, code):
    module = CFG["mechanisms"][mech]
    test = os.path.join(HERE, "tests_hidden", f"test_{mech.lower()}.py")
    with tempfile.TemporaryDirectory() as d:
        open(os.path.join(d, f"{module}.py"), "w", encoding="utf-8").write(code)
        shutil.copy(test, d)
        junit = os.path.join(d, "junit.xml")
        try:
            subprocess.run([sys.executable, "-m", "pytest", "-q", "-p", "no:cacheprovider",
                            f"--junitxml={junit}", os.path.basename(test)],
                           cwd=d, capture_output=True, text=True, timeout=60)
        except subprocess.TimeoutExpired:
            return "timeout", 0, 0, []
        if not os.path.exists(junit):
            return "import_error", 0, 0, []
        root = ET.parse(junit).getroot()
        cases = root.iter("testcase")
        total, passed, failed = 0, 0, []
        for c in cases:
            total += 1
            if any(ch.tag in ("failure", "error") for ch in c):
                failed.append(c.get("name"))
            elif not any(ch.tag == "skipped" for ch in c):
                passed += 1
        if total == 0:
            return "import_error", 0, 0, []
        return "ok", total, passed, failed


def main():
    rows = []
    for line in open(os.path.join(HERE, "results", "runs.jsonl"), encoding="utf-8"):
        r = json.loads(line)
        if not r.get("ok"):
            continue
        status, total, passed, failed = evaluate(r["mechanism"], r["code"])
        rows.append({"model": r["model"], "condition": r["condition"], "mechanism": r["mechanism"],
                     "replication": r["replication"], "status": status, "tests_total": total,
                     "tests_passed": passed,
                     "all_pass": int(status == "ok" and total > 0 and passed == total),
                     "failed_tests": ";".join(failed)})
    out = os.path.join(HERE, "results", "evaluation.csv")
    with open(out, "w", newline="", encoding="utf-8") as f:
        w = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
        w.writeheader(); w.writerows(rows)
    print(f"{len(rows)} runs evaluated -> {out}")


if __name__ == "__main__":
    main()
