import pytest
from m3_loan import should_request_loan, Bank


def test_threshold_inclusive():
    assert should_request_loan(300_000, False) is True
    assert should_request_loan(300_001, False) is False


def test_active_loan_blocks_request():
    assert should_request_loan(0, True) is False


def test_approval_returns_principal():
    bank = Bank()
    assert bank.request_loan("f1") == pytest.approx(2_000_000)
    assert bank.has_active_loan("f1")


def test_second_request_denied_while_active():
    bank = Bank()
    bank.request_loan("f1")
    assert bank.request_loan("f1") == pytest.approx(0.0)


def test_capital_ceiling_is_strict():
    assert Bank(capital=2_000_000).request_loan("f1") == pytest.approx(0.0)
    assert Bank(capital=2_000_001).request_loan("f1") == pytest.approx(2_000_000)


def test_capital_is_not_consumed():
    bank = Bank(capital=3_000_000)
    assert bank.request_loan("f1") == pytest.approx(2_000_000)
    assert bank.request_loan("f2") == pytest.approx(2_000_000)


def test_installment_without_interest():
    bank = Bank()
    bank.request_loan("f1")
    assert bank.current_term("f1") == pytest.approx(2_000_000 / 12)


def test_no_term_without_loan():
    assert Bank().current_term("f9") == pytest.approx(0.0)


def test_twelve_payments_close_loan_and_total_equals_principal():
    bank = Bank()
    bank.request_loan("f1")
    total = 0.0
    for _ in range(12):
        amount = bank.current_term("f1")
        assert bank.pay_term("f1", amount) is True
        total += amount
    assert total == pytest.approx(2_000_000)
    assert not bank.has_active_loan("f1")
    assert bank.pay_term("f1", 2_000_000 / 12) is False
    assert bank.request_loan("f1") == pytest.approx(2_000_000)


def test_wrong_amount_rejected():
    bank = Bank()
    bank.request_loan("f1")
    assert bank.pay_term("f1", 100_000) is False
    assert bank.current_term("f1") == pytest.approx(2_000_000 / 12)
