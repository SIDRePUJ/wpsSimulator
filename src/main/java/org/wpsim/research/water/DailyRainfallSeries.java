package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Complete, dated rainfall forcing for the opt-in physical irrigation experiment. */
public final class DailyRainfallSeries {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private final Map<LocalDate, Double> rainByDay;

    private DailyRainfallSeries(Map<LocalDate, Double> rainByDay) {
        this.rainByDay = Map.copyOf(rainByDay);
    }

    public static DailyRainfallSeries active() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final DailyRainfallSeries INSTANCE = configured();
    }

    private static DailyRainfallSeries configured() {
        String input = System.getProperty("wps.water.dailyRainCsv", "");
        if (input.isBlank()) {
            return null;
        }
        if (!PhysicalIrrigationPlan.enabled()) {
            throw new IllegalArgumentException("Daily rainfall forcing requires physical irrigation mode");
        }
        try {
            return fromCsv(Path.of(input));
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read daily rainfall forcing: " + input, e);
        }
    }

    public static DailyRainfallSeries fromCsv(Path input) throws IOException {
        List<String> lines = Files.readAllLines(input);
        if (lines.size() < 2 || !"date,rain_mm".equals(lines.get(0).trim())) {
            throw new IllegalArgumentException("Daily rainfall CSV must start with date,rain_mm");
        }
        TreeMap<LocalDate, Double> days = new TreeMap<>();
        for (int index = 1; index < lines.size(); index++) {
            String[] fields = lines.get(index).split(",", -1);
            if (fields.length != 2) {
                throw new IllegalArgumentException("Invalid daily rainfall row " + (index + 1));
            }
            LocalDate date = LocalDate.parse(fields[0].trim(), DATE);
            double rain = Double.parseDouble(fields[1].trim());
            if (!Double.isFinite(rain) || rain < 0) {
                throw new IllegalArgumentException("Invalid daily rainfall depth at " + date);
            }
            if (days.putIfAbsent(date, rain) != null) {
                throw new IllegalArgumentException("Duplicate daily rainfall date: " + date);
            }
        }
        LocalDate previous = null;
        for (LocalDate date : days.keySet()) {
            if (previous != null && ChronoUnit.DAYS.between(previous, date) != 1) {
                throw new IllegalArgumentException("Daily rainfall has a date gap after " + previous);
            }
            previous = date;
        }
        return new DailyRainfallSeries(days);
    }

    public double rainMm(String date) {
        LocalDate day = LocalDate.parse(date, DATE);
        Double rain = rainByDay.get(day);
        if (rain == null) {
            throw new IllegalArgumentException("Daily rainfall absent for " + day);
        }
        return rain;
    }
}
