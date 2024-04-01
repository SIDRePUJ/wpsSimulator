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
import BESA.Log.ReportBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.*;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.WellProdSim.Base.wpsLandTask;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.AgroEcosystem.Agent.CloseAgroEcosystemGuard;
import org.wpsim.AgroEcosystem.Agent.AgroEcosystem;
import org.wpsim.AgroEcosystem.Guards.AgroEcosystemGuard;
import org.wpsim.AgroEcosystem.Agent.AgroEcosystemState;
import org.wpsim.AgroEcosystem.Helper.Hemisphere;
import org.wpsim.AgroEcosystem.Helper.Soil;
import org.wpsim.AgroEcosystem.Helper.WorldConfiguration;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType;
import org.wpsim.AgroEcosystem.layer.crop.CropLayer;
import org.wpsim.AgroEcosystem.layer.crop.cell.rice.RiceCell;
import org.wpsim.AgroEcosystem.layer.crop.cell.roots.RootsCell;
import org.wpsim.AgroEcosystem.layer.disease.DiseaseCell;
import org.wpsim.AgroEcosystem.layer.disease.DiseaseLayer;
import org.wpsim.AgroEcosystem.layer.evapotranspiration.EvapotranspirationLayer;
import org.wpsim.AgroEcosystem.layer.rainfall.RainfallLayer;
import org.wpsim.AgroEcosystem.layer.shortWaveRadiation.ShortWaveRadiationLayer;
import org.wpsim.AgroEcosystem.layer.temperature.TemperatureLayer;
import rational.mapping.Believes;

import static org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType.CROP_INIT;

/**
 * @author jairo
 */
