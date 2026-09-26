package org.wpsim.research.water;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Finite source allocated once per synchronized request round. */
public final class SharedWaterSource {
    private static final double EPSILON = 1e-9;
    private double remainingM3;

    public SharedWaterSource(double availableM3) {
        WaterUnits.requireNonNegativeFinite(availableM3, "availableM3");
        remainingM3 = availableM3;
    }

    public synchronized double remainingM3() {
        return remainingM3;
    }

    /**
     * The caller must submit the complete set of requests for this round. Calling
     * this method for individual asynchronously arriving farms would introduce
     * arrival-order bias and is not a valid equitable-allocation experiment.
     */
    public synchronized List<WaterAllocation> allocateRound(List<WaterRequest> requests, AllocationRule rule) {
        if (requests == null || rule == null) {
            throw new IllegalArgumentException("requests and rule are required");
        }
        Set<String> seen = new HashSet<>();
        for (WaterRequest request : requests) {
            if (request == null || !seen.add(request.plotId())) {
                throw new IllegalArgumentException("requests must be non-null with unique plot IDs");
            }
        }
        if (requests.isEmpty()) {
            return List.of();
        }

        double[] demand = new double[requests.size()];
        double totalDemand = 0.0;
        for (int i = 0; i < requests.size(); i++) {
            demand[i] = requests.get(i).grossDemandM3();
            totalDemand += demand[i];
        }
        if (!Double.isFinite(totalDemand)) {
            throw new IllegalArgumentException("total demand overflows physical volume range");
        }
        double available = Math.min(remainingM3, totalDemand);
        double[] gross = new double[requests.size()];
        switch (rule) {
            case PROPORTIONAL_DEMAND -> proportional(demand, gross, available);
            case EQUAL_PER_HECTARE -> equalPerHectare(requests, demand, gross, available);
            case SMALL_PLOT_FLOOR -> smallPlotFloor(requests, demand, gross, available);
        }
        List<WaterAllocation> result = new ArrayList<>(requests.size());
        double used = 0.0;
        for (int i = 0; i < requests.size(); i++) {
            WaterRequest request = requests.get(i);
            gross[i] = Math.max(0.0, Math.min(demand[i], gross[i]));
            used += gross[i];
            double netM3 = gross[i] * request.deliveryEfficiency();
            result.add(new WaterAllocation(request.plotId(), gross[i],
                    WaterUnits.millimetres(netM3, request.areaHa())));
        }
        if (used > remainingM3 + EPSILON * Math.max(1.0, remainingM3)) {
            throw new IllegalStateException("water allocation exceeds shared supply");
        }
        remainingM3 = Math.max(0.0, remainingM3 - used);
        return List.copyOf(result);
    }

    private static void proportional(double[] demand, double[] gross, double available) {
        double sum = 0.0;
        for (double value : demand) {
            sum += value;
        }
        if (sum == 0.0) {
            return;
        }
        for (int i = 0; i < gross.length; i++) {
            gross[i] = available * demand[i] / sum;
        }
    }

    private static void equalPerHectare(List<WaterRequest> requests, double[] demand,
                                        double[] gross, double available) {
        double left = available;
        for (int round = 0; round < requests.size() && left > EPSILON; round++) {
            double activeArea = 0.0;
            for (int i = 0; i < requests.size(); i++) {
                if (demand[i] - gross[i] > EPSILON) {
                    activeArea += requests.get(i).areaHa();
                }
            }
            if (activeArea == 0.0) {
                break;
            }
            double spent = 0.0;
            for (int i = 0; i < requests.size(); i++) {
                if (demand[i] - gross[i] > EPSILON) {
                    double grant = Math.min(demand[i] - gross[i], left * requests.get(i).areaHa() / activeArea);
                    gross[i] += grant;
                    spent += grant;
                }
            }
            left = Math.max(0.0, left - spent);
        }
    }

    private static void smallPlotFloor(List<WaterRequest> requests, double[] demand,
                                       double[] gross, double available) {
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            if (demand[i] > 0.0) {
                order.add(i);
            }
        }
        if (order.isEmpty()) {
            return;
        }
        order.sort(Comparator.comparingDouble((Integer i) -> requests.get(i).areaHa())
                .thenComparing(i -> requests.get(i).plotId()));
        int protectedCount = Math.max(1, (order.size() + 3) / 4);
        double[] protectedNeed = new double[demand.length];
        for (int i = 0; i < protectedCount; i++) {
            int index = order.get(i);
            protectedNeed[index] = demand[index] * 0.5;
        }
        proportional(protectedNeed, gross, Math.min(available, sum(protectedNeed)));
        double remainder = Math.max(0.0, available - sum(gross));
        double[] unmet = new double[demand.length];
        double[] supplement = new double[demand.length];
        for (int i = 0; i < demand.length; i++) {
            unmet[i] = demand[i] - gross[i];
        }
        proportional(unmet, supplement, remainder);
        for (int i = 0; i < demand.length; i++) {
            gross[i] += supplement[i];
        }
    }

    private static double sum(double[] values) {
        double total = 0.0;
        for (double value : values) {
            total += value;
        }
        return total;
    }
}
