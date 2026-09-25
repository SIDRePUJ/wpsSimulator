"""Reference implementation of M3 (formal loan protocol). Not shown to the LLM."""


def should_request_loan(money, has_active_loan, mu=300_000):
    return money <= mu and not has_active_loan


class Bank:
    def __init__(self, capital=10_000_000, kappa=2_000_000, n_installments=12):
        self.capital = capital
        self.kappa = kappa
        self.n_installments = n_installments
        self._loans = {}  # family_id -> installments paid

    def has_active_loan(self, family_id):
        return family_id in self._loans

    def request_loan(self, family_id):
        if family_id in self._loans or not self.capital > self.kappa:
            return 0.0
        self._loans[family_id] = 0
        self.capital -= self.kappa
        return float(self.kappa)

    def current_term(self, family_id):
        if family_id not in self._loans:
            return 0.0
        return self.kappa / self.n_installments

    def pay_term(self, family_id, amount):
        if family_id not in self._loans:
            return False
        if abs(amount - self.kappa / self.n_installments) > 1e-6:
            return False
        self._loans[family_id] += 1
        if self._loans[family_id] >= self.n_installments:
            del self._loans[family_id]
        return True
