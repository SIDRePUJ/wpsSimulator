import pytest
from m1_forgetting import EmotionalAxis


def test_linear_fade_one_day():
    a = EmotionalAxis(base=0.0, gamma=0.24, value=0.5)
    assert a.update(1.0) == pytest.approx(0.26)


def test_no_overshoot():
    a = EmotionalAxis(base=0.0, gamma=0.24, value=0.5)
    assert a.update(10.0) == pytest.approx(0.0)


def test_depends_on_simulated_time_not_number_of_updates():
    a = EmotionalAxis(base=0.0, gamma=0.1, value=0.8)
    b = EmotionalAxis(base=0.0, gamma=0.1, value=0.8)
    for t in (0.25, 0.5, 0.75, 1.0, 1.5, 2.0):
        a.update(t)
    b.update(2.0)
    assert a.value == pytest.approx(b.value)
    assert b.value == pytest.approx(0.6)


def test_repeated_update_same_time_does_not_decay():
    a = EmotionalAxis(base=0.0, gamma=0.24, value=0.5)
    a.update(1.0)
    assert a.update(1.0) == pytest.approx(0.26)


def test_negative_value_moves_up_to_base():
    a = EmotionalAxis(base=0.0, gamma=0.06, value=-0.3)
    assert a.update(2.0) == pytest.approx(-0.18)


def test_nonzero_base():
    a = EmotionalAxis(base=0.2, gamma=0.1, value=-0.1)
    assert a.update(1.0) == pytest.approx(0.0)
    assert a.update(5.0) == pytest.approx(0.2)


def test_deltas_added_after_decay():
    a = EmotionalAxis(base=0.0, gamma=0.24, value=0.5)
    assert a.update(1.0, [0.1, -0.05]) == pytest.approx(0.31)


def test_clipping_upper_and_lower():
    a = EmotionalAxis(base=0.0, gamma=0.24, value=0.9)
    assert a.update(0.0, [0.5]) == pytest.approx(1.0)
    b = EmotionalAxis(base=0.0, gamma=0.24, value=-0.9)
    assert b.update(0.0, [-0.5]) == pytest.approx(-1.0)


def test_time_cannot_decrease():
    a = EmotionalAxis(base=0.0, gamma=0.24, value=0.5)
    a.update(3.0)
    with pytest.raises(ValueError):
        a.update(2.0)
