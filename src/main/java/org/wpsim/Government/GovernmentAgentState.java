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

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jairo
 */
public class GovernmentAgentState extends StateBESA implements Serializable {

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


    public void createFarms() {
        List<String> availableLands = landOwnership.entrySet().stream()
                .filter(e -> !e.getValue().kind.equals("road") && e.getValue().farmName == null) // Excluir tierras de tipo "road"
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int totalLands = availableLands.size();
        int largeFarmsLands = (int) (totalLands * 0.5);
        int mediumFarmsLands = (int) (totalLands * 0.3);
        // El resto será para fincas pequeñas

        int farmId = 1;
        Random rand = new Random();

        while (!availableLands.isEmpty()) {
            int size;
            String farmSize;

            if (largeFarmsLands > 0) {
                size = Math.min(rand.nextInt(11) + 10, availableLands.size()); // 10 to 20
                farmSize = "large";
                largeFarmsLands -= size;
            } else if (mediumFarmsLands > 0) {
                size = Math.min(rand.nextInt(7) + 4, availableLands.size()); // 4 to 10
                farmSize = "medium";
                mediumFarmsLands -= size;
            } else {
                size = Math.min(rand.nextInt(4) + 1, availableLands.size()); // 1 to 4 or remaining lands
                farmSize = "small";
            }

            List<String> farmLands = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String land = availableLands.remove(rand.nextInt(availableLands.size()));
                farmLands.add(land);
            }

            farms.put("farm_" + farmId + "_" + farmSize, farmLands);
            farmId++;
        }
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
