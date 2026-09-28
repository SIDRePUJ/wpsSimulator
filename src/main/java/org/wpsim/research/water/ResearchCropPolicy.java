package org.wpsim.research.water;

/** Explicitly fixes the crop portfolio when isolating rice-water allocation effects. */
public final class ResearchCropPolicy {
    private ResearchCropPolicy() {
    }

    public static boolean riceOnlyCohort() {
        return Boolean.parseBoolean(System.getProperty("wps.water.riceOnlyCohort", "false"));
    }

    public static boolean districtRiceCalendar() {
        return Boolean.parseBoolean(System.getProperty("wps.water.districtRiceCalendar", "false"));
    }

    public static void validate(boolean physicalWaterEnabled) {
        if (riceOnlyCohort() && !physicalWaterEnabled) {
            throw new IllegalArgumentException("Rice-only cohort requires physical water mode");
        }
        if (districtRiceCalendar() && (!physicalWaterEnabled || !riceOnlyCohort())) {
            throw new IllegalArgumentException("District rice calendar requires physical water and rice-only cohort modes");
        }
    }

    /** LandInfo starts at version 1; later versions retain the simulator's technical seasons. */
    public static boolean mayPrepare(int zeroBasedMonth, int landVersion) {
        if (!districtRiceCalendar()) {
            return zeroBasedMonth == 0 || zeroBasedMonth == 3 || zeroBasedMonth == 6 || zeroBasedMonth == 8;
        }
        return landVersion == 1 ? zeroBasedMonth >= 0 && zeroBasedMonth <= 2
                : zeroBasedMonth == 0 || zeroBasedMonth == 3 || zeroBasedMonth == 6 || zeroBasedMonth == 8;
    }

    /** The crop-start guard also covers land prepared late in March. */
    public static boolean mayPlant(int zeroBasedMonth, int landVersion) {
        return !districtRiceCalendar() || landVersion != 1
                || zeroBasedMonth >= 0 && zeroBasedMonth <= 2;
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
