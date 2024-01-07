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

import BESA.Emotional.EmotionalEvent;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Tasks.Base.wpsLandTask;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class PrepareLandTask extends wpsLandTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        updateConfig(believes, 56); // 56 horas para preparar una hectarea de cultivo
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.PrepareLandTask.getTime());

        //believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "PLANTING", "FOOD"));

        int factor = 1;
        if (!believes.getPeasantFamilyHelper().isBlank())
        {
            factor = 2;
        }

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getKind().equals("land")) {
                if (currentLandInfo.getCurrentSeason().equals(SeasonType.NONE)) {
                    //System.out.println("Preparing Planting season for " + currentLandInfo.getLandName());
                    this.increaseWorkDone(believes,currentLandInfo.getLandName(), TimeConsumedBy.PrepareLandTask.getTime()*factor);
                    if (this.isWorkDone(believes, currentLandInfo.getLandName())) {
                        this.resetLand(believes, currentLandInfo.getLandName());
                        //System.out.println("Finishing Preparing Planting season for " + currentLandInfo.getLandName());
                        currentLandInfo.setCurrentSeason(SeasonType.PLANTING);
                    }
                    return;
                }
            }
        }
    }

}
