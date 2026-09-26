package org.wpsim.research.water;

/** Physical conversions for irrigation depth and water volume. */
public final class WaterUnits {
    private WaterUnits() {
    }

    /** One millimetre over one hectare is ten cubic metres. */
    public static double cubicMetres(double depthMm, double areaHa) {
        requireNonNegativeFinite(depthMm, "depthMm");
        requirePositiveFinite(areaHa, "areaHa");
        return depthMm * areaHa * 10.0;
    }

    public static double millimetres(double volumeM3, double areaHa) {
        requireNonNegativeFinite(volumeM3, "volumeM3");
        requirePositiveFinite(areaHa, "areaHa");
        return volumeM3 / (areaHa * 10.0);
    }

    static void requireNonNegativeFinite(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0) {
            throw new IllegalArgumentException(name + " must be finite and non-negative");
        }
    }

    static void requirePositiveFinite(double value, String name) {
        if (!Double.isFinite(value) || value <= 0.0) {
            throw new IllegalArgumentException(name + " must be finite and positive");
        }
    }
}
