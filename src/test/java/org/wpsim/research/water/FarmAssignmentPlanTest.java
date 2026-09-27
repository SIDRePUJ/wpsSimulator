package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Research farm membership must not depend on request arrival order. */
public final class FarmAssignmentPlanTest {
    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("farm-assignments-", ".csv");
        try {
            Files.writeString(file, "family_alias,farm_name\nfamily-1,farm-2\nfamily-2,farm-1\n");
            FarmAssignmentPlan plan = FarmAssignmentPlan.load(file);
            if (plan.cropAreaHaPerPlot("family-1") != null) {
                throw new AssertionError("two-column legacy fixture changed crop area");
            }
            if (plan.status().valid() || !"farm-1".equals(plan.selectFarm("family-2", List.of("farm-2", "farm-1")))
                    || !"farm-2".equals(plan.selectFarm("family-1", List.of("farm-2")))
                    || !plan.status().valid()) {
                throw new AssertionError("manifest did not pin both families independently of arrival order");
            }
            expectFailure(() -> plan.selectFarm("family-1", List.of("farm-2")));
            if (plan.status().valid() || plan.status().failedFamilies() != 1) {
                throw new AssertionError("duplicate assignment did not invalidate the plan");
            }
            FarmAssignmentPlan unavailable = FarmAssignmentPlan.load(file);
            expectFailure(() -> unavailable.selectFarm("family-1", List.of("farm-1")));
            expectFailure(() -> unavailable.selectFarm("unknown", List.of("farm-1", "farm-2")));
            if (unavailable.status().valid()) {
                throw new AssertionError("unavailable or unmapped farm did not invalidate the plan");
            }
            Files.writeString(file, "family_alias,farm_name\nfamily-1,farm-1\nfamily-2,farm-1\n");
            expectFailure(() -> loadUnchecked(file));
            Files.writeString(file, "family_alias,farm_name,crop_area_ha_per_plot\n"
                    + "family-1,farm-2,1\nfamily-2,farm-1,8\n");
            FarmAssignmentPlan heterogeneous = FarmAssignmentPlan.load(file);
            if (heterogeneous.cropAreaHaPerPlot("family-1") != 1
                    || heterogeneous.cropAreaHaPerPlot("family-2") != 8
                    || !"farm-1".equals(heterogeneous.selectFarm("family-2", List.of("farm-1")))) {
                throw new AssertionError("heterogeneous crop area/farm assignment was not parsed");
            }
            Files.writeString(file, "family_alias,farm_name,crop_area_ha_per_plot\nfamily-1,farm-1,0\n");
            expectFailure(() -> loadUnchecked(file));
            Files.writeString(file, "family_alias,farm_name,crop_area_ha_per_plot\nfamily-1,farm-1,1.5\n");
            expectFailure(() -> loadUnchecked(file));
        } finally {
            Files.deleteIfExists(file);
        }
        System.out.println("FarmAssignmentPlanTest PASS");
    }

    private static void loadUnchecked(Path file) {
        try {
            FarmAssignmentPlan.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException expected) {
            return;
        }
        throw new AssertionError("expected invalid farm assignment");
    }
}
