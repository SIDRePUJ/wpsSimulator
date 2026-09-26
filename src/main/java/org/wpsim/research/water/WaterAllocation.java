package org.wpsim.research.water;

/** Gross source withdrawal and net depth reaching one plot. */
public record WaterAllocation(String plotId, double grossM3, double netMm) {
    public WaterAllocation {
        if (plotId == null || plotId.isBlank()) {
            throw new IllegalArgumentException("plotId must be present");
        }
        WaterUnits.requireNonNegativeFinite(grossM3, "grossM3");
        WaterUnits.requireNonNegativeFinite(netMm, "netMm");
    }
}
