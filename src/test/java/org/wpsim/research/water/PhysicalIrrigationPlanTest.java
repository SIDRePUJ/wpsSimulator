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
            Path auditDir = Files.createTempDirectory("water-audit-");
            try {
                plan.registerPlot("small", 1);
                plan.registerPlot("large", 2);
                expectStateFailure(() -> plan.registerPlot("small", 1));
                plan.recordApplied("01/01/2020", "small", 10);
                plan.recordApplied("01/01/2020", "large", 10);
                expectStateFailure(() -> plan.recordApplied("01/01/2020", "small", 10));
                expectStateFailure(() -> plan.recordApplied("02/01/2020", "small", 1));
                Path audit = auditDir.resolve("applied.csv");
                PhysicalIrrigationPlan.AuditSummary summary = plan.writeAudit(audit);
                if (!summary.valid() || summary.plannedPlots() != 2 || summary.appliedDeliveries() != 2) {
                    throw new AssertionError("applied audit did not reconcile: " + summary);
                }
                if (!Files.readString(audit).contains("10.0,10.0,APPLIED")) {
                    throw new AssertionError("applied irrigation was not written to audit");
                }
                expectIoFailure(() -> plan.writeAudit(audit));

                PhysicalIrrigationPlan missing = PhysicalIrrigationPlan.load(
                        requests, 300, AllocationRule.PROPORTIONAL_DEMAND);
                missing.registerPlot("small", 1);
                PhysicalIrrigationPlan.AuditSummary missingSummary = missing.writeAudit(auditDir.resolve("missing.csv"));
                if (missingSummary.valid() || missingSummary.absentPlots() != 1
                        || missingSummary.missingDeliveries() != 1) {
                    throw new AssertionError("missing plot or delivery went unnoticed: " + missingSummary);
                }
                String missingCsv = Files.readString(auditDir.resolve("missing.csv"));
                if (!missingCsv.contains("PLOT_ABSENT") || !missingCsv.contains("NOT_APPLIED")) {
                    throw new AssertionError("missing audit statuses not written");
                }
            } finally {
                for (Path file : Files.list(auditDir).toList()) {
                    Files.deleteIfExists(file);
                }
                Files.deleteIfExists(auditDir);
            }
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
