package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Optional, non-overwriting record of the daily forcing consumed by eligible rice plots. */
public final class PhysicalClimateLedger {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private final Set<String> eligiblePlots;
    private final ConcurrentMap<Key, Forcing> days = new ConcurrentHashMap<>();
    private final AtomicInteger duplicateDays = new AtomicInteger();

    private record Key(String plotId, LocalDate date) {
    }

    private record Forcing(double rainMm, double referenceEtMm, double temperatureC,
                           double shortWaveRadiation) {
    }

    public record Summary(int plannedPlots, int observedPlots, int dailyRows,
                          int missingPlots, int gapDays, int duplicateDays) {
        public boolean valid() {
            return plannedPlots > 0 && missingPlots == 0 && gapDays == 0 && duplicateDays == 0;
        }
    }

    public PhysicalClimateLedger(Set<String> eligiblePlots) {
        if (eligiblePlots == null || eligiblePlots.isEmpty()
                || eligiblePlots.stream().anyMatch(plot -> plot == null || plot.isBlank())) {
            throw new IllegalArgumentException("Climate audit requires eligible plot IDs");
        }
        this.eligiblePlots = Set.copyOf(eligiblePlots);
    }

    public static PhysicalClimateLedger active() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final PhysicalClimateLedger INSTANCE = configured();
    }

    private static PhysicalClimateLedger configured() {
        if (System.getProperty("wps.water.climateCsv", "").isBlank()) {
            return null;
        }
        PhysicalIrrigationPlan plan = PhysicalIrrigationPlan.active();
        if (plan == null) {
            throw new IllegalArgumentException("Climate audit requires a physical irrigation plan");
        }
        return new PhysicalClimateLedger(plan.positiveDemandAreas().keySet());
    }

    public boolean isEligible(String plotId) {
        return plotId != null && eligiblePlots.contains(plotId);
    }

    public void record(String plotId, String date, double rainMm, double referenceEtMm,
                       double temperatureC, double shortWaveRadiation) {
        if (!isEligible(plotId)) {
            throw new IllegalArgumentException("Climate plot is absent from eligible cohort: " + plotId);
        }
        LocalDate day = LocalDate.parse(date, DATE);
        if (!Double.isFinite(rainMm) || !Double.isFinite(referenceEtMm)
                || !Double.isFinite(temperatureC) || !Double.isFinite(shortWaveRadiation)) {
            throw new IllegalArgumentException("Non-finite climate forcing: " + plotId + " " + date);
        }
        if (days.putIfAbsent(new Key(plotId, day),
                new Forcing(rainMm, referenceEtMm, temperatureC, shortWaveRadiation)) != null) {
            duplicateDays.incrementAndGet();
        }
    }

    public Summary writeCsv(Path output) throws IOException {
        if (output == null) {
            throw new IllegalArgumentException("Climate output path is required");
        }
        Map<String, TreeMap<LocalDate, Forcing>> byPlot = new TreeMap<>();
        for (String plotId : eligiblePlots) {
            byPlot.put(plotId, new TreeMap<>());
        }
        days.forEach((key, forcing) -> byPlot.get(key.plotId()).put(key.date(), forcing));
        StringBuilder csv = new StringBuilder(
                "plot_id,date,rain_mm,reference_et_mm,temperature_c,short_wave_radiation\n");
        int missing = 0;
        int gaps = 0;
        for (Map.Entry<String, TreeMap<LocalDate, Forcing>> plot : byPlot.entrySet()) {
            LocalDate previous = null;
            if (plot.getValue().isEmpty()) {
                missing++;
            }
            for (Map.Entry<LocalDate, Forcing> day : plot.getValue().entrySet()) {
                if (previous != null) {
                    gaps += (int) Math.max(0, ChronoUnit.DAYS.between(previous, day.getKey()) - 1);
                }
                Forcing forcing = day.getValue();
                csv.append(plot.getKey()).append(',').append(DATE.format(day.getKey())).append(',')
                        .append(forcing.rainMm()).append(',').append(forcing.referenceEtMm()).append(',')
                        .append(forcing.temperatureC()).append(',').append(forcing.shortWaveRadiation())
                        .append('\n');
                previous = day.getKey();
            }
        }
        Files.writeString(output, csv, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        return new Summary(eligiblePlots.size(), eligiblePlots.size() - missing, days.size(),
                missing, gaps, duplicateDays.get());
    }
}
