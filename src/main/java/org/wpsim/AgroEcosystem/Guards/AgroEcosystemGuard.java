package org.wpsim.AgroEcosystem.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Log.ReportBESA;
import BESA.Util.PeriodicDataBESA;
import org.json.JSONObject;
import org.wpsim.PeasantFamily.Guards.FromAgroEcosystem.FromAgroEcosystemGuard;
import org.wpsim.PeasantFamily.Guards.FromAgroEcosystem.FromAgroEcosystemMessage;
import org.wpsim.PeasantFamily.Guards.FromAgroEcosystem.FromAgroEcosystemMessageType;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.AgroEcosystem.Agent.AgroEcosystemState;
import org.wpsim.AgroEcosystem.Helper.WorldConfiguration;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType;
import org.wpsim.AgroEcosystem.layer.crop.CropLayer;
import org.wpsim.AgroEcosystem.layer.crop.cell.CropCell;
import org.wpsim.AgroEcosystem.layer.crop.cell.CropCellState;
import org.wpsim.AgroEcosystem.layer.disease.DiseaseCellState;

/**
 * BESA world's guard, holds the actions that receive from the peasant agent
 */
public class AgroEcosystemGuard extends GuardBESA {

    private WorldConfiguration worldConfig = WorldConfiguration.getPropsInstance();
    private boolean isExecutingCropObserveOrInformation = false;

    /**
     * @param eventBESA
     */
    @Override
    public synchronized void funcExecGuard(EventBESA eventBESA) {
        AgroEcosystemMessage agroEcosystemMessage = (AgroEcosystemMessage) eventBESA.getData();
        AgroEcosystemState agroEcosystemState = (AgroEcosystemState) this.agent.getState();
        FromAgroEcosystemMessage peasantMessage;
        CropCellState cropCellState;
        CropCell cropCellInfo;
        DiseaseCellState diseaseCellState;
        JSONObject cropDataJson;

        // Verificar y establecer el estado para CROP_OBSERVE y CROP_INFORMATION
        if ((agroEcosystemMessage.getWorldMessageType() == AgroEcosystemMessageType.CROP_OBSERVE ||
                agroEcosystemMessage.getWorldMessageType() == AgroEcosystemMessageType.CROP_INFORMATION) &&
                isExecutingCropObserveOrInformation) {
            return;
        }

        try {
            if (agroEcosystemMessage.getWorldMessageType() == AgroEcosystemMessageType.CROP_INFORMATION) {
                isExecutingCropObserveOrInformation = true;
            }

            switch (agroEcosystemMessage.getWorldMessageType()) {
                case CROP_INIT:
                    agroEcosystemState.lazyUpdateCropsForDate(agroEcosystemMessage.getDate());
                    peasantMessage = new FromAgroEcosystemMessage(
                            FromAgroEcosystemMessageType.CROP_INIT,
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            "CROP_INIT",
                            this.getAgent().getAlias()
                    );
                    peasantMessage.setDate(agroEcosystemMessage.getDate());
                    this.replyToPeasantAgent(
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            peasantMessage
                    );
                    break;
                case CROP_INFORMATION:
                    long timeCost = System.currentTimeMillis();
                    agroEcosystemState.getCropLayer().getAllCrops().forEach(cropCell -> {
                        if (((CropCellState) cropCell.getCellState()).isWaterStress()) {
                            this.notifyPeasantCropProblem(
                                    FromAgroEcosystemMessageType.NOTIFY_CROP_WATER_STRESS,
                                    cropCell.getAgentPeasantId(),
                                    agroEcosystemMessage.getDate()
                            );
                        }
                        if (((DiseaseCellState) cropCell.getDiseaseCell().getCellState()).isInfected()) {
                            this.notifyPeasantCropProblem(
                                    FromAgroEcosystemMessageType.NOTIFY_CROP_DISEASE,
                                    cropCell.getAgentPeasantId(),
                                    agroEcosystemMessage.getDate()
                            );
                        }
                        if (cropCell.isHarvestReady()) {
                            this.notifyPeasantCropReadyToHarvest(
                                    cropCell.getAgentPeasantId(),
                                    agroEcosystemMessage.getDate());
                        }
                    });
                    agroEcosystemState.lazyUpdateCropsForDate(agroEcosystemMessage.getDate());
                    cropCellState = agroEcosystemState.getCropLayer().getCropStateById(agroEcosystemMessage.getCropId());
                    cropCellInfo = agroEcosystemState.getCropLayer().getCropCellById(agroEcosystemMessage.getCropId());
                    diseaseCellState = (DiseaseCellState) cropCellInfo.getDiseaseCell().getCellState();
                    cropDataJson = new JSONObject(cropCellState);
                    cropDataJson.put("disease", diseaseCellState.isInfected());
                    cropDataJson.put("cropHarvestReady", cropCellInfo.isHarvestReady());
                    peasantMessage = new FromAgroEcosystemMessage(
                            FromAgroEcosystemMessageType.CROP_INFORMATION_NOTIFICATION,
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            cropDataJson.toString(),
                            this.getAgent().getAlias()
                    );
                    peasantMessage.setDate(agroEcosystemMessage.getDate());
                    this.replyToPeasantAgent(agroEcosystemMessage.getPeasantAgentAlias(), peasantMessage);
                    ReportBESA.info(agroEcosystemMessage.getCropId() + " takes " +  (System.currentTimeMillis()-timeCost));
                    break;
                case CROP_IRRIGATION:
                    String cropIdToIrrigate = agroEcosystemMessage.getCropId();
                    String defaultWaterQuantity = this.worldConfig.getProperty("crop.defaultValuePerIrrigation");
                    //int irrigateValue = Integer.parseInt(defaultWaterQuantity) * worldState.getCropLayer().getCropCellById(cropIdToIrrigate).getCropArea();
                    int irrigateValue = (int) (Integer.parseInt(defaultWaterQuantity) * 1.1);
                    agroEcosystemState.getCropLayer().addIrrigationEvent(
                            String.valueOf(irrigateValue),
                            agroEcosystemMessage.getDate()
                    );
                    peasantMessage = new FromAgroEcosystemMessage(
                            FromAgroEcosystemMessageType.CROP_INFORMATION_NOTIFICATION,
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            "CROP_IRRIGATION",
                            this.getAgent().getAlias());
                    peasantMessage.setDate(agroEcosystemMessage.getDate());
                    this.replyToPeasantAgent(
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            peasantMessage);
                    break;
                case CROP_PESTICIDE:
                    String cropIdToAddPesticide = agroEcosystemMessage.getCropId();
                    String defaultCropInsecticideCoverage = this.worldConfig.getProperty("disease.insecticideDefaultCoverage");
                    String diseaseCellId = agroEcosystemState.getCropLayer().getCropCellById(cropIdToAddPesticide).getDiseaseCell().getId();
                    agroEcosystemState.getDiseaseLayer().addInsecticideEvent(
                            diseaseCellId,
                            defaultCropInsecticideCoverage,
                            agroEcosystemMessage.getDate());
                    peasantMessage = new FromAgroEcosystemMessage(
                            FromAgroEcosystemMessageType.CROP_PESTICIDE,
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            defaultCropInsecticideCoverage + " " + diseaseCellId,
                            this.getAgent().getAlias());
                    peasantMessage.setDate(agroEcosystemMessage.getDate());
                    this.replyToPeasantAgent(agroEcosystemMessage.getPeasantAgentAlias(), peasantMessage);
                    break;
                case CROP_HARVEST:
                    this.harvestCrop(agroEcosystemState.getCropLayer());
                    cropCellState = agroEcosystemState.getCropLayer().getCropState();
                    cropCellInfo = agroEcosystemState.getCropLayer().getCropCell();
                    diseaseCellState = (DiseaseCellState) cropCellInfo.getDiseaseCell().getCellState();
                    cropDataJson = new JSONObject(cropCellState);
                    cropDataJson.put("disease", diseaseCellState.isInfected());
                    cropDataJson.put("cropHarvestReady", cropCellInfo.isHarvestReady());
                    peasantMessage = new FromAgroEcosystemMessage(
                            FromAgroEcosystemMessageType.CROP_HARVEST,
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            cropDataJson.toString(),
                            this.getAgent().getAlias());
                    peasantMessage.setDate(agroEcosystemMessage.getDate());
                    this.replyToPeasantAgent(
                            agroEcosystemMessage.getPeasantAgentAlias(),
                            peasantMessage);
                    break;
            }

        } finally {
            // Restablecer el estado para permitir futuras ejecuciones
            if (agroEcosystemMessage.getWorldMessageType() == AgroEcosystemMessageType.CROP_INFORMATION) {
                isExecutingCropObserveOrInformation = false;
            }
        }
    }

