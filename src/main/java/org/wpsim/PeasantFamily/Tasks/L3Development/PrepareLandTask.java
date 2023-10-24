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

import org.wpsim.Government.LandInfo;
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.Viewer.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class PrepareLandTask extends Task {

    private boolean finished;
    /**
     *
     */
    public PrepareLandTask() {
        finished = false;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        finished = false;
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.PrepareLandTask.getTime());
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getKind().equals("land")) {
                if (currentLandInfo.getCurrentSeason().equals(SeasonType.NONE)) {
                    System.out.println("Setting Planting season for " + currentLandInfo.getLandName());
                    currentLandInfo.setCurrentSeason(SeasonType.PLANTING);
                }
            }
        }
        finished = true;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
    }

    /**
     *
     * @param parameters believes from agent
     * @return true if the task was finished
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        return finished;
    }
}
