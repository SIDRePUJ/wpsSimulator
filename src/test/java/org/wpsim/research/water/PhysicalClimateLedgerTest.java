package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public final class PhysicalClimateLedgerTest {
    private PhysicalClimateLedgerTest() {
    }

    public static void main(String[] args) throws IOException {
        Path directory = Files.createTempDirectory("wps-climate-audit-");
        PhysicalClimateLedger complete = new PhysicalClimateLedger(Set.of("plot-a", "plot-b"));
        complete.record("plot-b", "01/05/2022", 0, 4, 29, 19);
        complete.record("plot-a", "30/04/2022", 3, 4, 28, 18);
        complete.record("plot-a", "01/05/2022", 0, 5, 29, 19);
        Path output = directory.resolve("complete.csv");
        PhysicalClimateLedger.Summary summary = complete.writeCsv(output);
        assert summary.valid();
        assert summary.plannedPlots() == 2 && summary.observedPlots() == 2;
        assert summary.dailyRows() == 3 && summary.gapDays() == 0;
        String csv = Files.readString(output);
        assert csv.startsWith("plot_id,date,rain_mm,reference_et_mm,temperature_c,short_wave_radiation\n");
        assert csv.indexOf("plot-a,30/04/2022") < csv.indexOf("plot-a,01/05/2022");
        assert csv.indexOf("plot-a,01/05/2022") < csv.indexOf("plot-b,01/05/2022");
        try {
            complete.writeCsv(output);
            throw new AssertionError("Climate audit must not overwrite an existing file");
        } catch (FileAlreadyExistsException expected) {
            // Required evidence is immutable after a run.
        }

        PhysicalClimateLedger incomplete = new PhysicalClimateLedger(Set.of("plot-a", "plot-b"));
        incomplete.record("plot-a", "30/04/2022", 0, 4, 28, 18);
        incomplete.record("plot-a", "02/05/2022", 0, 4, 28, 18);
        incomplete.record("plot-a", "02/05/2022", 0, 4, 28, 18);
        PhysicalClimateLedger.Summary invalid = incomplete.writeCsv(directory.resolve("incomplete.csv"));
        assert !invalid.valid();
        assert invalid.missingPlots() == 1 && invalid.gapDays() == 1 && invalid.duplicateDays() == 1;
        try {
            complete.record("unknown", "01/05/2022", 0, 4, 28, 18);
            throw new AssertionError("Unknown plot must be rejected");
        } catch (IllegalArgumentException expected) {
            // Only eligible rice plots belong to this audit.
        }
        try {
            complete.record("plot-a", "02/05/2022", Double.NaN, 4, 28, 18);
            throw new AssertionError("Non-finite forcing must be rejected");
        } catch (IllegalArgumentException expected) {
            // Non-finite forcing cannot be paired across policies.
        }
        System.out.println("PhysicalClimateLedgerTest PASS");
    }
}
