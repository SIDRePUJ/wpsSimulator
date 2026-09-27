package org.wpsim.research.water;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Pins research families to existing farms independently of agent arrival order. */
public final class FarmAssignmentPlan {
    private static final FarmAssignmentPlan ACTIVE = loadConfigured();

    private final Map<String, String> farmsByFamily;
    private final Set<String> assignedFamilies = ConcurrentHashMap.newKeySet();
    private final Set<String> failedFamilies = ConcurrentHashMap.newKeySet();

    private FarmAssignmentPlan(Map<String, String> farmsByFamily) {
        this.farmsByFamily = Map.copyOf(farmsByFamily);
    }

    public static FarmAssignmentPlan active() {
        return ACTIVE;
    }

    private static FarmAssignmentPlan loadConfigured() {
        String file = System.getProperty("wps.water.farmAssignments", "");
        if (file.isBlank()) {
            return null;
        }
        try {
            return load(Path.of(file));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read research farm assignments: " + file, e);
        }
    }

    public static FarmAssignmentPlan load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        if (lines.isEmpty() || !lines.get(0).trim().equals("family_alias,farm_name")) {
            throw new IllegalArgumentException("Farm assignment header must be family_alias,farm_name");
        }
        Map<String, String> assignments = new HashMap<>();
        Set<String> farmNames = ConcurrentHashMap.newKeySet();
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",", -1);
            if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()
                    || !fields[0].equals(fields[0].trim()) || !fields[1].equals(fields[1].trim())) {
                throw new IllegalArgumentException("Invalid farm assignment row " + (i + 1));
            }
            if (assignments.putIfAbsent(fields[0], fields[1]) != null || !farmNames.add(fields[1])) {
                throw new IllegalArgumentException("Duplicate family or farm assignment at row " + (i + 1));
            }
        }
        if (assignments.isEmpty()) {
            throw new IllegalArgumentException("Farm assignment manifest is empty");
        }
        return new FarmAssignmentPlan(assignments);
    }

    /** Called under the authority's assignment lock. */
    public String selectFarm(String familyAlias, List<String> availableFarms) {
        String farm = farmsByFamily.get(familyAlias);
        if (farm == null || !availableFarms.contains(farm) || !assignedFamilies.add(familyAlias)) {
            failedFamilies.add(familyAlias);
            throw new IllegalStateException("Unmapped, unavailable or duplicate research farm: " + familyAlias);
        }
        return farm;
    }

    public Status status() {
        return new Status(farmsByFamily.size(), assignedFamilies.size(), failedFamilies.size());
    }

    public record Status(int plannedFamilies, int assignedFamilies, int failedFamilies) {
        public boolean valid() {
            return plannedFamilies == assignedFamilies && failedFamilies == 0;
        }
    }
}
