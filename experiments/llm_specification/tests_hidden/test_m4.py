import pytest
from m4_goal_selection import modulated_contribution, select_goal


def g(name, level, active, c, lam):
    return {"name": name, "level": level, "active": active, "c": c, "lam": lam}


def test_blend_formula():
    assert modulated_contribution(0.75, 0.5, 0.84) == pytest.approx(0.795)


def test_higher_level_wins_over_larger_contribution():
    goals = [g("leisure", 3, True, 1.0, 0.0), g("pay_debts", 1, True, 0.2, 0.0)]
    assert select_goal(goals, 0.5) == "pay_debts"


def test_affect_can_change_choice_within_level():
    goals = [g("check_crops", 2, True, 0.75, 0.5), g("sell", 2, True, 0.8, 0.0)]
    assert select_goal(goals, 0.84) == "sell"
    assert select_goal(goals, 0.95) == "check_crops"


def test_ablation_ignores_affect():
    goals = [g("check_crops", 2, True, 0.75, 0.5), g("sell", 2, True, 0.8, 0.0)]
    assert select_goal(goals, 0.95, emotions_enabled=False) == "sell"


def test_inactive_goals_ignored_even_at_top_level():
    goals = [g("vitals", 1, False, 1.0, 0.0), g("plant", 2, True, 0.9, 0.5)]
    assert select_goal(goals, 0.2) == "plant"


def test_tie_broken_by_list_order():
    goals = [g("a", 2, True, 0.6, 0.0), g("b", 2, True, 0.6, 0.0)]
    assert select_goal(goals, 0.5) == "a"


def test_no_active_goal_returns_none():
    assert select_goal([g("a", 1, False, 1.0, 0.0)], 0.5) is None
