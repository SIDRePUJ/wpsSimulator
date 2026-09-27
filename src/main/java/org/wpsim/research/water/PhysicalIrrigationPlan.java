package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;

/**
 * Exogenous, complete irrigation request rounds allocated before agents run.
 * This avoids arrival-order bias from asynchronous farm decisions.
 */
public final class PhysicalIrrigationPlan {
    public enum AllocationHorizon {
        ROUND_CHRONOLOGICAL,
        SEASONAL_ENTITLEMENT
    }

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private final Map<LocalDate, Map<String, WaterAllocation>> allocations;
    private final Map<String, Double> areaHaByPlot;
    private final Map<String, Double> positiveDemandAreas;
    private final double initialM3;
    private final double remainingM3;
    private final ConcurrentMap<String, Double> registeredPlots = new ConcurrentHashMap<>();
    private final ConcurrentMap<DeliveryKey, Double> appliedMm = new ConcurrentHashMap<>();
    private final Set<String> failedPlotRegistrations = ConcurrentHashMap.newKeySet();

    private record DeliveryKey(LocalDate date, String plotId) {
    }

    public record AuditSummary(int plannedPlots, int registeredPlots, int absentPlots, int failedPlotRegistrations,
                               int missingDeliveries, int appliedDeliveries) {
        public boolean valid() {
            return absentPlots == 0 && failedPlotRegistrations == 0 && missingDeliveries == 0;
        }
    }

    private PhysicalIrrigationPlan(Map<LocalDate, Map<String, WaterAllocation>> allocations,
                                   Map<String, Double> areaHaByPlot, Map<String, Double> positiveDemandAreas,
                                   double initialM3, double remainingM3) {
        this.allocations = Map.copyOf(allocations);
        this.areaHaByPlot = Map.copyOf(areaHaByPlot);
        this.positiveDemandAreas = Map.copyOf(positiveDemandAreas);
        this.initialM3 = initialM3;
        this.remainingM3 = remainingM3;
    }

    public static boolean enabled() {
        String file = System.getProperty("wps.water.requests");
        return file != null && !file.isBlank();
    }

