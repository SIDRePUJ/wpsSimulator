package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Research-only rice production ledger; never changes legacy biomass or household income. */
public final class PhysicalYieldLedger {
    private final Map<String, Double> plannedAreas;
    private final double potentialYieldTpha;
    private final double ky;
    private final ConcurrentMap<String, Harvest> harvested = new ConcurrentHashMap<>();

    private record Harvest(String plantingDate, String harvestDate, double actualEtMm, double potentialEtMm,
                           RiceYieldResponse.RiceOutcome outcome) {
    }

    public record Summary(int plannedPlots, int harvestedPlots, int missingHarvests) {
        public boolean valid() {
            return plannedPlots > 0 && missingHarvests == 0;
        }
    }

    public PhysicalYieldLedger(Map<String, Double> plannedAreas, double potentialYieldTpha, double ky) {
        if (plannedAreas == null || plannedAreas.isEmpty()) {
            throw new IllegalArgumentException("At least one planned rice plot is required");
        }
        WaterUnits.requirePositiveFinite(potentialYieldTpha, "potentialYieldTpha");
        WaterUnits.requirePositiveFinite(ky, "ky");
        for (Map.Entry<String, Double> entry : plannedAreas.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new IllegalArgumentException("Planned plot IDs must be nonblank");
            }
            WaterUnits.requirePositiveFinite(entry.getValue(), "planned area");
        }
        this.plannedAreas = Map.copyOf(plannedAreas);
        this.potentialYieldTpha = potentialYieldTpha;
        this.ky = ky;
    }

    public static PhysicalYieldLedger active() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final PhysicalYieldLedger INSTANCE = configured();
    }

    private static PhysicalYieldLedger configured() {
        String output = System.getProperty("wps.water.yieldCsv", "");
        if (output.isBlank()) {
            return null;
        }
        PhysicalIrrigationPlan plan = PhysicalIrrigationPlan.active();
        if (plan == null) {
            throw new IllegalArgumentException("Physical yield requires a physical irrigation plan");
        }
        String ym = System.getProperty("wps.water.potentialYieldTpha");
        String ky = System.getProperty("wps.water.ky");
        if (ym == null || ky == null) {
            throw new IllegalArgumentException("Physical yield requires potentialYieldTpha and ky");
        }
        return new PhysicalYieldLedger(plan.positiveDemandAreas(), Double.parseDouble(ym), Double.parseDouble(ky));
    }

    public boolean isEligible(String plotId) {
        return plannedAreas.containsKey(plotId);
    }

    public void recordHarvest(String plotId, double areaHa, String plantingDate, String harvestDate,
                              double actualEtMm, double potentialEtMm) {
        Double plannedArea = plannedAreas.get(plotId);
        if (plannedArea == null || Math.abs(plannedArea - areaHa) > 1e-9) {
            throw new IllegalArgumentException("Harvest plot or area differs from plan: " + plotId);
        }
        if (plantingDate == null || plantingDate.isBlank() || harvestDate == null || harvestDate.isBlank()) {
            throw new IllegalArgumentException("Planting and harvest dates are required");
        }
        RiceYieldResponse.RiceOutcome outcome = RiceYieldResponse.estimate(
                potentialYieldTpha, actualEtMm, potentialEtMm, ky, areaHa);
        if (harvested.putIfAbsent(plotId, new Harvest(plantingDate, harvestDate,
                actualEtMm, potentialEtMm, outcome)) != null) {
            throw new IllegalStateException("Duplicate physical harvest: " + plotId);
        }
    }

    /** Write one row per planned plot, including missing harvests; never overwrite evidence. */
    public Summary writeCsv(Path output) throws IOException {
        if (output == null) {
            throw new IllegalArgumentException("Yield output path is required");
        }
        StringBuilder csv = new StringBuilder(
                "plot_id,area_ha,planting_date,harvest_date,actual_et_mm,potential_et_mm,actual_t_ha,actual_t,full_t,status\n");
        int missing = 0;
        for (Map.Entry<String, Double> entry : new TreeMap<>(plannedAreas).entrySet()) {
            String plotId = entry.getKey();
            double area = entry.getValue();
            Harvest harvest = harvested.get(plotId);
            csv.append(plotId).append(',').append(area).append(',');
            if (harvest == null) {
                csv.append(",,,,,,,NOT_HARVESTED\n");
                missing++;
            } else {
                csv.append(harvest.plantingDate()).append(',').append(harvest.harvestDate()).append(',')
                        .append(harvest.actualEtMm()).append(',')
                        .append(harvest.potentialEtMm()).append(',')
                        .append(harvest.outcome().tonnesPerHa()).append(',')
                        .append(harvest.outcome().tonnes()).append(',')
                        .append(potentialYieldTpha * area).append(",HARVESTED\n");
            }
        }
        Files.writeString(output, csv.toString(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        return new Summary(plannedAreas.size(), harvested.size(), missing);
    }
}
