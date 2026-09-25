# Traceability-matrix row T8: formal loan protocol

| Field | Content |
|---|---|
| Objectives | O1, O2 |
| Requirements | FR1, FR3: households manage money; exchanges with financial institutions |
| Evidence source | Bank practice reported by experts |
| Implementation units | `should_request_loan`, `Bank` in `m3_loan.py` |
| Parameters (baseline) | mu = COP 300,000; kappa = COP 2,000,000; n = 12 installments; no interest; lending capital COP 10,000,000 |

## Behaviour specification (protocol)

- **Trigger (household):** `should_request_loan(money, has_active_loan, mu=300_000)` returns True if and only if money <= mu and there is no active loan.
- **Approval (bank):** `Bank.request_loan(family_id)` approves if and only if the household has no active loan and capital > kappa (strict). On approval it records an active loan with 0 installments paid and returns kappa; otherwise it returns 0. The capital is a fixed lending ceiling: it is checked at approval and never modified by loans or payments.
- **Term (bank):** `Bank.current_term(family_id)` returns kappa / n for a household with an active loan, 0 otherwise.
- **Payment (bank):** `Bank.pay_term(family_id, amount)` returns True and counts one installment if and only if there is an active loan and amount equals kappa / n (within 1e-6); otherwise it returns False and changes nothing. After the n-th accepted payment the loan is closed.
- **Query:** `Bank.has_active_loan(family_id)`.
- **Constructor:** `Bank(capital=10_000_000, kappa=2_000_000, n_installments=12)`.

## Test oracle (examples)

| Case | Expected |
|---|---|
| `should_request_loan(250_000, False)` | True |
| `should_request_loan(400_000, False)` | False |
| `Bank(capital=5_000_000, kappa=1_000_000, n_installments=4).request_loan("a")` | 1,000,000 |
| same bank: `current_term("a")` | 250,000 |
| same bank: four calls `pay_term("a", 250_000)` | True each; then `has_active_loan("a")` is False |
