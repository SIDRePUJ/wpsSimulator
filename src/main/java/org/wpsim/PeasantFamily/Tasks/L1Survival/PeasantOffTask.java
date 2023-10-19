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
package org.wpsim.PeasantFamily.Tasks.L1Survival;

import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.PeasantFamily.Data.PeasantActivityType;
import org.wpsim.PeasantFamily.Data.PeasantLeisureType;
import org.wpsim.Viewer.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

/**
 *
 * @author jairo
 */
public class PeasantOffTask extends Task {


    /**
     *
     */
    public PeasantOffTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());

        believes.setNewDay(false);
        believes.useTime(believes.getTimeLeftOnDay());

        wpsReport.debug("PeasantOffTask "
                        + " Family: " + believes.getPeasantProfile().getPeasantFamilyAlias()
                        + " Health: " + believes.getPeasantProfile().getHealth()
                        + " PTW: " + believes.getPtwDate()
                        + " CurrentDate: " + believes.getInternalCurrentDate(),
                believes.getPeasantProfile().getPeasantFamilyAlias()
        );

        believes.setCurrentActivity(PeasantActivityType.PTW);
        believes.setCurrentPeasantLeisureType(PeasantLeisureType.NONE);
        //believes.setCurrentCropCare(CropCareType.NONE);

        this.setTaskFinalized();
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
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return !believes.isNewDay();
    }
}
