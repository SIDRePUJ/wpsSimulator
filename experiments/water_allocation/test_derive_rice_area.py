"""Synthetic checks for privacy-safe CNA rice-area aggregation."""

import json
from pathlib import Path
import tempfile
import unittest
import zipfile

from derive_rice_area import derive, summarize


class RiceAreaDerivationTest(unittest.TestCase):
    def test_summarize_bins_aggregated_upa_area(self):
        result = summarize({("a",): 5.0, ("b",): 6.0, ("c",): 11.0})
        self.assertEqual(result["upa_count"], 3)
        self.assertEqual(result["positive_harvested_area_ha"], 22.0)
        self.assertEqual(list(result["upa_count_by_harvested_area_ha"].values()), [1, 1, 1])

    def test_derive_excludes_zero_and_exports_no_identifiers(self):
        with tempfile.TemporaryDirectory() as temporary:
            archive = Path(temporary) / "synthetic.zip"
            upa = "P_MUNIC,UC_UO,ENCUESTA,COD_VEREDA,P_S11P124_SP10\n13442,secret,1,1,1\n13442,other,2,1,0\n"
            crops = "P_MUNIC,UC_UO,ENCUESTA,COD_VEREDA,P_S6P46,AREA_COSECHADA\n13442,secret,1,1,00113202001,2\n13442,secret,1,1,00113202001,4\n13442,other,2,1,00113202001,0\n"
            with zipfile.ZipFile(archive, "w") as bundle:
                bundle.writestr("S01_15_Unidad_productora_.csv", upa)
                bundle.writestr("S06A_Cultivos_.csv", crops)
            result = derive(archive)
            self.assertEqual(result["counts"]["rice_crop_rows"], 3)
            self.assertEqual(result["positive_area_rice_upa_reporting_district_source"]["upa_count"], 1)
            self.assertEqual(result["positive_area_rice_upa_reporting_district_source"]["positive_harvested_area_ha"], 6)
            self.assertNotIn("secret", json.dumps(result))


if __name__ == "__main__":
    unittest.main()
