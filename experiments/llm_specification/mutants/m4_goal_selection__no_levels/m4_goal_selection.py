"""Reference implementation of M4 (lexicographic goal selection with affective blend). Not shown to the LLM."""


def modulated_contribution(c, lam, phi):
    return (1.0 - lam) * c + lam * phi


def select_goal(goals, phi, emotions_enabled=True):
    active = [g for g in goals if g["active"]]
    if not active:
        return None
    top = None
    best, best_value = None, None
    for g in active:
        lam = g["lam"] if emotions_enabled else 0.0
        value = modulated_contribution(g["c"], lam, phi)
        if best is None or value > best_value:
            best, best_value = g, value
    return best["name"]
