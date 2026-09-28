"""Derive a small synthetic study world from the committed 100-column grid."""

import argparse
import json
from pathlib import Path


SOURCE = Path(__file__).resolve().parents[2] / "src/main/resources/web/data/world.100.json"
DESTINATION = Path(__file__).resolve().parents[2] / "src/main/resources/web/data/world.24.json"


def select_cells(source: Path) -> list[dict]:
    cells = json.loads(source.read_text(encoding="utf-8"))
    by_name = {cell["name"]: cell for cell in cells}
    if len(by_name) != len(cells):
        raise ValueError("source world contains duplicate cell names")
    names = [f"land_{x}_{y}" for y in range(2) for x in range(12)]
    selected = []
    for name in names:
        cell = by_name.get(name)
        if cell is None or cell.get("kind") != "land" or len(cell.get("coordinates", [])) != 4:
            raise ValueError(f"missing or invalid source land cell: {name}")
        selected.append(cell)
    return selected


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=SOURCE)
    parser.add_argument("--output", type=Path, default=DESTINATION)
    args = parser.parse_args()
    args.output.write_text(json.dumps(select_cells(args.source), indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
