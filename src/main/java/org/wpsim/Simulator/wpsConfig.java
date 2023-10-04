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
package org.wpsim.Simulator;

import BESA.Log.ReportBESA;
import BESA.Util.FileLoader;
import com.google.gson.Gson;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.wpsim.PeasantFamily.Data.FarmingResource;
import org.wpsim.PeasantFamily.Data.PeasantFamilyProfile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @author jairo
 */
public final class wpsConfig {

    private static final wpsConfig INSTANCE = new wpsConfig();
    private String SocietyAgentName;
    private String BankAgentName;
    private String MarketAgentName;
    private String ControlAgentName;
    private String PerturbationAgentName;
    private String GovernmentAgentName;
    private String ViewerAgentName;
    private String peasantType;
    private String rainfallConditions;
    private String perturbation;
    private String startSimulationDate;
    private int peasantSerialID;

    private PeasantFamilyProfile stableFarmerProfile;
    private PeasantFamilyProfile highRiskFarmerProfile;
    private PeasantFamilyProfile thrivingFarmerProfile;

    /**
     *
     */
    public static wpsConfig getInstance() {
        return INSTANCE;
    }

    /**
     *
     */
    private wpsConfig() {
        loadPeasantConfig();
        loadWPSConfig();
        this.peasantSerialID = 1;
        this.perturbation = "";

    }

    public String getSocietyAgentName() {
        return SocietyAgentName;
    }

    public String getBankAgentName() {
        return BankAgentName;
    }

    public String getMarketAgentName() {
        return MarketAgentName;
    }

    public String getPerturbationAgentName() {
        return PerturbationAgentName;
    }

    public String getControlAgentName() {
        return ControlAgentName;
    }

    public String getViewerAgentName() {
        return this.ViewerAgentName;
    }

    /**
     * @return
     */
    public PeasantFamilyProfile getStableFarmerProfile() {
        return stableFarmerProfile.clone();
    }

    /**
     * @return
     */
    public PeasantFamilyProfile getHighRiskFarmerProfile() {
        return highRiskFarmerProfile.clone();
    }

    /**
     * @return
     */
    public PeasantFamilyProfile getThrivingFarmerProfile() {
        return thrivingFarmerProfile.clone();
    }

    /**
     * @return
     */
    public String getStartSimulationDate() {
        return startSimulationDate;
    }

    /**
     * @param startSimulationDate
     */
    public void setStartSimulationDate(String startSimulationDate) {
        this.startSimulationDate = startSimulationDate;
    }

    /**
     * @return
     */
    public String getPeasantType() {
        return peasantType;
    }

    /**
     * @param peasantType
     */
    public void setPeasantType(String peasantType) {
        this.peasantType = peasantType;
    }

    /**
     * @return
     */
    public String getPerturbation() {
        return perturbation;
    }

    /**
     * @param perturbation
     */
    public void setPerturbation(String perturbation) {
        this.perturbation = perturbation;
    }