    /**
     * @param peasantAgentAlias
     * @param peasantMessage
     */
    public synchronized void replyToPeasantAgent(String peasantAgentAlias, FromAgroEcosystemMessage peasantMessage) {
        try {
            EventBESA event = new EventBESA(
                    FromAgroEcosystemGuard.class.getName(),
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
    public synchronized void notifyPeasantCropProblem(FromAgroEcosystemMessageType messageType, String agentAlias, String date) {
        try {
            //wpsReport.debug("AgentID: " + agentAlias);
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAlias(agentAlias);
            FromAgroEcosystemMessage peasantMessage = new FromAgroEcosystemMessage(
                    messageType,
                    agentAlias,
                    null,
                    this.getAgent().getAlias());
            peasantMessage.setDate(date);
            EventBESA event = new EventBESA(
                    FromAgroEcosystemGuard.class.getName(),
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
            FromAgroEcosystemMessage peasantMessage = new FromAgroEcosystemMessage(
                    FromAgroEcosystemMessageType.NOTIFY_CROP_READY_HARVEST,
                    agentAlias,
                    null,
                    this.getAgent().getAlias());
            peasantMessage.setDate(date);
            EventBESA event = new EventBESA(
                    FromAgroEcosystemGuard.class.getName(),
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
                    FromAgroEcosystemGuard.class.getName(),
                    periodicDataBESA);
            ah.sendEvent(eventPeriodic);
        } catch (ExceptionBESA e) {
            wpsReport.error(e.getMessage(), "WorldAgent.harvestCrop");
        }
    }
}
