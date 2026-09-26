"""Small synthetic fixtures for paired-result analysis, not study findings."""

import csv
import tempfile
import unittest
from pathlib import Path

from analyze_physical_results import contrasts, read_results, sensitivity_envelope, summarize


class PhysicalResultAnalysisTest(unittest.TestCase):
    def make_csv(self, rows):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        path = Path(temporary.name) / "results.csv"
        with path.open("w", encoding="utf-8", newline="") as stream:
            writer = csv.DictWriter(stream, fieldnames=[
                "weather", "scarcity_ratio", "rule", "seed", "parameter_set",
                "plot_id", "area_ha", "full_t", "actual_t", "gross_m3",
            ])
            writer.writeheader()
            writer.writerows(rows)
        return path

    def sample(self, rule, plot, area, full, actual):
        return {"weather": "dry-proxy", "scarcity_ratio": 0.5, "rule": rule,
                "seed": "1", "parameter_set": "A", "plot_id": plot,
                "area_ha": area, "full_t": full, "actual_t": actual, "gross_m3": 50}

    def test_paired_efficiency_and_small_plot_loss(self):
        rows = [
            self.sample("PROPORTIONAL_DEMAND", "small", 1, 10, 6),
            self.sample("PROPORTIONAL_DEMAND", "large", 3, 30, 20),
            self.sample("SMALL_PLOT_FLOOR", "small", 1, 10, 8),
            self.sample("SMALL_PLOT_FLOOR", "large", 3, 30, 16),
        ]
        groups = read_results(self.make_csv(rows))
        records = summarize(groups)
        comparison = contrasts(groups, records)[0]
        self.assertAlmostEqual(-2, comparison["delta_production_t"])
        self.assertAlmostEqual(-0.2, comparison["delta_smallest_quartile_p90_loss"])
        envelope = sensitivity_envelope([comparison])[0]
        self.assertEqual(1, envelope["parameter_sets"])
        self.assertEqual(1.0, envelope["share_parameter_sets_improving_smallest_quartile_loss"])

    def test_rejects_unmatched_reference(self):
        rows = [self.sample("PROPORTIONAL_DEMAND", "a", 1, 10, 5),
                self.sample("SMALL_PLOT_FLOOR", "b", 1, 10, 5)]
        groups = read_results(self.make_csv(rows))
        with self.assertRaisesRegex(ValueError, "missing matched"):
            contrasts(groups, summarize(groups))

    def test_rejects_nonphysical_yield(self):
        with self.assertRaisesRegex(ValueError, "actual cannot exceed"):
            read_results(self.make_csv([self.sample("PROPORTIONAL_DEMAND", "a", 1, 10, 11)]))


if __name__ == "__main__":
    unittest.main()
