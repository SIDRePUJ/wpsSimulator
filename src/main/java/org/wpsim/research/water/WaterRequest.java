package org.wpsim.research.water;

/** A plot's net irrigation need, before application and conveyance losses. */
public record WaterRequest(String plotId, double areaHa, double netDemandMm, double deliveryEfficiency) {
    public WaterRequest {
        if (plotId == null || plotId.isBlank()) {
            throw new IllegalArgumentException("plotId must be present");
        }
        WaterUnits.requirePositiveFinite(areaHa, "areaHa");
        WaterUnits.requireNonNegativeFinite(netDemandMm, "netDemandMm");
        WaterUnits.requirePositiveFinite(deliveryEfficiency, "deliveryEfficiency");
        if (deliveryEfficiency > 1.0) {
            throw new IllegalArgumentException("deliveryEfficiency cannot exceed one");
        }
    }

    public double grossDemandM3() {
        return WaterUnits.cubicMetres(netDemandMm, areaHa) / deliveryEfficiency;
    }
}
