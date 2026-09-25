def should_request_loan(money, has_active_loan, mu=300_000):
    raise NotImplementedError


class Bank:
    def __init__(self, capital=10_000_000, kappa=2_000_000, n_installments=12):
        raise NotImplementedError

    def has_active_loan(self, family_id):
        raise NotImplementedError

    def request_loan(self, family_id):
        raise NotImplementedError

    def current_term(self, family_id):
        raise NotImplementedError

    def pay_term(self, family_id, amount):
        raise NotImplementedError
