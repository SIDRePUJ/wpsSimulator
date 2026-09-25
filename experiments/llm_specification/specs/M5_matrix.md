# Traceability-matrix row T4: water-stress coefficient (FAO-56)

| Field | Content |
|---|---|
| Objective | O2 |
| Requirements | FR2, FR7: crop growth driven by climate and soil water |
| Evidence source | FAO-56 soil-water-balance rule; regional climate statistics |
| Implementation units | `depletion_fraction`, `water_stress_coefficient` in `m5_water_stress.py` |
| Parameters (baseline) | p bounded to [0.1, 0.8] |

## Behaviour specification

- **Rules:**
  1. `depletion_fraction(p_tab, etc_mm_day)` = p_tab + 0.04 * (5 - ETc), bounded to [0.1, 0.8].
  2. `water_stress_coefficient(dr_mm, taw_mm, p)`: raise `ValueError` if TAW <= 0 or Dr < 0; RAW = p * TAW; if Dr <= RAW return 1.0; otherwise return (TAW - Dr) / ((1 - p) * TAW) bounded to [0, 1].
- **Boundary conventions:** Ks = 1 exactly at Dr = RAW; Ks = 0 at Dr = TAW and beyond.

## Test oracle (examples)

| Case | Expected |
|---|---|
| `depletion_fraction(0.55, 4.0)` | 0.59 |
| `water_stress_coefficient(20.0, 80.0, 0.5)` | 1.0 |
| `water_stress_coefficient(60.0, 80.0, 0.5)` | 0.5 |
| `water_stress_coefficient(80.0, 80.0, 0.5)` | 0.0 |
