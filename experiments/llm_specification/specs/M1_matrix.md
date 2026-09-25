# Traceability-matrix row T10: emotional axis with forgetting

| Field | Content |
|---|---|
| Objective | O3: assess whether affect-informed decision-making changes household outcomes |
| Requirement | FR1: household agents hold an affective state updated by the events they perceive |
| Evidence source | Fieldwork on stress, expectations, and perceived risk; OCC appraisal model |
| Implementation unit | `EmotionalAxis` in `m1_forgetting.py` |
| Parameters (baseline) | gamma = 0.24 per simulated day (happiness-sadness); base b = 0 |

## Behaviour specification

- **State:** intensity `value` e in [-1, 1]; base b in [-1, 1]; rate gamma > 0; time of the previous update `last_time` (simulated days), initially 0.
- **Trigger:** a call `update(t_days, deltas)`, where `t_days` is the current simulated time in days and `deltas` are the appraisal increments of the events perceived in this update.
- **Rules (in this order):**
  1. If `t_days` < `last_time`, raise `ValueError` (simulated time never goes backwards).
  2. Elapsed time dt = `t_days` - `last_time` (simulated days, not wall-clock time and not number of calls).
  3. Linear relaxation towards b without overshoot: e moves towards b by min(gamma * dt, |b - e|).
  4. Add the sum of `deltas`.
  5. Clip e to [-1, 1]; store it in `value`; set `last_time` = `t_days`; return e.
- **Construction:** `EmotionalAxis(base=0.0, gamma=0.24, value=0.0)`; raise `ValueError` if base or value is outside [-1, 1] or gamma <= 0.
- **Boundary conventions:** repeated updates at the same time produce no further relaxation; splitting an interval into several updates gives the same result as a single update over the whole interval.

## Test oracle (examples)

| Case | Expected |
|---|---|
| e = 0.6, b = 0, gamma = 0.2; `update(2.0)` | 0.2 |
| e = -0.1, b = 0, gamma = 0.24; `update(1.0)` | 0.0 (no overshoot) |
| e = 0.9, b = 0, gamma = 0.1; `update(1.0)` then `update(3.0)` | 0.8, then 0.6 |
| e = 0, b = 0; `update(0.0, [0.4])` | 0.4 |
| e = -0.95, b = 0; `update(0.0, [-0.2])` | -1.0 (clipped) |
