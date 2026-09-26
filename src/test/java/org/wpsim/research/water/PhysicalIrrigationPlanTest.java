package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Dependency-free complete-round and chronology checks. */
public final class PhysicalIrrigationPlanTest {
    public static void main(String[] args) throws IOException {
        Path requests = Files.createTempFile("water-requests-", ".csv");
        try {
            Files.writeString(requests, "date,plot_id,area_ha,net_demand_mm,delivery_efficiency\n"
                    + "02/01/2020,small,1,20,1\n"
                    + "01/01/2020,small,1,20,1\n"
                    + "01/01/2020,large,2,20,1\n");
            PhysicalIrrigationPlan plan = PhysicalIrrigationPlan.load(
                    requests, 300, AllocationRule.PROPORTIONAL_DEMAND);
            close(10, plan.netMm("01/01/2020", "small"));
            close(10, plan.netMm("01/01/2020", "large"));
            close(0, plan.netMm("02/01/2020", "small"));
            close(0, plan.remainingM3());
            close(0, plan.netMm("03/01/2020", "small"));
            plan.verifyPlotArea("small", 1);
            expectFailure(() -> plan.verifyPlotArea("small", 2));
            expectFailure(() -> plan.verifyPlotArea("absent", 1));
            expectFailure(() -> loadUnchecked(requests, -1));
            Files.writeString(requests, "date,plot_id,area_ha,net_demand_mm,delivery_efficiency\n"
                    + "01/01/2020,small,1,20,1\n"
                    + "01/01/2020,small,1,20,1\n");
            expectFailure(() -> loadUnchecked(requests, 300));
        } finally {
            Files.deleteIfExists(requests);
        }
        System.out.println("PhysicalIrrigationPlanTest PASS");
    }

    private static void loadUnchecked(Path file, double sourceM3) {
        try {
            PhysicalIrrigationPlan.load(file, sourceM3, AllocationRule.PROPORTIONAL_DEMAND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void close(double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-8) {
            throw new AssertionError("expected " + expected + ", actual " + actual);
        }
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected IllegalArgumentException");
    }
}
