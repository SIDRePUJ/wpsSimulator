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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.*;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.Base.wpsLandTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Agent.KillWorldGuard;
import org.wpsim.World.Agent.WorldAgent;
import org.wpsim.World.Guards.WorldGuard;
import org.wpsim.World.Agent.WorldState;
import org.wpsim.World.Helper.Hemisphere;
import org.wpsim.World.Helper.Soil;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Messages.WorldMessage;
import org.wpsim.World.Messages.WorldMessageType;
import org.wpsim.World.layer.crop.CropLayer;
import org.wpsim.World.layer.crop.cell.roots.RootsCell;
import org.wpsim.World.layer.disease.DiseaseCell;
import org.wpsim.World.layer.disease.DiseaseLayer;
import org.wpsim.World.layer.evapotranspiration.EvapotranspirationLayer;
import org.wpsim.World.layer.rainfall.RainfallLayer;
import org.wpsim.World.layer.shortWaveRadiation.ShortWaveRadiationLayer;
import org.wpsim.World.layer.temperature.TemperatureLayer;
import rational.mapping.Believes;

import static org.wpsim.World.Messages.WorldMessageType.CROP_INIT;

/**
 * @author jairo
 */
public class PlantCropTask extends wpsLandTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        updateConfig(believes, 32);
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        //believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "PLANTING", "FOOD"));

        // Set world perturbation
        setPerturbation(wpsStart.config.getPerturbation());

        PeasantFamilyProfile profile = believes.getPeasantProfile();
        String rainfallConditions = profile.getRainfallConditions();
        String peasantAlias = profile.getPeasantFamilyAlias();
        int cropSize = profile.getCropSize();
        String currentCropName = profile.getCurrentCropName();

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.PLANTING)) {
                wpsReport.info("Plantando for " + currentLandInfo.getLandName(), peasantAlias);
                this.increaseWorkDone(believes, currentLandInfo.getLandName(), TimeConsumedBy.PlantCropTask.getTime());
                if (this.isWorkDone(believes, currentLandInfo.getLandName())) {
                    this.resetLand(believes, currentLandInfo.getLandName());
                    wpsReport.info("Finalización de plantado for " + currentLandInfo.getLandName(), peasantAlias);
                    //System.out.println("plantando " + currentLandInfo.getLandName());
                    boolean isRunning = false;
                    try {
                        AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName());
                        isRunning = true;
                    } catch (ExceptionBESA ex) {
                        wpsReport.info("Se debe crear el agente mundo " + currentLandInfo.getLandName(), peasantAlias);
                    }
                    WorldMessage worldMessage = null;
                    if (isRunning) {
                        wpsReport.warn("Renovando cultivo " + currentLandInfo.getLandName(), peasantAlias);
                        worldMessage = new WorldMessage(
                                CROP_INIT,
                                currentLandInfo.getLandName(),
                                believes.getInternalCurrentDate(),
                                peasantAlias
                        );
                        try {
                            EventBESA ev = new EventBESA(WorldGuard.class.getName(), worldMessage);
                            AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName()).sendEvent(ev);
                        } catch (ExceptionBESA ex) {
                            wpsReport.error("Error renovando tierra " + currentLandInfo.getLandName(), peasantAlias);
                        }

                        // TODO: Estimar el costo de Semillas
                        profile.useSeeds(profile.getRiceSeedsByHectare());
                        currentLandInfo.setCurrentSeason(SeasonType.GROWING);

                    } else {
                        try {

                            WorldAgent landAgent = buildWorld(
                                    getRainfallFile(rainfallConditions),
                                    peasantAlias,
                                    currentLandInfo.getLandName(),
                                    cropSize,
                                    currentCropName
                            );
                            assert landAgent != null;
                            initialWorldStateInitialization(
                                    landAgent,
                                    peasantAlias,
                                    believes.getInternalCurrentDate()
                            );
                            landAgent.start();

                            worldMessage = new WorldMessage(
                                    CROP_INIT,
                                    currentLandInfo.getLandName(),
                                    believes.getInternalCurrentDate(),
                                    peasantAlias
                            );

                            AdmBESA.getInstance().getHandlerByAlias(
                                    currentLandInfo.getLandName()
                            ).sendEvent(
                                    new EventBESA(
                                            WorldGuard.class.getName(),
                                            worldMessage
                                    )
                            );

                            // TODO: Estimar el costo de Semillas
                            profile.useSeeds(profile.getRiceSeedsByHectare());
                            currentLandInfo.setCurrentSeason(SeasonType.GROWING);

                        } catch (ExceptionBESA ex) {
                            wpsReport.error(ex, peasantAlias);
                        }
                    }
                }
                // Exit at first iteration with PLANTING
                return;
            }
        }
    }

    private static void setPerturbation(String arg) {
        WorldConfiguration worldConfiguration = WorldConfiguration.getPropsInstance();
        switch (arg) {
            case "disease" -> worldConfiguration.setPerturbations(true, false);
            case "course" -> worldConfiguration.setPerturbations(false, true);
            case "all" -> worldConfiguration.setPerturbations(true, true);
            default -> worldConfiguration.setPerturbations(false, false);
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
        DiseaseCell diseaseCellRoots = new DiseaseCell("roots1DiseaseCell");
        diseaseLayer.addVertex(diseaseCellRoots);
        CropLayer cropLayer = new CropLayer();
        cropLayer.addCrop(
                new RootsCell(
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
                diseaseCellRoots,
                cropName,
                agentAlias
                )
        );
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
        //wpsReport.warn(agentAlias + " " + aliasWorldAgent, "ObtainALandTask");
        WorldState worldState = buildWorldState(rainfallFile, agentAlias, cropSize, cropName);
        StructBESA structBESA = new StructBESA();
        structBESA.bindGuard(WorldGuard.class);
        structBESA.bindGuard(KillWorldGuard.class);
        try {
            return new WorldAgent(aliasWorldAgent, worldState, structBESA);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "ObtainALandTask");
        }
        return null;
    }

    private static String getRainfallFile(String arg) {
        WorldConfiguration worldConfiguration = WorldConfiguration.getPropsInstance();
        String rainfallFile;
        switch (arg) {
            case "wet" -> rainfallFile = worldConfiguration.getProperty("data.rainfall.wet");
            case "dry" -> rainfallFile = worldConfiguration.getProperty("data.rainfall.dry");
            case "normal" -> rainfallFile = worldConfiguration.getProperty("data.rainfall");
            default -> rainfallFile = worldConfiguration.getProperty("data.rainfall");
        }
        return rainfallFile;
    }
}
