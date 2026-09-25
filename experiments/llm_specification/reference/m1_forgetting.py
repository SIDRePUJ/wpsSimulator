"""Reference implementation of M1 (emotional axis with linear forgetting). Not shown to the LLM."""


class EmotionalAxis:
    def __init__(self, base=0.0, gamma=0.24, value=0.0):
        if not -1.0 <= base <= 1.0 or not -1.0 <= value <= 1.0:
            raise ValueError("base and value must lie in [-1, 1]")
        if gamma <= 0:
            raise ValueError("gamma must be positive")
        self.base = base
        self.gamma = gamma
        self.value = value
        self.last_time = 0.0

    def update(self, t_days, deltas=()):
        if t_days < self.last_time:
            raise ValueError("simulated time cannot decrease")
        dt = t_days - self.last_time
        e, b = self.value, self.base
        step = min(self.gamma * dt, abs(b - e))
        if b > e:
            e = e + step
        elif b < e:
            e = e - step
        e = e + sum(deltas)
        self.value = max(-1.0, min(1.0, e))
        self.last_time = t_days
        return self.value
