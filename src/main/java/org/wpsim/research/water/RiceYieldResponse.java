package org.wpsim.research.water;

/**
 * FAO-33 seasonal relative-yield response: 1 - Ya/Ym = Ky (1 - ETa/ETm).
 * This is a transparent research approximation, not the legacy biomass model.
 */
public final class RiceYieldResponse {
    private RiceYieldResponse() {
    }

    public static RiceOutcome estimate(double potentialYieldTonnesPerHa,
                                       double actualEtMm,
                                       double potentialEtMm,
                                       double responseFactorKy,
                                       double harvestedAreaHa) {
        WaterUnits.requirePositiveFinite(potentialYieldTonnesPerHa, "potentialYieldTonnesPerHa");
        WaterUnits.requireNonNegativeFinite(actualEtMm, "actualEtMm");
        WaterUnits.requirePositiveFinite(potentialEtMm, "potentialEtMm");
        WaterUnits.requirePositiveFinite(responseFactorKy, "responseFactorKy");
        WaterUnits.requirePositiveFinite(harvestedAreaHa, "harvestedAreaHa");
        if (actualEtMm > potentialEtMm) {
            throw new IllegalArgumentException("actualEtMm cannot exceed potentialEtMm");
        }
        double relativeYield = Math.max(0.0,
                1.0 - responseFactorKy * (1.0 - actualEtMm / potentialEtMm));
        double tonnesPerHa = potentialYieldTonnesPerHa * relativeYield;
        double tonnes = tonnesPerHa * harvestedAreaHa;
        if (!Double.isFinite(tonnes)) {
            throw new IllegalArgumentException("yield calculation overflows physical range");
        }
        return new RiceOutcome(tonnesPerHa, tonnes, relativeYield);
    }

    public record RiceOutcome(double tonnesPerHa, double tonnes, double relativeYield) {
    }
}
