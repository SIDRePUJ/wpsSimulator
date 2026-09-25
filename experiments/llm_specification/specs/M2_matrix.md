# Traceability-matrix row T11: productivity factor and task duration

| Field | Content |
|---|---|
| Objective | O3: assess whether affect-informed decision-making changes household outcomes |
| Requirement | FR1: household agents hold an affective state that modulates their performance |
| Evidence source | Experimental evidence that positive affect raises productivity by about 10-12% and negative events lower it |
| Implementation unit | `productivity_factor`, `task_duration` in `m2_productivity.py` |
| Parameters (baseline) | f in {1.12, 1.06, 1.00, 0.90} |

## Behaviour specification

- **Input:** affective contribution phi in [0, 1]; base task duration tau (minutes, >= 0); flag `emotions_enabled`.
- **Rules:**
  1. `productivity_factor(phi)`: raise `ValueError` if phi is outside [0, 1]; return 1.12 if phi >= 0.7; 1.06 if 0.5 < phi < 0.7; 1.00 if 0.3 < phi <= 0.5; 0.90 if phi <= 0.3.
  2. `task_duration(tau, phi, emotions_enabled=True)`: raise `ValueError` if tau < 0; if emotions are disabled return tau (non-affective baseline); otherwise return (2 - f) * tau with f = `productivity_factor(phi)`.
- **Boundary conventions:** 0.7 belongs to the highest level; 0.5 and 0.3 belong to the lower of the two adjacent levels.

## Test oracle (examples)

| Case | Expected |
|---|---|
| `productivity_factor(0.85)` | 1.12 |
| `productivity_factor(0.6)` | 1.06 |
| `productivity_factor(0.45)` | 1.00 |
| `productivity_factor(0.2)` | 0.90 |
| `task_duration(100, 0.9)` | 88.0 |
| `task_duration(100, 0.9, emotions_enabled=False)` | 100.0 |
