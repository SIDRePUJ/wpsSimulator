"""Synthetic arithmetic/guard tests; not empirical allocation evidence."""

import csv
import tempfile
import unittest
from pathlib import Path

from analyze_upa_results import report


class UpaAnalysisTest(unittest.TestCase):
    def table(self, rows):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        path = Path(temporary.name) / "joined.csv"
        with path.open("w", encoding="utf-8", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=[
                "weather", "scarcity_ratio", "rule", "seed", "parameter_set",
                "plot_id", "family_alias", "area_ha", "full_t", "actual_t", "gross_m3",
            ])
            writer.writeheader()
            writer.writerows(rows)
        return path

    @staticmethod
    def row(rule, plot, family, actual, area=1, full=10):
        return {"weather": "dry-proxy", "scarcity_ratio": 0.5, "rule": rule,
                "seed": "1", "parameter_set": "A", "plot_id": plot,
                "family_alias": family, "area_ha": area, "full_t": full,
                "actual_t": actual, "gross_m3": 50}

    def sample(self):
        base = "PROPORTIONAL_DEMAND"
        other = "SMALL_PLOT_FLOOR"
        return [self.row(base, "a1", "a", 10), self.row(base, "a2", "a", 0),
                self.row(base, "b1", "b", 5), self.row(base, "b2", "b", 5),
                self.row(other, "a1", "a", 9), self.row(other, "a2", "a", 3),
                self.row(other, "b1", "b", 4), self.row(other, "b2", "b", 4)]

    def test_aggregates_before_inequality(self):
        result = report(self.table(self.sample()))
        self.assertEqual("UPA", result["unit_of_inequality"])
        base = next(row for row in result["scenario_summaries"]
                    if row["rule"] == "PROPORTIONAL_DEMAND")
        self.assertEqual(2, base["upa_count"])
        self.assertNotIn("plot_count", base)
        self.assertAlmostEqual(0, base["gini_relative_loss"])
        self.assertAlmostEqual(20, base["production_t"])
        a = next(row for row in result["upa_rows"] if row["family_alias"] == "a"
                 and row["rule"] == "PROPORTIONAL_DEMAND")
        self.assertEqual(["a1", "a2"], a["eligible_plot_ids"])
        self.assertEqual(2, a["area_ha"])
        self.assertAlmostEqual(0.5, a["relative_loss"])
        self.assertAlmostEqual(0, result["paired_rule_contrasts"][0]["delta_production_t"])

    def test_rejects_changed_owner(self):
        rows = self.sample()
        rows[4]["family_alias"] = "b"
        with self.assertRaisesRegex(ValueError, "unmatched owner"):
            report(self.table(rows))

    def test_size_quartile_uses_aggregated_upa_area(self):
        base = "PROPORTIONAL_DEMAND"
        rows = [self.row(base, "a1", "a", 5),
                self.row(base, "a2", "a", 5),
                self.row(base, "b1", "b", 27, area=3, full=30)]
        summary = report(self.table(rows))["scenario_summaries"][0]
        self.assertEqual(2, summary["upa_count"])
        self.assertAlmostEqual(0.5, summary["smallest_quartile_p90_loss"])
        self.assertAlmostEqual(5, summary["smallest_to_largest_quartile_mean_relative_loss_ratio"])

    def test_smallest_area_upa_metrics_include_all_four_tied_families(self):
        baseline = "PROPORTIONAL_DEMAND"
        other = "SMALL_PLOT_FLOOR"
        rows = []
        for rule, actuals in ((baseline, (9, 8, 7, 6)),
                              (other, (9.5, 9, 8.5, 4))):
            for family, actual in zip("abcd", actuals):
                rows.append(self.row(rule, family, family, actual, area=2))
            rows.append(self.row(rule, "large", "large", 36, area=8, full=40))

        result = report(self.table(rows))
        summaries = {row["rule"]: row for row in result["scenario_summaries"]}
        base = summaries[baseline]
        floor = summaries[other]
        self.assertEqual(4, base["smallest_area_upa_count"])
        self.assertEqual(4, floor["smallest_area_upa_count"])
        self.assertAlmostEqual(0.25, base["smallest_area_upa_mean_relative_loss"])
        self.assertAlmostEqual(0.4, base["smallest_area_upa_max_relative_loss"])
        self.assertAlmostEqual(0.225, floor["smallest_area_upa_mean_relative_loss"])
        self.assertAlmostEqual(0.6, floor["smallest_area_upa_max_relative_loss"])
        self.assertAlmostEqual(0.2, base["smallest_quartile_p90_loss"])
        contrast = result["paired_rule_contrasts"][0]
        self.assertAlmostEqual(-0.025, contrast["delta_smallest_area_upa_mean_relative_loss"])
        self.assertAlmostEqual(0.2, contrast["delta_smallest_area_upa_max_relative_loss"])

    def test_rejects_missing_plot(self):
        with self.assertRaisesRegex(ValueError, "missing matched proportional baseline or plot IDs"):
            report(self.table(self.sample()[:-1]))

    def test_rejects_changed_area_or_full_reference(self):
        rows = self.sample()
        rows[4]["area_ha"] = 2
        with self.assertRaisesRegex(ValueError, "unmatched area"):
            report(self.table(rows))

        rows = self.sample()
        rows[4]["full_t"] = 11
        with self.assertRaisesRegex(ValueError, "unmatched area or full-water reference"):
            report(self.table(rows))

    def test_rejects_duplicate_plot(self):
        rows = self.sample()
        rows.append(dict(rows[0]))
        with self.assertRaisesRegex(ValueError, "duplicate plot"):
            report(self.table(rows))

    def test_rejects_empty_owner(self):
        rows = self.sample()
        rows[0]["family_alias"] = ""
        with self.assertRaisesRegex(ValueError, "empty family_alias"):
            report(self.table(rows))


if __name__ == "__main__":
    unittest.main()
