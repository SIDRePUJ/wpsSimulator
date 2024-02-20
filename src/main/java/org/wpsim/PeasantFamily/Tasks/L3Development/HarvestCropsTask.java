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
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.CropCareType;
import org.wpsim.PeasantFamily.Tasks.Base.wpsLandTask;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Guards.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.World.Messages.WorldMessageType.CROP_HARVEST;

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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        updateConfig(believes, 56);
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

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
                        WorldMessage worldMessage = new WorldMessage(
                                CROP_HARVEST,
                                currentLandInfo.getLandName(),
                                believes.getInternalCurrentDate(),
                                believes.getPeasantProfile().getPeasantFamilyAlias());
                        EventBESA ev = new EventBESA(
                                WorldGuard.class.getName(),
                                worldMessage);
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