    /** Returns null unless the research mode was explicitly configured. */
    public static PhysicalIrrigationPlan active() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final PhysicalIrrigationPlan INSTANCE = configured();
    }

    private static PhysicalIrrigationPlan configured() {
        if (!enabled()) {
            return null;
        }
        String stock = System.getProperty("wps.water.sourceM3");
        String selectedRule = System.getProperty("wps.water.rule");
        if (stock == null || selectedRule == null) {
            throw new IllegalArgumentException("Research irrigation requires wps.water.sourceM3 and wps.water.rule");
        }
        try {
            AllocationHorizon horizon = AllocationHorizon.valueOf(
                    System.getProperty("wps.water.horizon", "ROUND_CHRONOLOGICAL"));
            PhysicalIrrigationPlan plan = load(Path.of(System.getProperty("wps.water.requests")),
                    Double.parseDouble(stock), AllocationRule.valueOf(selectedRule), horizon);
            System.out.println("PHYSICAL_WATER_HORIZON: " + horizon);
            return plan;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read research irrigation requests", e);
        }
    }

    /** CSV columns: date,plot_id,area_ha,net_demand_mm,delivery_efficiency. */
    public static PhysicalIrrigationPlan load(Path file, double sourceM3, AllocationRule rule) throws IOException {
        return load(file, sourceM3, rule, AllocationHorizon.ROUND_CHRONOLOGICAL);
    }

    public static PhysicalIrrigationPlan load(Path file, double sourceM3, AllocationRule rule,
                                              AllocationHorizon horizon) throws IOException {
        WaterUnits.requireNonNegativeFinite(sourceM3, "sourceM3");
        if (file == null || rule == null || horizon == null) {
            throw new IllegalArgumentException("file, rule and horizon are required");
        }
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty() || !lines.get(0).replace("\uFEFF", "").equals(
                "date,plot_id,area_ha,net_demand_mm,delivery_efficiency")) {
            throw new IllegalArgumentException("Unexpected irrigation request CSV header");
        }
        Map<LocalDate, List<WaterRequest>> rounds = new TreeMap<>();
        Map<String, Double> areas = new HashMap<>();
        Map<String, Double> positiveAreas = new HashMap<>();
        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            if (line.isBlank()) {
                continue;
            }
            String[] columns = line.split(",", -1);
            if (columns.length != 5) {
                throw new IllegalArgumentException("Expected five columns at line " + (lineNumber + 1));
            }
            try {
                LocalDate date = LocalDate.parse(columns[0].trim(), DATE);
                WaterRequest request = new WaterRequest(columns[1].trim(),
                        Double.parseDouble(columns[2].trim()), Double.parseDouble(columns[3].trim()),
                        Double.parseDouble(columns[4].trim()));
                Double previousArea = areas.putIfAbsent(request.plotId(), request.areaHa());
                if (previousArea != null && Double.compare(previousArea, request.areaHa()) != 0) {
                    throw new IllegalArgumentException("Plot area changes across rounds: " + request.plotId());
                }
                if (request.netDemandMm() > 0.0) {
                    positiveAreas.put(request.plotId(), request.areaHa());
                }
                rounds.computeIfAbsent(date, ignored -> new ArrayList<>()).add(request);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Invalid irrigation request at line " + (lineNumber + 1), e);
            }
        }
        SharedWaterSource source = new SharedWaterSource(sourceM3);
        Map<LocalDate, Map<String, WaterAllocation>> allocated = horizon == AllocationHorizon.SEASONAL_ENTITLEMENT
                ? allocateSeasonally(rounds, source, rule) : allocateChronologically(rounds, source, rule);
        return new PhysicalIrrigationPlan(allocated, areas, positiveAreas, sourceM3, source.remainingM3());
    }

    private static Map<LocalDate, Map<String, WaterAllocation>> allocateChronologically(
            Map<LocalDate, List<WaterRequest>> rounds, SharedWaterSource source, AllocationRule rule) {
        Map<LocalDate, Map<String, WaterAllocation>> allocated = new HashMap<>();
        for (Map.Entry<LocalDate, List<WaterRequest>> round : rounds.entrySet()) {
            Map<String, WaterAllocation> plots = new HashMap<>();
            for (WaterAllocation allocation : source.allocateRound(round.getValue(), rule)) {
                plots.put(allocation.plotId(), allocation);
            }
            allocated.put(round.getKey(), Map.copyOf(plots));
        }
        return allocated;
    }

    /** Reserve seasonal plot entitlements before spreading them across dated requests. */
    private static Map<LocalDate, Map<String, WaterAllocation>> allocateSeasonally(
            Map<LocalDate, List<WaterRequest>> rounds, SharedWaterSource source, AllocationRule rule) {
        Map<String, Double> areaByPlot = new TreeMap<>();
        Map<String, Double> efficiencyByPlot = new HashMap<>();
        Map<String, Double> totalDepthByPlot = new HashMap<>();
        for (List<WaterRequest> requests : rounds.values()) {
            Set<String> seenToday = new java.util.HashSet<>();
            for (WaterRequest request : requests) {
                if (!seenToday.add(request.plotId())) {
                    throw new IllegalArgumentException("Duplicate plot request in one day: " + request.plotId());
                }
                Double oldArea = areaByPlot.putIfAbsent(request.plotId(), request.areaHa());
                Double oldEfficiency = efficiencyByPlot.putIfAbsent(request.plotId(), request.deliveryEfficiency());
                if ((oldArea != null && Double.compare(oldArea, request.areaHa()) != 0)
                        || (oldEfficiency != null
                        && Double.compare(oldEfficiency, request.deliveryEfficiency()) != 0)) {
                    throw new IllegalArgumentException("Seasonal plot area/efficiency changes: " + request.plotId());
                }
                totalDepthByPlot.merge(request.plotId(), request.netDemandMm(), Double::sum);
            }
        }
        List<WaterRequest> seasonal = new ArrayList<>();
        for (Map.Entry<String, Double> entry : areaByPlot.entrySet()) {
            String plot = entry.getKey();
            seasonal.add(new WaterRequest(plot, entry.getValue(), totalDepthByPlot.get(plot),
                    efficiencyByPlot.get(plot)));
        }
        Map<String, Double> seasonalGrossByPlot = new HashMap<>();
        for (WaterAllocation allocation : source.allocateRound(seasonal, rule)) {
            seasonalGrossByPlot.put(allocation.plotId(), allocation.grossM3());
        }
        Map<LocalDate, Map<String, WaterAllocation>> allocated = new HashMap<>();
        for (Map.Entry<LocalDate, List<WaterRequest>> round : rounds.entrySet()) {
            Map<String, WaterAllocation> plots = new HashMap<>();
            for (WaterRequest request : round.getValue()) {
                double totalGross = new WaterRequest(request.plotId(), request.areaHa(),
                        totalDepthByPlot.get(request.plotId()), request.deliveryEfficiency()).grossDemandM3();
                double fraction = totalGross == 0.0 ? 0.0
                        : seasonalGrossByPlot.get(request.plotId()) / totalGross;
                double gross = request.grossDemandM3() * fraction;
                plots.put(request.plotId(), new WaterAllocation(request.plotId(), gross,
                        request.netDemandMm() * fraction));
            }
            allocated.put(round.getKey(), Map.copyOf(plots));
        }
        return allocated;
    }

    public double netMm(String date, String plotId) {
        LocalDate day = LocalDate.parse(date, DATE);
        WaterAllocation allocation = allocations.getOrDefault(day, Map.of()).get(plotId);
        return allocation == null ? 0.0 : allocation.netMm();
    }

    public void verifyPlotArea(String plotId, double actualAreaHa) {
        Double planned = areaHaByPlot.get(plotId);
        if (planned == null) {
            throw new IllegalArgumentException("Rice plot is absent from irrigation plan: " + plotId);
        }
        if (Math.abs(planned - actualAreaHa) > 1e-9) {
            throw new IllegalArgumentException("Plot area disagrees with irrigation plan: " + plotId);
        }
    }

    /** Register the actual crop world, not merely a planned CSV row. */
    public void registerPlot(String plotId, double actualAreaHa) {
        verifyPlotArea(plotId, actualAreaHa);
        if (registeredPlots.putIfAbsent(plotId, actualAreaHa) != null) {
            throw new IllegalStateException("Duplicate crop world for plot: " + plotId);
        }
    }

    /** Retain worker-thread planting failures until the main shutdown audit. */
    public void recordRegistrationFailure(String plotId) {
        failedPlotRegistrations.add(plotId);
    }

    /** Record a delivery only after the crop water-balance update succeeds. */
    public void recordApplied(String date, String plotId, double netMm) {
        LocalDate day = LocalDate.parse(date, DATE);
        if (!registeredPlots.containsKey(plotId)) {
            throw new IllegalStateException("Unregistered crop world: " + plotId);
        }
        WaterAllocation planned = allocations.getOrDefault(day, Map.of()).get(plotId);
        if (planned == null || planned.netMm() <= 0.0 || Math.abs(planned.netMm() - netMm) > 1e-8) {
            throw new IllegalStateException("Applied depth differs from allocation: " + plotId + " on " + date);
        }
        if (appliedMm.putIfAbsent(new DeliveryKey(day, plotId), netMm) != null) {
            throw new IllegalStateException("Duplicate irrigation delivery: " + plotId + " on " + date);
        }
    }

    /** Create a one-run, plot-level reconciliation file; never overwrite prior evidence. */
    public AuditSummary writeAudit(Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("audit file is required");
        }
        StringBuilder csv = new StringBuilder("date,plot_id,area_ha,gross_m3,net_mm,applied_net_mm,status\n");
        int missing = 0;
        int applied = 0;
        for (LocalDate date : new TreeMap<>(allocations).keySet()) {
            for (String plotId : allocations.get(date).keySet().stream().sorted().toList()) {
                WaterAllocation allocation = allocations.get(date).get(plotId);
                Double delivered = appliedMm.get(new DeliveryKey(date, plotId));
                String status;
                if (!registeredPlots.containsKey(plotId)) {
                    status = "PLOT_ABSENT";
                } else if (allocation.netMm() > 0.0 && delivered == null) {
                    status = "NOT_APPLIED";
                    missing++;
                } else if (delivered != null) {
                    status = "APPLIED";
                    applied++;
                } else {
                    status = "NO_DELIVERY";
                }
                csv.append(date.format(DATE)).append(',').append(plotId).append(',')
                        .append(areaHaByPlot.get(plotId)).append(',').append(allocation.grossM3()).append(',')
                        .append(allocation.netMm()).append(',')
                        .append(delivered == null ? "" : delivered).append(',').append(status).append('\n');
            }
        }
        for (String plotId : failedPlotRegistrations.stream().sorted().toList()) {
            csv.append(',').append(plotId).append(",,,,,PLOT_REGISTRATION_FAILED\n");
        }
        Files.writeString(file, csv.toString(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        int absentPlotCount = (int) areaHaByPlot.keySet().stream()
                .filter(plotId -> !registeredPlots.containsKey(plotId)).count();
        return new AuditSummary(areaHaByPlot.size(), registeredPlots.size(), absentPlotCount,
                failedPlotRegistrations.size(), missing, applied);
    }

    public double initialM3() {
        return initialM3;
    }

    public double remainingM3() {
        return remainingM3;
    }

    public int plannedPlotCount() {
        return areaHaByPlot.size();
    }

    public Map<String, Double> plannedAreas() {
        return areaHaByPlot;
    }

    /** Plots exposed to the rationing rule; zero-demand registration rows are excluded. */
    public Map<String, Double> positiveDemandAreas() {
        return positiveDemandAreas;
    }
}
