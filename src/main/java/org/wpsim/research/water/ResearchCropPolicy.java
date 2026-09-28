package org.wpsim.research.water;

/** Explicitly fixes the crop portfolio when isolating rice-water allocation effects. */
public final class ResearchCropPolicy {
    private ResearchCropPolicy() {
    }

    public static boolean riceOnlyCohort() {
        return Boolean.parseBoolean(System.getProperty("wps.water.riceOnlyCohort", "false"));
    }

    public static void validate(boolean physicalWaterEnabled) {
        if (riceOnlyCohort() && !physicalWaterEnabled) {
            throw new IllegalArgumentException("Rice-only cohort requires physical water mode");
        }
    }

    public static String select(String marketChoice, boolean physicalWaterEnabled, boolean riceOnly) {
        if (riceOnly && !physicalWaterEnabled) {
            throw new IllegalArgumentException("Rice-only cohort requires physical water mode");
        }
        if (marketChoice == null || marketChoice.isBlank()) {
            throw new IllegalArgumentException("Market crop choice is required");
        }
        return riceOnly ? "rice" : marketChoice;
    }
}