public class PlantCropTask extends wpsLandTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        updateConfig(believes, 1920);
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "PLANTING", "FOOD"));

        int factor;
        int harvestReady = 0;
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.PLANTING)) {
                harvestReady++;
            }
        }

        setPerturbation(wpsStart.config.getPerturbation());
        PeasantFamilyProfile profile = believes.getPeasantProfile();
        String initialRainfallConditions = WorldConfiguration.getPropsInstance().getProperty(
                "data.rainfall." + ControlCurrentDate.getInstance().getCurrentYear());
        String peasantAlias = profile.getPeasantFamilyAlias();
        int cropSize = profile.getCropSize();
        String currentCropName = believes.getCurrentCropName();
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.PLANTING)) {
                if (currentLandInfo.getCropName().isEmpty()) {
                    currentLandInfo.setCropName(currentCropName);
                }
                wpsReport.info("Plantando for " + currentLandInfo.getLandName(), peasantAlias);
                if (believes.getPeasantFamilyHelper().isBlank()) {
                    factor = (TimeConsumedBy.PlantCropTask.getTime()/harvestReady);
                }else{
                    factor = (TimeConsumedBy.PlantCropTask.getTime()/harvestReady) * 2;
                }
                this.increaseWorkDone(believes, currentLandInfo.getLandName(), factor);

                ReportBESA.info("Avanzando en plantación de " + currentLandInfo.getLandName());
                if (this.isWorkDone(believes, currentLandInfo.getLandName())) {
                    this.resetLand(believes, currentLandInfo.getLandName());
                    ReportBESA.info("Termina plantación en " + currentLandInfo.getLandName());
                    //System.out.println("Finalización de plantado for " + currentLandInfo.getLandName() + " " + peasantAlias);
                    wpsReport.info("Finalización de plantado for " + currentLandInfo.getLandName(), peasantAlias);
                    //System.out.println("plantando " + currentLandInfo.getLandName());
                    if (!currentLandInfo.getCropName().equals(currentCropName)) {
                        currentLandInfo.setCropName(currentCropName);
                    }
                    // Check if are Running to kill
                    boolean isRunning = false;
                    try {
                        AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName());
                        isRunning = true;
                    } catch (ExceptionBESA ex) {
                        wpsReport.info("Se debe crear el agente mundo " + currentLandInfo.getLandName(), peasantAlias);
                    }
                    if (isRunning) {
                        try {
                            String agID = AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName()).getAgId();
                            AdmBESA.getInstance().killAgent(agID, wpsStart.config.getDoubleProperty("control.passwd"));
                        } catch (Exception ex) {
                            ReportBESA.info("Error Eliminando la tierra " + currentLandInfo.getLandName() + ex.getMessage() + " " + peasantAlias);
                        }
                        try {
                            Thread.sleep(50);
                        } catch (Exception ex) {
                            ReportBESA.info("Error Temp "+ ex.getMessage() + " " + peasantAlias);
                        }
                    }
                    wpsReport.info("Cultivando en " + currentLandInfo.getLandName() + " " + currentLandInfo.getCropName() + " " + currentCropName, peasantAlias);

                    AgroEcosystem landAgent = buildWorld(
                            getRainfallFile(initialRainfallConditions),
                            peasantAlias,
                            currentLandInfo.getLandName(),
                            cropSize,
                            currentLandInfo.getCropName()
                    );
                    assert landAgent != null;
                    initialWorldStateInitialization(
                            landAgent,
                            peasantAlias,
                            believes.getInternalCurrentDate()
                    );
                    landAgent.start();
                    // CHECK WORLD
                    boolean confirmation = false;
                    while (!confirmation) {
                        try {
                            AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName());
                            confirmation = true;
                        } catch (ExceptionBESA e) {
                            ReportBESA.info(e + "--- " + peasantAlias);
                        } catch (Exception e) {
                            ReportBESA.info(e + " " + peasantAlias);
                        }

                        if (!confirmation) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                ReportBESA.info(e + " " + peasantAlias);
                                break;
                            }
                        }
                    }
                    try {
                        AdmBESA.getInstance().getHandlerByAlias(
                                currentLandInfo.getLandName()
                        ).sendEvent(
                                new EventBESA(
                                        AgroEcosystemGuard.class.getName(),
                                        new AgroEcosystemMessage(
                                                CROP_INIT,
                                                currentLandInfo.getLandName(),
                                                believes.getInternalCurrentDate(),
                                                peasantAlias
                                        )
                                )
                        );
                    } catch (ExceptionBESA ex) {
                        wpsReport.error(ex, peasantAlias);
                    }
                    profile.useSeeds(1);
                    currentLandInfo.setCurrentSeason(SeasonType.GROWING);
                    ReportBESA.info("Terminando plantación en " + currentLandInfo.getLandName());
                }// Exit at first iteration with PLANTING
            }
        }
        believes.useTime(TimeConsumedBy.PlantCropTask.getTime());
        believes.addTaskToLog(believes.getInternalCurrentDate());
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

    private static AgroEcosystemState buildWorldState(
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
        // @TODO: CAMBIAR TIPO DE CULTIVO DEPENDIENDO
        switch (cropName) {
            case "roots" -> cropLayer.addCrop(
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
            case "rice" -> cropLayer.addCrop(
                    new RiceCell(
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
        }
        cropLayer.bindLayer("radiation", radiationLayer);
        cropLayer.bindLayer("rainfall", rainfallLayer);
        cropLayer.bindLayer("temperature", temperatureLayer);
        cropLayer.bindLayer("evapotranspiration", evapotranspirationLayer);
        return new AgroEcosystemState(
                temperatureLayer,
                radiationLayer,
                cropLayer,
                diseaseLayer,
                evapotranspirationLayer,
                rainfallLayer);
    }

    private static void initialWorldStateInitialization(AgroEcosystem agroEcosystem, String agentAlias, String currentDate) {
        try {
            AdmBESA.getInstance().getHandlerByAid(
                    agroEcosystem.getAid()
            ).sendEvent(
                    new EventBESA(
                            AgroEcosystemGuard.class.getName(),
                            new AgroEcosystemMessage(
                                    AgroEcosystemMessageType.CROP_INIT,
                                    null,
                                    currentDate,
                                    agentAlias
                            )
                    )
            );
        } catch (ExceptionBESA e) {
            wpsReport.error(e, "ObtainALandTask");
        }
    }

    private static AgroEcosystem buildWorld(
            String rainfallFile,
            String agentAlias,
            String aliasWorldAgent,
            int cropSize,
            String cropName) {
        //wpsReport.warn(agentAlias + " " + aliasWorldAgent, "ObtainALandTask");
        AgroEcosystemState agroEcosystemState = buildWorldState(rainfallFile, agentAlias, cropSize, cropName);
        StructBESA structBESA = new StructBESA();
        structBESA.bindGuard(AgroEcosystemGuard.class);
        structBESA.bindGuard(CloseAgroEcosystemGuard.class);
        try {
            return new AgroEcosystem(aliasWorldAgent, agroEcosystemState, structBESA);
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

    @Override
    public void interruptTask(Believes parameters) {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        System.out.println("interruptTask " + believes.getAlias());
    }

    @Override
    public void cancelTask(Believes parameters) {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        System.out.println("cancelTask " + believes.getAlias());
    }
}
