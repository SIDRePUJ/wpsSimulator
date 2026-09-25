# Protocol: Does a traceability-matrix specification improve the conformance of LLM-generated ABSS code?

Version 1.0 (frozen before any model call). Companion to Section 5.4 of the manuscript
"A Six-Phase Methodology for Agent-Based Simulation Development". All materials referred to
below are in this folder; their SHA-256 hashes are listed in `MANIFEST.sha256`.

## 1. Purpose and position in the evaluation

The methodology prescribes, in Phase 3, a traceability-matrix row for each mechanism, with a
structured behaviour specification and a test oracle, and it requires, in Phase 4, that code
produced or modified by an AI assistant be accepted only if it passes the conformance tests of
its rows. This experiment is a controlled (artificial, summative) evaluation of one element of
that design: whether giving a large language model (LLM) the specification in the form of a
matrix row, rather than as model-description prose, changes the conformance of the code it
generates. It does not evaluate the methodology as a whole.

## 2. Hypotheses (pre-registered)

- **H1 (primary).** The proportion of generated implementations that pass all hidden
  conformance tests is higher with the matrix-row specification than with the prose
  specification.
- **H2 (secondary).** The mean fraction of hidden tests passed is higher with the matrix-row
  specification.
- The direction is stated in advance; results are reported whatever their direction.

## 3. Mechanisms

Five mechanisms of WellProdSim, chosen because each has a precise specification in the
released version and because three of them correspond to discrepancies that were found
between specification and code during the revision (time base of forgetting, bounds of the
depletion fraction, level structure of deliberation):

| ID | Mechanism | Matrix row | Hidden tests |
|---|---|---|---|
| M1 | Emotional axis with linear forgetting on simulated time | T10 | 9 |
| M2 | Productivity factor and task duration | T11 | 14 |
| M3 | Formal loan protocol (household and bank) | T8 | 10 |
| M4 | Lexicographic goal selection with affective blend | T11b | 7 |
| M5 | FAO-56 water-stress coefficient | T4 | 6 |

The mechanisms are re-expressed as standalone Python modules with fixed signatures (`stubs/`),
so that generation and testing do not depend on the Java code base of the simulator, and so
that the task cannot be solved by retrieving the simulator's public code.

## 4. Conditions

- **Prose (`specs/M*_prose.md`).** A model-description paragraph in the style of a published
  model description (ODD-like narrative), containing every rule, parameter value, and boundary
  convention needed to implement the mechanism.
- **Matrix row (`specs/M*_matrix.md`).** The same rules, parameter values, and boundary
  conventions, organized as a traceability-matrix row (objective, requirement, evidence
  source, implementation unit, parameters, behaviour specification with ordered rules) plus a
  test oracle of 4–6 worked examples.
- The information content of both conditions is the same; the matrix condition adds structure
  and worked examples. The worked examples are different from the hidden tests.
- Both conditions use the same prompt template (`prompt_template.txt`) and the same stub.

## 5. Models, tool, and settings

- Models: the Claude Sonnet and Claude Opus versions used by the authors for the development
  of the released version (exact model identifiers recorded in `config.json` before running).
- Primary mode (`--mode api`): single-shot generation through the Anthropic Messages API, no
  tools, temperature 1.0, maximum 4 000 output tokens. This isolates the effect of the
  specification from any tool use or test execution by the model.
- Optional exploratory mode (`--mode cli`): Claude Code CLI in non-interactive mode, run in an
  empty temporary directory that contains no tests, reference code, or simulator code. Results
  of this mode are reported separately and are not part of the confirmatory analysis.
- Replications: 10 independent generations per mechanism × condition × model
  (5 × 2 × 2 × 10 = 200 generations). Run order is randomized with a fixed seed.

## 6. Outcomes and evaluation

- Each generated module is placed alone in a temporary directory with its hidden test file and
  executed with pytest (timeout 60 s).
- **Primary outcome:** all hidden tests pass (1/0). A module that cannot be imported counts as 0.
- **Secondary outcomes:** fraction of hidden tests passed; failure category of each failed
  test, coded by the authors after unblinding into: boundary convention, time base, missing
  rule, extra or unspecified behaviour, interface or signature, error handling.
- The hidden tests were validated on reference implementations (`reference/`, all pass) and on
  six mutants that reproduce the historical defect types (`mutants/`, each detected by at least
  one test).

## 7. Analysis (pre-registered)

- Descriptive: all-pass rate with Wilson 95% intervals per condition, per condition × model, and
  per condition × mechanism; mean fraction of tests passed.
- H1: Cochran–Mantel–Haenszel test of condition on the all-pass outcome, stratified by
  mechanism × model (10 strata), with the pooled odds ratio.
- H2: Wilcoxon signed-rank test on the per-stratum difference (matrix − prose) in the mean
  fraction of tests passed.
- Significance level 0.05, two-sided. No interim analyses. `analyze.py` implements exactly this.

## 8. Exclusions and deviations

- API or CLI failures (no response) are logged and the run is repeated; no other run is
  excluded.
- Any deviation from this protocol (model unavailable, changed settings) is recorded in
  `DEVIATIONS.md` and reported in the paper.

## 9. Threats to validity

- **Construct.** The experiment evaluates the format of the specification for five small
  mechanisms; it does not show that the methodology improves the development of a whole
  simulator.
- **Experimenter bias.** The specifications, tests, and reference implementations were written
  by the authors; they are frozen and hashed before any model call and are published.
- **Contamination.** The simulator's code is public; the mechanisms are re-expressed as
  standalone Python modules with new names to reduce retrieval of the original code.
- **Generalization.** Results depend on the model versions; the exact identifiers and dates are
  reported.
- **Conservative design.** Real published descriptions are often less complete than the prose
  condition, which contains every rule; the difference observed here is therefore likely to
  underestimate the difference with typical model descriptions.

## 10. Reporting

Report all runs (`results/runs.jsonl`), the evaluation table (`results/evaluation.csv`), the
summary (`results/summary.md`), and the failure taxonomy, whatever the results.
