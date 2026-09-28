import csv
import json
from pathlib import Path
import tempfile
import unittest

from prepare_twelve_upa_world import DESTINATION, SOURCE, select_cells


class TwelveUpaWorldTest(unittest.TestCase):
    def test_committed_world_is_exact_source_subset_and_twelve_pairs(self):
        expected = select_cells(SOURCE)
        committed = json.loads(DESTINATION.read_text(encoding="utf-8"))
        self.assertEqual(expected, committed)
        self.assertEqual(24, len(committed))
        self.assertEqual(12, len({cell["name"].split("_")[1] for cell in committed}))
        for y in range(2):
            row = {cell["name"] for cell in committed if cell["name"].endswith(f"_{y}")}
            self.assertEqual({f"land_{x}_{y}" for x in range(12)}, row)
        pairs = [(f"land_{x}_{y}", f"land_{x + 1}_{y}")
                 for y in range(2) for x in range(0, 12, 2)]
        self.assertEqual(12, len(pairs))
        self.assertEqual({cell["name"] for cell in committed}, {name for pair in pairs for name in pair})

    def test_manifest_matches_two_plot_area_classes(self):
        manifest = Path(__file__).with_name("twelve_upa_manifest.csv")
        with manifest.open(newline="", encoding="utf-8") as file:
            rows = list(csv.DictReader(file))
        self.assertEqual(12, len(rows))
        self.assertEqual([f"MAS_PeasantFamily{i}" for i in range(1, 13)],
                         [row["family_alias"] for row in rows])
        self.assertEqual([f"farm_{i}_small" for i in range(1, 13)],
                         [row["farm_name"] for row in rows])
        self.assertEqual([2] * 4 + [8] * 5 + [16] * 3,
                         [2 * int(row["crop_area_ha_per_plot"]) for row in rows])

    def test_missing_cell_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            source = Path(directory) / "world.json"
            source.write_text(json.dumps(select_cells(SOURCE)[:-1]), encoding="utf-8")
            with self.assertRaisesRegex(ValueError, "missing or invalid"):
                select_cells(source)


if __name__ == "__main__":
    unittest.main()
