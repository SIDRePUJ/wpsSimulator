package org.wpsim.research.water;

/** Dependency-free opt-in cohort-choice checks. */
public final class ResearchCropPolicyTest {
    public static void main(String[] args) {
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
        System.out.println("ResearchCropPolicyTest PASS");
    }

    private static void equal(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }
}
