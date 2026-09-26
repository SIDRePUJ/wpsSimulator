package org.wpsim.research.water;

import java.util.List;

/** Dependency-free focused tests; run with java -ea after javac. */
public final class SharedWaterSourceTest {
    public static void main(String[] args) {
        close(100.0, WaterUnits.cubicMetres(10.0, 1.0));
        close(10.0, WaterUnits.millimetres(100.0, 1.0));
        expectFailure(() -> WaterUnits.cubicMetres(1.0, 0.0));
        expectFailure(() -> new WaterRequest("x", 1, 10, 1.1));

        List<WaterRequest> farms = List.of(
                new WaterRequest("small", 1, 30, 0.75),
                new WaterRequest("large", 3, 30, 0.75));
        close(400.0, farms.get(0).grossDemandM3());
        for (AllocationRule rule : AllocationRule.values()) {
            SharedWaterSource source = new SharedWaterSource(600.0);
            List<WaterAllocation> allocation = source.allocateRound(farms, rule);
            double used = allocation.stream().mapToDouble(WaterAllocation::grossM3).sum();
            close(600.0, used);
            close(0.0, source.remainingM3());
            for (int i = 0; i < farms.size(); i++) {
                if (allocation.get(i).grossM3() > farms.get(i).grossDemandM3() + 1e-8) {
                    throw new AssertionError("overdelivery");
                }
                close(allocation.get(i).grossM3() * farms.get(i).deliveryEfficiency(),
                        WaterUnits.cubicMetres(allocation.get(i).netMm(), farms.get(i).areaHa()));
            }
        }
        SharedWaterSource proportional = new SharedWaterSource(600.0);
        close(150.0, proportional.allocateRound(farms, AllocationRule.PROPORTIONAL_DEMAND).get(0).grossM3());
        SharedWaterSource priority = new SharedWaterSource(600.0);
        if (priority.allocateRound(farms, AllocationRule.SMALL_PLOT_FLOOR).get(0).grossM3() <= 150.0) {
            throw new AssertionError("small-plot floor had no protective effect");
        }
        SharedWaterSource zero = new SharedWaterSource(0.0);
        close(0.0, zero.allocateRound(farms, AllocationRule.EQUAL_PER_HECTARE).get(0).grossM3());
        SharedWaterSource sufficient = new SharedWaterSource(2000.0);
        sufficient.allocateRound(farms, AllocationRule.PROPORTIONAL_DEMAND);
        close(400.0, sufficient.remainingM3());
        List<WaterRequest> capped = List.of(
                new WaterRequest("low-demand", 1, 1, 1),
                new WaterRequest("high-demand", 1, 30, 1));
        List<WaterAllocation> equal = new SharedWaterSource(100).allocateRound(
                capped, AllocationRule.EQUAL_PER_HECTARE);
        close(10.0, equal.get(0).grossM3());
        close(90.0, equal.get(1).grossM3());
        List<WaterRequest> idleAndActive = List.of(
                new WaterRequest("idle", 0.1, 0, 1),
                new WaterRequest("active", 1, 30, 1));
        List<WaterAllocation> protectedActive = new SharedWaterSource(100).allocateRound(
                idleAndActive, AllocationRule.SMALL_PLOT_FLOOR);
        close(0.0, protectedActive.get(0).grossM3());
        close(100.0, protectedActive.get(1).grossM3());
        expectFailure(() -> new SharedWaterSource(1).allocateRound(
                List.of(farms.get(0), farms.get(0)), AllocationRule.EQUAL_PER_HECTARE));
        System.out.println("SharedWaterSourceTest PASS");
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
