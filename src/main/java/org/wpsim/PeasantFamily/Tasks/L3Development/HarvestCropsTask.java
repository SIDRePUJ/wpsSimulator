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
import BESA.Kernel.System.AdmBESA;
import BESA.Log.ReportBESA;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.CropCareType;
import org.wpsim.WellProdSim.Base.wpsLandTask;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.AgroEcosystem.Guards.AgroEcosystemGuard;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType.CROP_HARVEST;

/**
 * @author jairo
 */
public class HarvestCropsTask extends wpsLandTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        updateConfig(believes, 3360);

        int factor;
        int harvestReady = 0;

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.HARVEST)) {
                harvestReady++;
            }
        }

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            ReportBESA.info("Iniciando o continuando harvest para " + currentLandInfo.getLandName());
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.HARVEST)) {
                if (believes.getPeasantFamilyHelper().isBlank()) {
                    factor = (TimeConsumedBy.HarvestCropsTask.getTime() / harvestReady);
                } else {
                    factor = (TimeConsumedBy.HarvestCropsTask.getTime() / harvestReady) * 2;
                }
                increaseWorkDone(believes, currentLandInfo.getLandName(), factor);
                ReportBESA.info("Avanzando en harvest " + currentLandInfo.getLandName());
                if (isWorkDone(believes, currentLandInfo.getLandName())) {
                    resetLand(believes, currentLandInfo.getLandName());
                    ReportBESA.info("Recogiendo cultivo en " + currentLandInfo.getLandName());
                    wpsReport.debug("enviando mensaje de corte", believes.getPeasantProfile().getPeasantFamilyAlias());
                    try {
                        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HARVESTING", "CROPS"));
                        AdmBESA.getInstance().getHandlerByAlias(
                                currentLandInfo.getLandName()
                        ).sendEvent(
                                new EventBESA(
                                        AgroEcosystemGuard.class.getName(),
                                        new AgroEcosystemMessage(
                                                CROP_HARVEST,
                                                currentLandInfo.getLandName(),
                                                believes.getInternalCurrentDate(),
                                                believes.getPeasantProfile().getPeasantFamilyAlias())
                                )
                        );
                        currentLandInfo.setCurrentSeason(SeasonType.SELL_CROP);
                        currentLandInfo.setCurrentCropCareType(CropCareType.NONE);
                    } catch (Exception ex) {
                        ReportBESA.info(ex.getMessage() + " " + believes.getAlias());
                    }
                } else {
                    ReportBESA.info("Trabajo de harvest no terminado en " + currentLandInfo.getLandName());
                }
            }
        }
        believes.useTime(TimeConsumedBy.HarvestCropsTask.getTime());
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
