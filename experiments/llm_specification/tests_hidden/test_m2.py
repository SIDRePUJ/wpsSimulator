import pytest
from m2_productivity import productivity_factor, task_duration


@pytest.mark.parametrize("phi,f", [(1.0, 1.12), (0.7, 1.12), (0.69, 1.06), (0.51, 1.06),
                                   (0.5, 1.00), (0.31, 1.00), (0.3, 0.90), (0.0, 0.90)])
def test_factor_levels_and_boundaries(phi, f):
    assert productivity_factor(phi) == pytest.approx(f)


def test_duration_positive_affect_shortens_task():
    assert task_duration(360, 0.8) == pytest.approx(316.8)


def test_duration_negative_affect_lengthens_task():
    assert task_duration(720, 0.1) == pytest.approx(792.0)


def test_duration_neutral_band_unchanged():
    assert task_duration(360, 0.4) == pytest.approx(360.0)


def test_duration_without_emotions_is_base_time():
    assert task_duration(360, 0.9, emotions_enabled=False) == pytest.approx(360.0)


@pytest.mark.parametrize("phi", [-0.01, 1.01])
def test_phi_out_of_range(phi):
    with pytest.raises(ValueError):
        productivity_factor(phi)
