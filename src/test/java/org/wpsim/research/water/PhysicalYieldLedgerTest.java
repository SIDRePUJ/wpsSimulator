package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Synthetic harvest-output and fail-closed completeness checks. */
public final class PhysicalYieldLedgerTest {
    public static void main(String[] args) throws IOException {
        PhysicalYieldLedger ledger = new PhysicalYieldLedger(Map.of("small", 2.0, "large", 8.0), 6.0, 1.0);
        ledger.recordHarvest("small", 2.0, "10/04/2020", 80.0, 100.0);
        close(9.6, RiceYieldResponse.estimate(6.0, 80.0, 100.0, 1.0, 2.0).tonnes());
        expectFailure(() -> ledger.recordHarvest("unknown", 2.0, "10/04/2020", 80.0, 100.0));
        expectFailure(() -> ledger.recordHarvest("large", 7.0, "10/04/2020", 80.0, 100.0));
        expectStateFailure(() -> ledger.recordHarvest("small", 2.0, "10/04/2020", 80.0, 100.0));

        Path output = Files.createTempFile("physical-yield-", ".csv");
        Files.delete(output);
        try {
            PhysicalYieldLedger.Summary incomplete = ledger.writeCsv(output);
            if (incomplete.valid() || incomplete.missingHarvests() != 1 ||
                    !Files.readString(output).contains("large,8.0,,,,,,,NOT_HARVESTED")) {
                throw new AssertionError("missing harvest was not audited");
            }
            expectIoFailure(() -> ledger.writeCsv(output));
        } finally {
            Files.deleteIfExists(output);
        }

        ledger.recordHarvest("large", 8.0, "11/04/2020", 100.0, 100.0);
        Path completeOutput = Files.createTempFile("physical-yield-complete-", ".csv");
        Files.delete(completeOutput);
        try {
            PhysicalYieldLedger.Summary complete = ledger.writeCsv(completeOutput);
            String csv = Files.readString(completeOutput);
            String small = csv.lines().filter(line -> line.startsWith("small,")).findFirst().orElseThrow();
            String[] values = small.split(",", -1);
            if (!complete.valid() || complete.harvestedPlots() != 2 || values.length != 9 ||
                    !"HARVESTED".equals(values[8])) {
                throw new AssertionError("physical yield output did not reconcile: " + csv);
            }
            close(4.8, Double.parseDouble(values[5]));
            close(9.6, Double.parseDouble(values[6]));
            close(12.0, Double.parseDouble(values[7]));
        } finally {
            Files.deleteIfExists(completeOutput);
        }
        System.out.println("PhysicalYieldLedgerTest PASS");
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

    private static void expectStateFailure(Runnable action) {
        try {
            action.run();
        } catch (IllegalStateException expected) {
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }

    private static void expectIoFailure(IoAction action) {
        try {
            action.run();
        } catch (IOException expected) {
            return;
        }
        throw new AssertionError("expected IOException");
    }

    private interface IoAction {
        void run() throws IOException;
    }
}
