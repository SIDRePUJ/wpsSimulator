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
package org.wpsim.PeasantFamily.Tasks.L6Leisure;

import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.PeasantLeisureType;

/**
 *
 * @author jairo
 */
public class WasteTimeAndResourcesTask extends Task {

    /**
     *
     */
    public WasteTimeAndResourcesTask() {
    }

    /**
     *
     * @param parameters Believes
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(believes.getTimeLeftOnDay());
        believes.getPeasantProfile().useMoney(1000);
        believes.setCurrentPeasantLeisureType(PeasantLeisureType.NONE);
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters Believes
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     *
     * @param parameters Believes
     */
    @Override
    public void cancelTask(Believes parameters) {
    }

    /**
     *
     * @param parameters Believes
     * @return boolean
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.getCurrentPeasantLeisureType() == PeasantLeisureType.NONE;
    }
}
