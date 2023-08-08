/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Tasks.L4SkillsResources;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.wpsReport;
import org.wpsim.World.Agent.WorldAgent;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Agent.WorldState;
import org.wpsim.World.Helper.Hemisphere;
import org.wpsim.World.Helper.Soil;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Messages.WorldMessage;
import org.wpsim.World.Messages.WorldMessageType;
import org.wpsim.World.layer.crop.CropLayer;
import org.wpsim.World.layer.crop.cell.rice.RiceCell;
import org.wpsim.World.layer.disease.DiseaseCell;
import org.wpsim.World.layer.disease.DiseaseLayer;
import org.wpsim.World.layer.evapotranspiration.EvapotranspirationLayer;
import org.wpsim.World.layer.rainfall.RainfallLayer;
import org.wpsim.World.layer.shortWaveRadiation.ShortWaveRadiationLayer;
import org.wpsim.World.layer.temperature.TemperatureLayer;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.PeasantActivityType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class ObtainALandTask extends Task {

    /**
     *
     */
    public ObtainALandTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        // @TODO: setFarmName lo cambia el gobierno o el campesino
        believes.getPeasantProfile().setLand(true);
        believes.setCurrentActivity(PeasantActivityType.PRICE_LIST);
        believes.getPeasantProfile().setHousing(1);
        believes.getPeasantProfile().setServicesPresence(1);
        believes.getPeasantProfile().setHousingSize(1);
        believes.getPeasantProfile().setHousingLocation(1);
        believes.getPeasantProfile().setFarmDistance(1);

        // Set world perturbation
        setPerturbation(wpsStart.config.getPerturbation());

        try {
            WorldAgent worldAgent = buildWorld(
                    getRainfallFile(believes.getPeasantProfile().getRainfallConditions()),
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getPeasantProfile().getPeasantFamilyLandAlias(),
                    believes.getPeasantProfile().getCropSize(),
                    believes.getPeasantProfile().getCurrentCropName()
            );
            initialWorldStateInitialization(
                    worldAgent,
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getInternalCurrentDate()
            );

            worldAgent.start();

        } catch (Exception ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }

        wpsReport.info("🥬 " + believes.getPeasantProfile().getPeasantFamilyAlias() + " ya tiene tierra " + believes.getPeasantProfile().getPeasantFamilyLandAlias(), believes.getPeasantProfile().getPeasantFamilyAlias());
        this.setTaskFinalized();

    }

    private static void setPerturbation(String arg) {
        WorldConfiguration worldConfiguration = WorldConfiguration.getPropsInstance();
        switch (arg) {
            case "disease" ->
                worldConfiguration.setPerturbations(true, false);
            case "course" ->
                worldConfiguration.setPerturbations(false, true);
            case "all" ->
                worldConfiguration.setPerturbations(true, true);
            default ->
                worldConfiguration.setPerturbations(false, false);
        }
    }

    private static WorldState buildWorldState(
            String rainfallFile,
            String agentAlias,
            int cropSize,
            String cropName) {
        WorldConfiguration worldConfiguration = WorldConfiguration.getPropsInstance();
        ShortWaveRadiationLayer radiationLayer = new ShortWaveRadiationLayer(
                worldConfiguration.getProperty("data.radiation"),
                Hemisphere.SOUTHERN,
                9);
        TemperatureLayer temperatureLayer = new TemperatureLayer(
                worldConfiguration.getProperty("data.temperature"));
        EvapotranspirationLayer evapotranspirationLayer = new EvapotranspirationLayer(
                worldConfiguration.getProperty("data.evapotranspiration"));
        RainfallLayer rainfallLayer = new RainfallLayer(rainfallFile);
        DiseaseLayer diseaseLayer = new DiseaseLayer();
        DiseaseCell diseaseCellRice = new DiseaseCell("rice1DiseaseCell");
        diseaseLayer.addVertex(diseaseCellRice);
        CropLayer cropLayer = new CropLayer();
        cropLayer.addCrop(new RiceCell(
                1.05,
                1.2,
                0.7,
                1512,
                3330,
                cropSize,
                0.9,
                0.2,
                Soil.SAND,
                true,
                diseaseCellRice,
                cropName,
                agentAlias));
        cropLayer.bindLayer("radiation", radiationLayer);
        cropLayer.bindLayer("rainfall", rainfallLayer);
        cropLayer.bindLayer("temperature", temperatureLayer);
        cropLayer.bindLayer("evapotranspiration", evapotranspirationLayer);
        return new WorldState(
                temperatureLayer,
                radiationLayer,
                cropLayer,
                diseaseLayer,
                evapotranspirationLayer,
                rainfallLayer);
    }

    private static void initialWorldStateInitialization(WorldAgent worldAgent, String agentAlias, String currentDate) {
        AdmBESA adm = AdmBESA.getInstance();
        WorldMessage worldMessage = new WorldMessage(
                WorldMessageType.CROP_INIT,
                null,
                currentDate,
                agentAlias);
        try {
            AgHandlerBESA ah = adm.getHandlerByAid(worldAgent.getAid());
            EventBESA eventBesa = new EventBESA(WorldGuard.class.getName(), worldMessage);
            ah.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            wpsReport.error(e, "ObtainALandTask");
        }
    }

    private static WorldAgent buildWorld(
            String rainfallFile,
            String agentAlias,
            String aliasWorldAgent,
            int cropSize,
            String cropName) {
        wpsReport.warn(agentAlias + " " + aliasWorldAgent, "ObtainALandTask");
        WorldState worldState = buildWorldState(rainfallFile, agentAlias, cropSize, cropName);
        StructBESA structBESA = new StructBESA();
        structBESA.bindGuard(WorldGuard.class);
        try {
            WorldAgent worldAgent = new WorldAgent(aliasWorldAgent, worldState, structBESA);
            return worldAgent;
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "ObtainALandTask");
        }
        return null;
    }

    private static String getRainfallFile(String arg) {
        WorldConfiguration worldConfiguration = WorldConfiguration.getPropsInstance();
        String rainfallFile;
        switch (arg) {
            case "wet" ->
                rainfallFile = worldConfiguration.getProperty("data.rainfall.wet");
            case "dry" ->
                rainfallFile = worldConfiguration.getProperty("data.rainfall.dry");
            case "normal" ->
                rainfallFile = worldConfiguration.getProperty("data.rainfall");
            default ->
                rainfallFile = worldConfiguration.getProperty("data.rainfall");
        }
        return rainfallFile;
    }

    /**
     *
     * @param believes
     */
    @Override
    public void interruptTask(Believes believes) {
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
        this.setTaskFinalized();
    }

    /**
     *
     * @param believes
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.getPeasantProfile().getLand();
    }
}
