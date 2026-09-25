"""Reference implementation of M5 (FAO-56 water-stress coefficient). Not shown to the LLM."""


def depletion_fraction(p_tab, etc_mm_day):
    p = p_tab + 0.04 * (5.0 - etc_mm_day)
    return max(0.1, min(0.8, p))


def water_stress_coefficient(dr_mm, taw_mm, p):
    if taw_mm <= 0:
        raise ValueError("taw_mm must be positive")
    if dr_mm < 0:
        raise ValueError("dr_mm must be non-negative")
    raw = p * taw_mm
    if dr_mm <= raw:
        return 1.0
    ks = (taw_mm - dr_mm) / ((1.0 - p) * taw_mm)
    return max(0.0, min(1.0, ks))
