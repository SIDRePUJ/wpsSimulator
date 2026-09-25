import pytest
from m5_water_stress import depletion_fraction, water_stress_coefficient


def test_depletion_fraction_adjustment():
    assert depletion_fraction(0.5, 5.0) == pytest.approx(0.5)
    assert depletion_fraction(0.5, 3.0) == pytest.approx(0.58)


def test_depletion_fraction_bounds():
    assert depletion_fraction(0.75, 0.0) == pytest.approx(0.8)
    assert depletion_fraction(0.2, 10.0) == pytest.approx(0.1)


def test_no_stress_up_to_raw():
    assert water_stress_coefficient(0.0, 100.0, 0.5) == pytest.approx(1.0)
    assert water_stress_coefficient(50.0, 100.0, 0.5) == pytest.approx(1.0)


def test_linear_decrease_between_raw_and_taw():
    assert water_stress_coefficient(75.0, 100.0, 0.5) == pytest.approx(0.5)
    assert water_stress_coefficient(100.0, 100.0, 0.5) == pytest.approx(0.0)


def test_clipped_beyond_taw():
    assert water_stress_coefficient(120.0, 100.0, 0.5) == pytest.approx(0.0)


def test_invalid_inputs():
    with pytest.raises(ValueError):
        water_stress_coefficient(10.0, 0.0, 0.5)
    with pytest.raises(ValueError):
        water_stress_coefficient(-1.0, 100.0, 0.5)
