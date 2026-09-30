# Local IDEAM station-source audit: structural pass, quality unresolved

The original ZIP's four selected station members and the locked station catalog pass a read-only structural audit for the 2019 and 2022 comparison years. This **does not certify meteorological quality**, the ZIP's 07:00 date-label meaning, or spatial representativeness for the irrigation district. All four station branches and both date mappings remain in the locked technical screen.

## Reproduce and interpret

From the repository root, run `python experiments/water_allocation/audit_ideam_station_quality.py`. The standard-library utility reads the original ZIP and catalog without writing or regenerating inputs. It requires exactly one member per selected station, `Fecha|Valor` rows at 07:00, unique dates, finite non-negative values, every day of 2019 and 2022, and 1 January 2020/2023 for the `previous_day` mapping. A failed requirement exits nonzero; descriptive review flags do not exclude a station. The focused synthetic tests are `python -m unittest discover -s experiments/water_allocation -p test_audit_ideam_station_quality.py`.

The source ZIP SHA-256 is `FA695160A154A7CE9C95DEE736535A5A8CD9BBCFAFE5A6AA3DACE13A9BD03E1B`; the catalog SHA-256 is `2AD7C7613CCC2596BD15989E8A141FF0766A5E65BD6E7EB3D2EFBDACC488A7B5`. The values below are interpreted as millimetres according to the locked derived rainfall manifest; the raw two-column members themselves do not carry unit or quality-flag columns.

## Selected-year observations

Each station has 365/365 dates in each selected year and the two required adjacent dates. Across all four selected members, the parser found no duplicate date, malformed/non-07:00 timestamp, negative or non-finite value. Totals and wet days below refer to the **full source year**; the window column is 1 February–11 August on source-year dates. Decimal-place reporting describes the observed positive values, not instrument resolution.

| Station | Year | Total (mm) | Window (mm) | Wet days | Maximum day (mm) | Minimum positive (mm) | Fractional positive days |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| Puerto Santander `29030080` | 2019 | 1,342.2 | 740.2 | 85 | 85.0 | 1.0 | 29 |
| Puerto Santander `29030080` | 2022 | 1,933.5 | 1,103.3 | 141 | 87.8 | 1.0 | 41 |
| Flamenco `29030160` | 2019 | 1,110.0 | 539.0 | 64 | 59.0 | 3.0 | 0 |
| Flamenco `29030160` | 2022 | 1,937.0 | 1,121.0 | 100 | 82.0 | 3.0 | 0 |
| Mampuján `29030780` | 2019 | 1,326.3 | 527.0 | 85 | 58.0 | 2.0 | 1 |
| Mampuján `29030780` | 2022 | 2,496.0 | 1,382.0 | 165 | 32.0 | 2.0 | 0 |
| Nueva Florida `29035040` | 2019 | 1,338.2 | 750.5 | 104 | 79.3 | 0.6 | 97 |
| Nueva Florida `29035040` | 2022 | 2,198.7 | 1,227.9 | 154 | 55.6 | 0.3 | 144 |

## Manual-review flags, not rejection criteria

- Mampuján's raw series begins **8 October 1981**, before the catalog's **15 July 1983** installation date. This provenance inconsistency is real in the locked local files, but does not by itself establish that its selected 2019/2022 observations are wrong.
- Flamenco has integer-only positive values in both selected years. Mampuján has one fractional positive day in 2019 and integer-only positive values in 2022, whereas Puerto Santander and Nueva Florida have many fractional positive days. This difference could reflect reporting conventions or source quality; the ZIP does not identify which.
- In 2022, Mampuján has the most wet days (**165**) and the lowest daily maximum (**32.0 mm**) among the four stations. This cross-station pattern warrants review, not automatic exclusion or pooling.

No official observation-level QC flags, verified date-label convention, or district-scale spatial validation were found in these two-column source members. The audit does not assess historical reservoir inflow, releases, deliveries, or yield response. Preserve both one-day date mappings and all four stations as distinct sensitivity branches; do not select or remove one based on modeled policy outcomes.
