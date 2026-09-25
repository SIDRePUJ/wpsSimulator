#!/usr/bin/env python3
"""Pre-registered analysis (see PROTOCOL.md, section 7).

Primary outcome: all hidden conformance tests pass (binary), matrix vs prose condition.
- Wilson 95% intervals of the all-pass rate per condition and per condition x model.
- Cochran-Mantel-Haenszel test stratified by mechanism x model (pooled odds ratio).
Secondary outcome: fraction of hidden tests passed, compared with a Wilcoxon signed-rank
test over the mechanism x model strata (paired by stratum).
Writes results/summary.md.
"""
import csv, os
from collections import defaultdict
import numpy as np
from scipy.stats import wilcoxon
from statsmodels.stats.contingency_tables import StratifiedTable
from statsmodels.stats.proportion import proportion_confint

HERE = os.path.dirname(os.path.abspath(__file__))
rows = list(csv.DictReader(open(os.path.join(HERE, "results", "evaluation.csv"), encoding="utf-8")))
for r in rows:
    r["all_pass"] = int(r["all_pass"])
    r["frac"] = int(r["tests_passed"]) / int(r["tests_total"]) if int(r["tests_total"]) else 0.0

out = ["# Results of the specification-format experiment", ""]

def rate(sub):
    k, n = sum(r["all_pass"] for r in sub), len(sub)
    lo, hi = proportion_confint(k, n, method="wilson") if n else (float("nan"),) * 2
    return k, n, lo, hi

out += ["| Group | All-pass | n | Rate | 95% CI (Wilson) | Mean fraction of tests passed |", "|---|---|---|---|---|---|"]
groups = defaultdict(list)
for r in rows:
    groups[(r["condition"],)].append(r)
    groups[(r["condition"], r["model"])].append(r)
    groups[(r["condition"], r["mechanism"])].append(r)
for key in sorted(groups):
    sub = groups[key]
    k, n, lo, hi = rate(sub)
    out.append(f"| {' / '.join(key)} | {k} | {n} | {k/n:.2f} | [{lo:.2f}, {hi:.2f}] | {np.mean([r['frac'] for r in sub]):.2f} |")

tables, diffs = [], []
strata = sorted({(r["mechanism"], r["model"]) for r in rows})
for s in strata:
    sub = [r for r in rows if (r["mechanism"], r["model"]) == s]
    m = [r for r in sub if r["condition"] == "matrix"]
    p = [r for r in sub if r["condition"] == "prose"]
    a, b = sum(r["all_pass"] for r in m), len(m) - sum(r["all_pass"] for r in m)
    c, d = sum(r["all_pass"] for r in p), len(p) - sum(r["all_pass"] for r in p)
    tables.append(np.array([[a, b], [c, d]], dtype=float))
    diffs.append(np.mean([r["frac"] for r in m]) - np.mean([r["frac"] for r in p]))

out += ["", "## Primary test (Cochran-Mantel-Haenszel, strata = mechanism x model)", ""]
try:
    st = StratifiedTable(tables)
    res = st.test_null_odds(correction=True)
    out.append(f"Pooled odds ratio (matrix vs prose) = {st.oddsratio_pooled:.2f}; CMH statistic = {res.statistic:.2f}; p = {res.pvalue:.4f}")
except Exception as e:
    out.append(f"CMH test not computable: {e!r} (report descriptive rates only)")

out += ["", "## Secondary test (Wilcoxon signed-rank on per-stratum difference in mean fraction passed)", ""]
nz = [x for x in diffs if abs(x) > 1e-12]
if len(nz) >= 1:
    w = wilcoxon(diffs, zero_method="wilcox")
    out.append(f"Median difference = {np.median(diffs):.3f}; W = {w.statistic:.1f}; p = {w.pvalue:.4f}; strata = {len(diffs)}")
else:
    out.append("All per-stratum differences are zero.")

open(os.path.join(HERE, "results", "summary.md"), "w", encoding="utf-8").write("\n".join(out) + "\n")
print("\n".join(out))
