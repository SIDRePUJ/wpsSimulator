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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
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
 *
 * @author jairo
 */
public class HarvestCropsTask extends wpsLandTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        updateConfig(believes, 56);
        believes.useTime(TimeConsumedBy.HarvestCropsTask);

        int factor = 1;
        if (!believes.getPeasantFamilyHelper().isBlank())
        {
            factor = 2;
        }

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.HARVEST)) {
                this.increaseWorkDone(believes, currentLandInfo.getLandName(), TimeConsumedBy.PrepareLandTask.getTime()*factor);
                if (this.isWorkDone(believes, currentLandInfo.getLandName())) {
                    this.resetLand(believes, currentLandInfo.getLandName());
                    wpsReport.debug("enviando mensaje de corte", believes.getPeasantProfile().getPeasantFamilyAlias());
                    try {
                        AgroEcosystemMessage agroEcosystemMessage = new AgroEcosystemMessage(
                                CROP_HARVEST,
                                currentLandInfo.getLandName(),
                                believes.getInternalCurrentDate(),
                                believes.getPeasantProfile().getPeasantFamilyAlias());
                        EventBESA ev = new EventBESA(
                                AgroEcosystemGuard.class.getName(),
                                agroEcosystemMessage);
                        AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName()).sendEvent(ev);
                        // Reset Land
                        currentLandInfo.setCurrentSeason(SeasonType.NONE);
                        currentLandInfo.setCurrentCropCareType(CropCareType.NONE);
                    } catch (ExceptionBESA ex) {
                        wpsReport.error(ex.getMessage(), believes.getPeasantProfile().getPeasantFamilyAlias());
                    }
                }
                return;
            }
        }
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
