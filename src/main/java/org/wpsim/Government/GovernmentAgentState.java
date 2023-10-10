/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 * \ V  V / | |_) |\__ \ *    @since 2023                                  *
 * \_/\_/  | .__/ |___/ *                                                 *
 * | |          *    @author Jairo Serrano                        *
 * |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.Government;

import BESA.Kernel.Agent.StateBESA;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wpsim.Simulator.wpsConfig;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jairo
 */
public class GovernmentAgentState extends StateBESA implements Serializable {

    private static final int GRID_SIZE = 20;
    /**
     * Map that contains the land ownership information.
     */
    private Map<String, LandInfo> landOwnership;
    /**
     * Map that contains the farm information.
     */
    private Map<String, List<String>> farms;

    /**
     * Constructor.
     */
    public GovernmentAgentState() {
        super();
        this.landOwnership = new HashMap<>();
        initializeLands();
    }

    /**
     * Initializes the land ownership map with lands.
     */
    private void initializeLands() {
        try {
            // Cargar el archivo JSON
            String content = wpsConfig.getInstance().loadFile("web/world.json");

            // Parsear el contenido del archivo JSON
            JSONArray landsArray = new JSONArray(content);

            // Iterar sobre el contenido parseado y asignar los datos al hashmap
            for (int i = 0; i < landsArray.length(); i++) {
                JSONObject landObject = landsArray.getJSONObject(i);
                String landName = landObject.getString("name");
                String kind = landObject.getString("kind");

                // Usar la estructura LandInfo para almacenar el tipo de tierra y la finca
                LandInfo landInfo = new LandInfo(kind);
                landOwnership.put(landName, landInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.farms = new HashMap<>();

        createFarms();

        System.out.println("Farms created");
        System.out.println(farms);

        // Contar fincas por tamaño
        long largeFarmsCount = farms.keySet().stream().filter(farmName -> farmName.contains("_large")).count();
        long mediumFarmsCount = farms.keySet().stream().filter(farmName -> farmName.contains("_medium")).count();
        long smallFarmsCount = farms.keySet().stream().filter(farmName -> farmName.contains("_small")).count();

        // Calcular el número total de tierras asignadas
        int totalLandsAssigned = farms.values().stream()
                .mapToInt(List::size)
                .sum();

        // Imprimir la información
        System.out.println("Total farms created: " + farms.size());
        System.out.println("Large farms: " + largeFarmsCount);
        System.out.println("Medium farms: " + mediumFarmsCount);
        System.out.println("Small farms: " + smallFarmsCount);
        System.out.println("Total lands assigned: " + totalLandsAssigned);
    }

    private Point landNameToPoint(String landName) {
        int number = Integer.parseInt(landName.replace("land_", ""));
        int x = (number - 1) % GRID_SIZE;
        int y = (number - 1) / GRID_SIZE;
        return new Point(x, y);
    }

    private List<String> selectBlock(List<String> availableLands, int rows, int cols) {
        for (int i = 0; i < availableLands.size(); i++) {
            String landName = availableLands.get(i);
            Point startPoint = landNameToPoint(landName);

            if (isBlockAvailable(startPoint, rows, cols, availableLands)) {
                return extractBlock(startPoint, rows, cols, availableLands);
            }
        }
        return new ArrayList<>();
    }

    private boolean isBlockAvailable(Point startPoint, int rows, int cols, List<String> availableLands) {
        for (int x = startPoint.x; x < startPoint.x + cols; x++) {
            for (int y = startPoint.y; y < startPoint.y + rows; y++) {
                if (x >= GRID_SIZE || y >= GRID_SIZE || !availableLands.contains("land_" + (y * GRID_SIZE + x + 1))) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<String> extractBlock(Point startPoint, int rows, int cols, List<String> availableLands) {
        List<String> block = new ArrayList<>();
        for (int x = startPoint.x; x < startPoint.x + cols; x++) {
            for (int y = startPoint.y; y < startPoint.y + rows; y++) {
                String landName = "land_" + (y * GRID_SIZE + x + 1);
                block.add(landName);
                availableLands.remove(landName);
            }
        }
        return block;
    }

    public synchronized Map.Entry<String, Map<String, String>> assignLandToFamily(String familyName) {
        List<String> availableFarms = farms.entrySet().stream()
                .filter(e -> e.getValue().stream().allMatch(land -> landOwnership.get(land).farmName == null))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (availableFarms.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        String selectedFarm = availableFarms.get(rand.nextInt(availableFarms.size()));

        List<String> landsOfSelectedFarm = farms.get(selectedFarm);
        Map<String, String> landsWithKind = new HashMap<>();
        for (String land : landsOfSelectedFarm) {
            landOwnership.get(land).farmName = familyName;
            landsWithKind.put(land, landOwnership.get(land).kind);
        }
        return new AbstractMap.SimpleEntry<>(selectedFarm, landsWithKind);
    }

    public void createFarms() {
        List<String> availableLands = landOwnership.entrySet().stream()
                .filter(e -> !e.getValue().kind.equals("road") && e.getValue().farmName == null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Random rand = new Random();
        int farmId = 1;

        // Asignar fincas grandes
        while (true) {
            List<String> farmLands = selectBlock(availableLands, 4, 5);
            if (farmLands.isEmpty()) {
                break;
            }
            farms.put("farm_" + farmId + "_large", farmLands);
            farmId++;
        }

        // Asignar fincas medianas
        while (true) {
            List<String> farmLands = selectBlock(availableLands, 2, 5);
            if (farmLands.isEmpty()) {
                break;
            }
            farms.put("farm_" + farmId + "_medium", farmLands);
            farmId++;
        }

        // Asignar fincas pequeñas
        while (!availableLands.isEmpty()) {
            List<String> farmLands = selectBlock(availableLands, 1, 2);
            if (farmLands.isEmpty()) {
                break;
            }
            farms.put("farm_" + farmId + "_small", farmLands);
            farmId++;
        }
    }

    /**
     * Removes the assignment of a specified land.
     */
    public boolean removeLandAssignment(String landName) {
        if (landOwnership.containsKey(landName) && landOwnership.get(landName) != null) {
            landOwnership.put(landName, null);
            return true;
        }
        return false;
    }

    /**
     * Returns the land ownership map.
     *
     * @return Map<String, String>
     */
    public synchronized Map<String, LandInfo> getLandOwnership() {
        return landOwnership;
    }

    /**
     * Return the land ownership map.
     *
     * @return
     */
    @Override
    public String toString() {
        return "GovernmentAgentState{" +
                "landOwnership=" + landOwnership +
                '}';
    }
}