    public Map<String, FarmingResource> loadMarketConfig() {

        Map<String, FarmingResource> priceList = new HashMap<>();
        Properties properties = new Properties();
        FileInputStream fileInputStream = null;

        try {
            // Carga las propiedades desde el archivo
            properties.load(loadFileAsStream("wpsConfig.properties"));

            String[] resourceNames = {
                    "water", "seeds", "pesticides",
                    "tools", "livestock", "ñame"
            };

            for (String resourceName : resourceNames) {
                priceList.put(resourceName,
                        new FarmingResource(
                                resourceName,
                                properties.getProperty(
                                        "market." + resourceName + ".price"
                                ),
                                properties.getProperty(
                                        "market." + resourceName + ".quantity"
                                )
                        )
                );
            }
            fileInputStream.close();
            return priceList;

        } catch (IOException e) {
            ReportBESA.error(e.getMessage());
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    ReportBESA.error(e.getMessage());
                }
            }
        }
        return null;
    }

    private void loadWPSConfig() {
        Properties properties = new Properties();
        try {
            properties.load(loadFileAsStream("wpsConfig.properties"));
            this.startSimulationDate = properties.getProperty("control.startdate");
            this.BankAgentName = properties.getProperty("bank.name");
            this.ControlAgentName = properties.getProperty("control.name");
            this.MarketAgentName = properties.getProperty("market.name");
            this.SocietyAgentName = properties.getProperty("society.name");
            this.PerturbationAgentName = properties.getProperty("perturbation.name");
            this.ViewerAgentName = properties.getProperty("viewer.name");
            this.GovernmentAgentName = properties.getProperty("government.name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPeasantConfig() {
        LoadSettings settings = LoadSettings.builder().build();
        Load load = new Load(settings);
        Map<String, Object> data;

        String jsonData;
        String yamlContent;
        Gson gson = new Gson();

        yamlContent = loadFile("wpsStablePeasant.yml");
        data = (Map<String, Object>) load.loadFromString(yamlContent);
        Map<String, Object> regularPeasant = (Map<String, Object>) data.get("StablePeasant");
        jsonData = gson.toJson(regularPeasant);
        stableFarmerProfile = gson.fromJson(jsonData, PeasantFamilyProfile.class);

        yamlContent = loadFile("wpsHighriskPeasant.yml");
        data = (Map<String, Object>) load.loadFromString(yamlContent);
        Map<String, Object> lazyPeasant = (Map<String, Object>) data.get("HighriskPeasant");
        jsonData = gson.toJson(lazyPeasant);
        highRiskFarmerProfile = gson.fromJson(jsonData, PeasantFamilyProfile.class);

        yamlContent = loadFile("wpsThrivingPeasant.yml");
        data = (Map<String, Object>) load.loadFromString(yamlContent);
        Map<String, Object> proactivePeasant = (Map<String, Object>) data.get("ThrivingPeasant");
        jsonData = gson.toJson(proactivePeasant);
        thrivingFarmerProfile = gson.fromJson(jsonData, PeasantFamilyProfile.class);
    }

    private InputStream loadFileAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            File file = new File(fileName);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                throw new FileNotFoundException("No se pudo encontrar " + fileName + " ni dentro del JAR ni en el sistema de archivos");
            }
        }
        return inputStream;
    }


    public String loadFile(String fileName) {
        InputStream inputStream = null;
        try {
            // Intentar cargar desde dentro del JAR
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            // Si no se encuentra dentro del JAR, intentar cargarlo desde el sistema de archivos
            if (inputStream == null) {
                File file = new File(fileName);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                } else {
                    throw new FileNotFoundException("No se pudo encontrar " + fileName + " ni dentro del JAR ni en el sistema de archivos");
                }
            }

            // Convertir InputStream a String
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();

        } catch (IOException e) {
            ReportBESA.error(e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    ReportBESA.error(e.getMessage());
                }
            }
        }
    }


    private double generateRandomNumber(double min, double max) {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    public PeasantFamilyProfile getFarmerProfile() {

        PeasantFamilyProfile pfProfile = this.getHighRiskFarmerProfile();
        //getThrivingFarmerProfile();
        //getStableFarmerProfile()
        //getHighRiskFarmerProfile()

        double rnd = 1 + generateRandomNumber(
                pfProfile.getVariance() * -1,
                pfProfile.getVariance()
        );
        //ReportBESA.error(rnd + " random number");

        pfProfile.setHealth((int) (pfProfile.getHealth() * rnd));
        pfProfile.setMoney((int) (pfProfile.getMoney() * rnd));
        pfProfile.setWaterAvailable((int) (pfProfile.getWaterAvailable() * rnd));
        pfProfile.setSeeds((int) (pfProfile.getSeeds() * rnd));
        pfProfile.setCropSize((int) (pfProfile.getCropSize() * rnd));
        pfProfile.setPeasantFamilyAffinity(pfProfile.getPeasantFamilyAffinity() * rnd);
        pfProfile.setPeasantFriendsAffinity(pfProfile.getPeasantFriendsAffinity() * rnd);
        pfProfile.setPeasantLeisureAffinity(pfProfile.getPeasantLeisureAffinity() * rnd);

        return pfProfile;
    }

    public synchronized String getUniqueFarmerName() {
        return "PeasantFamily_" + peasantSerialID++;
    }

    public String getGovernmentAgentName() {
        return this.GovernmentAgentName;
    }
}
