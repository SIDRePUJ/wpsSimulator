package org.wpsim.research.water;

/** Dependency-free opt-in cohort-choice checks. */
public final class ResearchCropPolicyTest {
    public static void main(String[] args) {
        System.clearProperty("wps.water.districtRiceCalendar");
        System.clearProperty("wps.water.riceOnlyCohort");
        equal("roots", ResearchCropPolicy.select("roots", false, false));
        equal("roots", ResearchCropPolicy.select("roots", true, false));
        equal("rice", ResearchCropPolicy.select("roots", true, true));
        equal("rice", ResearchCropPolicy.select("rice", true, true));
        try {
            ResearchCropPolicy.select("roots", false, true);
            throw new AssertionError("rice-only cohort must not alter legacy mode");
        } catch (IllegalArgumentException expected) {
            // Expected invalid configuration.
        }
        for (int month = 0; month < 12; month++) {
            boolean legacy = month == 0 || month == 3 || month == 6 || month == 8;
            equal(legacy, ResearchCropPolicy.mayPrepare(month, 1));
            equal(true, ResearchCropPolicy.mayPlant(month, 1));
        }
        System.setProperty("wps.water.districtRiceCalendar", "true");
        try {
            rejectsInvalidCalendar();
            System.setProperty("wps.water.riceOnlyCohort", "true");
            ResearchCropPolicy.validate(true);
            for (int month = 0; month < 12; month++) {
                boolean first = month >= 0 && month <= 2;
                boolean later = month == 0 || month == 3 || month == 6 || month == 8;
                equal(first, ResearchCropPolicy.mayPrepare(month, 1));
                equal(first, ResearchCropPolicy.mayPlant(month, 1));
                equal(later, ResearchCropPolicy.mayPrepare(month, 2));
                equal(true, ResearchCropPolicy.mayPlant(month, 2));
            }
        } finally {
            System.clearProperty("wps.water.districtRiceCalendar");
            System.clearProperty("wps.water.riceOnlyCohort");
        }
        System.out.println("ResearchCropPolicyTest PASS");
    }

    private static void rejectsInvalidCalendar() {
        try {
            ResearchCropPolicy.validate(true);
            throw new AssertionError("district calendar requires rice-only cohort");
        } catch (IllegalArgumentException expected) {
            // Expected invalid configuration.
        }
        System.setProperty("wps.water.riceOnlyCohort", "true");
        try {
            ResearchCropPolicy.validate(false);
            throw new AssertionError("district calendar requires physical water mode");
        } catch (IllegalArgumentException expected) {
            // Expected invalid configuration.
        } finally {
            System.clearProperty("wps.water.riceOnlyCohort");
        }
    }

    private static void equal(boolean expected, boolean actual) {
        if (expected != actual) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }

    private static void equal(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }
}
