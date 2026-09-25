class EmotionalAxis:
    def __init__(self, base=0.0, gamma=0.24, value=0.0):
        raise NotImplementedError

    def update(self, t_days, deltas=()):
        raise NotImplementedError
