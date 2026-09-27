package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DailyRainfallSeriesTest {
    private DailyRainfallSeriesTest() {
    }

    public static void main(String[] args) throws IOException {
        Path directory = Files.createTempDirectory("wps-daily-rain-");
        Path valid = directory.resolve("valid.csv");
        Files.writeString(valid, "date,rain_mm\n28/02/2022,0\n01/03/2022,12.5\n");
        DailyRainfallSeries series = DailyRainfallSeries.fromCsv(valid);
        assert series.rainMm("28/02/2022") == 0.0;
        assert series.rainMm("01/03/2022") == 12.5;
        try {
            series.rainMm("02/03/2022");
            throw new AssertionError("Missing forcing day must fail");
        } catch (IllegalArgumentException expected) {
            // Missing climate forcing cannot silently fall back to a random draw.
        }
        expectInvalid(directory, "gap", "date,rain_mm\n28/02/2022,0\n02/03/2022,1\n");
        expectInvalid(directory, "duplicate", "date,rain_mm\n01/03/2022,1\n01/03/2022,2\n");
        expectInvalid(directory, "missing", "date,rain_mm\n01/03/2022,-999\n");
        expectInvalid(directory, "nan", "date,rain_mm\n01/03/2022,NaN\n");
        expectInvalid(directory, "header", "day,rain\n01/03/2022,1\n");
        System.out.println("DailyRainfallSeriesTest PASS");
    }

    private static void expectInvalid(Path directory, String name, String content) throws IOException {
        Path path = directory.resolve(name + ".csv");
        Files.writeString(path, content);
        try {
            DailyRainfallSeries.fromCsv(path);
            throw new AssertionError("Invalid daily rainfall must fail: " + name);
        } catch (IllegalArgumentException expected) {
            // The scenario must be complete and physically defined before agents start.
        }
    }
}
