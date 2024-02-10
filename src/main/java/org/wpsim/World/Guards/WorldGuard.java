package org.wpsim.World.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Util.PeriodicDataBESA;
import org.json.JSONObject;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldGuard;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldMessage;
import org.wpsim.PeasantFamily.Guards.FromWorld.FromWorldMessageType;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Agent.WorldState;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Messages.WorldMessage;
import org.wpsim.World.Messages.WorldMessageType;
import org.wpsim.World.layer.crop.CropLayer;
import org.wpsim.World.layer.crop.cell.CropCell;
import org.wpsim.World.layer.crop.cell.CropCellState;
import org.wpsim.World.layer.disease.DiseaseCellState;

/**
 * BESA world's guard, holds the actions that receive from the peasant agent
 */
public class WorldGuard extends GuardBESA {

    private WorldConfiguration worldConfig = WorldConfiguration.getPropsInstance();
    private boolean isExecutingCropObserveOrInformation = false;

    /**
     * @param eventBESA
     */
    @Override
    public synchronized void funcExecGuard(EventBESA eventBESA) {
        WorldMessage worldMessage = (WorldMessage) eventBESA.getData();
        WorldState worldState = (WorldState) this.agent.getState();
        FromWorldMessage peasantMessage;
        CropCellState cropCellState;
        CropCell cropCellInfo;
        DiseaseCellState diseaseCellState;
        JSONObject cropDataJson;

        // Verificar y establecer el estado para CROP_OBSERVE y CROP_INFORMATION
        if ((worldMessage.getWorldMessageType() == WorldMessageType.CROP_OBSERVE ||
                worldMessage.getWorldMessageType() == WorldMessageType.CROP_INFORMATION) &&
                isExecutingCropObserveOrInformation) {
            return;
        }

        try {
            if (worldMessage.getWorldMessageType() == WorldMessageType.CROP_OBSERVE ||
                    worldMessage.getWorldMessageType() == WorldMessageType.CROP_INFORMATION) {
                isExecutingCropObserveOrInformation = true;
            }

            switch (worldMessage.getWorldMessageType()) {
                case CROP_INIT:
                    worldState.lazyUpdateCropsForDate(worldMessage.getDate());
                    peasantMessage = new FromWorldMessage(
                            FromWorldMessageType.CROP_INIT,
                            worldMessage.getPeasantAgentAlias(),
                            "CROP_INIT",
                            this.getAgent().getAlias()
                    );
                    peasantMessage.setDate(worldMessage.getDate());
                    this.replyToPeasantAgent(
                            worldMessage.getPeasantAgentAlias(),
                            peasantMessage
                    );
                    break;
                case CROP_INFORMATION:
                    worldState.lazyUpdateCropsForDate(worldMessage.getDate());
                    //System.out.println("Received CROP_INFORMATION for " + worldMessage.getCropId());
                    cropCellState = worldState.getCropLayer().getCropStateById(worldMessage.getCropId());
                    cropCellInfo = worldState.getCropLayer().getCropCellById(worldMessage.getCropId());
                    diseaseCellState = (DiseaseCellState) cropCellInfo.getDiseaseCell().getCellState();
                    cropDataJson = new JSONObject(cropCellState);
                    cropDataJson.put("disease", diseaseCellState.isInfected());
                    cropDataJson.put("cropHarvestReady", cropCellInfo.isHarvestReady());
                    peasantMessage = new FromWorldMessage(
                            FromWorldMessageType.CROP_INFORMATION_NOTIFICATION,
                            worldMessage.getPeasantAgentAlias(),
                            cropDataJson.toString(),
                            this.getAgent().getAlias()
                    );
                    peasantMessage.setDate(worldMessage.getDate());
                    this.replyToPeasantAgent(worldMessage.getPeasantAgentAlias(), peasantMessage);
                    break;
                case CROP_OBSERVE:
                    worldState.getCropLayer().getAllCrops().forEach(cropCell -> {
                        if (((CropCellState) cropCell.getCellState()).isWaterStress()) {
                            this.notifyPeasantCropProblem(
                                    FromWorldMessageType.NOTIFY_CROP_WATER_STRESS,
                                    cropCell.getAgentPeasantId(),
                                    worldMessage.getDate()
                            );
                        }
                        if (((DiseaseCellState) cropCell.getDiseaseCell().getCellState()).isInfected()) {
                            this.notifyPeasantCropProblem(
                                    FromWorldMessageType.NOTIFY_CROP_DISEASE,
                                    cropCell.getAgentPeasantId(),
                                    worldMessage.getDate()
                            );
                        }
                        if (cropCell.isHarvestReady()) {
                            this.notifyPeasantCropReadyToHarvest(
                                    cropCell.getAgentPeasantId(),
                                    worldMessage.getDate());
                        }
                    });
                    break;
                case CROP_IRRIGATION:
                    String cropIdToIrrigate = worldMessage.getCropId();
                    String defaultWaterQuantity = this.worldConfig.getProperty("crop.defaultValuePerIrrigation");
                    //int irrigateValue = Integer.parseInt(defaultWaterQuantity) * worldState.getCropLayer().getCropCellById(cropIdToIrrigate).getCropArea();
                    int irrigateValue = (int) (Integer.parseInt(defaultWaterQuantity) * 1.1);
                    worldState.getCropLayer().addIrrigationEvent(
                            String.valueOf(irrigateValue),
                            worldMessage.getDate()
                    );
                    peasantMessage = new FromWorldMessage(
                            FromWorldMessageType.CROP_INFORMATION_NOTIFICATION,
                            worldMessage.getPeasantAgentAlias(),
                            "CROP_IRRIGATION",
                            this.getAgent().getAlias());
                    peasantMessage.setDate(worldMessage.getDate());
                    this.replyToPeasantAgent(
                            worldMessage.getPeasantAgentAlias(),
                            peasantMessage);
                    break;
                case CROP_PESTICIDE:
                    String cropIdToAddPesticide = worldMessage.getCropId();
                    String defaultCropInsecticideCoverage = this.worldConfig.getProperty("disease.insecticideDefaultCoverage");
                    String diseaseCellId = worldState.getCropLayer().getCropCellById(cropIdToAddPesticide).getDiseaseCell().getId();
                    worldState.getDiseaseLayer().addInsecticideEvent(
                            diseaseCellId,
                            defaultCropInsecticideCoverage,
                            worldMessage.getDate());
                    peasantMessage = new FromWorldMessage(
                            FromWorldMessageType.CROP_PESTICIDE,
                            worldMessage.getPeasantAgentAlias(),
                            defaultCropInsecticideCoverage + " " + diseaseCellId,
                            this.getAgent().getAlias());
                    peasantMessage.setDate(worldMessage.getDate());
                    this.replyToPeasantAgent(worldMessage.getPeasantAgentAlias(), peasantMessage);
                    break;
                case CROP_HARVEST:
                    this.harvestCrop(worldState.getCropLayer());
                    cropCellState = worldState.getCropLayer().getCropState();
                    cropCellInfo = worldState.getCropLayer().getCropCell();
                    diseaseCellState = (DiseaseCellState) cropCellInfo.getDiseaseCell().getCellState();
                    cropDataJson = new JSONObject(cropCellState);
                    cropDataJson.put("disease", diseaseCellState.isInfected());
                    cropDataJson.put("cropHarvestReady", cropCellInfo.isHarvestReady());
                    peasantMessage = new FromWorldMessage(
                            FromWorldMessageType.CROP_HARVEST,
                            worldMessage.getPeasantAgentAlias(),
                            cropDataJson.toString(),
                            this.getAgent().getAlias());
                    peasantMessage.setDate(worldMessage.getDate());
                    this.replyToPeasantAgent(
                            worldMessage.getPeasantAgentAlias(),
                            peasantMessage);
                    break;
            }

        } finally {
            // Restablecer el estado para permitir futuras ejecuciones
            if (worldMessage.getWorldMessageType() == WorldMessageType.CROP_OBSERVE ||
                    worldMessage.getWorldMessageType() == WorldMessageType.CROP_INFORMATION) {
                isExecutingCropObserveOrInformation = false;
            }
        }
    }

