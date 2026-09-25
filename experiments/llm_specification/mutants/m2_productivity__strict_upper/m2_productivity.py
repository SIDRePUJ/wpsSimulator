"""Reference implementation of M2 (productivity factor and task duration). Not shown to the LLM."""


def productivity_factor(phi):
    if not 0.0 <= phi <= 1.0:
        raise ValueError("phi must lie in [0, 1]")
    if phi > 0.7:
        return 1.12
    if phi > 0.5:
        return 1.06
    if phi > 0.3:
        return 1.00
    return 0.90


def task_duration(tau_minutes, phi, emotions_enabled=True):
    if tau_minutes < 0:
        raise ValueError("tau_minutes must be non-negative")
    if not emotions_enabled:
        return float(tau_minutes)
    f = productivity_factor(phi)
    return (2.0 - f) * tau_minutes