    /**
     * @param peasantAgentAlias
     * @param peasantMessage
     */
    public synchronized void replyToPeasantAgent(String peasantAgentAlias, FromWorldMessage peasantMessage) {
        try {
            EventBESA event = new EventBESA(
                    FromWorldGuard.class.getName(),
                    peasantMessage);
            this.agent.getAdmLocal().getHandlerByAlias(
                    peasantAgentAlias
            ).sendEvent(
                    event
            );
            //wpsReport.debug("Sent: " + peasantMessage.getPayload());
        } catch (ExceptionBESA e) {
            wpsReport.error(e.getMessage(), "WorldAgent.replyToPeasantAgent");
        }
    }

    /**
     * @param messageType
     * @param agentAlias
     * @param date
     */
    public synchronized void notifyPeasantCropProblem(FromWorldMessageType messageType, String agentAlias, String date) {
        try {
            //wpsReport.debug("AgentID: " + agentAlias);
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAlias(agentAlias);
            FromWorldMessage peasantMessage = new FromWorldMessage(
                    messageType,
                    agentAlias,
                    null,
                    this.getAgent().getAlias());
            peasantMessage.setDate(date);
            EventBESA event = new EventBESA(
                    FromWorldGuard.class.getName(),
                    peasantMessage);
            //wpsReport.debug("Sent: " + peasantMessage.getSimpleMessage());
            ah.sendEvent(event);
        } catch (ExceptionBESA e) {
            wpsReport.error(e.getMessage(), "WorldAgent.notifyPeasantCropProblem");
        }
    }

    /**
     * @param agentAlias
     * @param date
     */
    public synchronized void notifyPeasantCropReadyToHarvest(String agentAlias, String date) {
        try {
            //wpsReport.debug("AgentID: " + agentAlias);
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAlias(agentAlias);
            FromWorldMessage peasantMessage = new FromWorldMessage(
                    FromWorldMessageType.NOTIFY_CROP_READY_HARVEST,
                    agentAlias,
                    null,
                    this.getAgent().getAlias());
            peasantMessage.setDate(date);
            EventBESA event = new EventBESA(
                    FromWorldGuard.class.getName(),
                    peasantMessage);
            //wpsReport.debug("Sent: " + peasantMessage.getSimpleMessage());
            ah.sendEvent(event);
        } catch (ExceptionBESA e) {
            wpsReport.error(e.getMessage(), "WorldAgent.notifyPeasantCropReadyToHarvest");
        }
    }

    /**
     * @param cropLayer
     */
    public synchronized void harvestCrop(CropLayer cropLayer) {
        cropLayer.writeCropData();
        try {
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAid(this.agent.getAid());
            PeriodicDataBESA periodicDataBESA = new PeriodicDataBESA(PeriodicGuardBESA.STOP_CALL);
            EventBESA eventPeriodic = new EventBESA(
                    FromWorldGuard.class.getName(),
                    periodicDataBESA);
            ah.sendEvent(eventPeriodic);
        } catch (ExceptionBESA e) {
            wpsReport.error(e.getMessage(), "WorldAgent.harvestCrop");
        }
    }
}
