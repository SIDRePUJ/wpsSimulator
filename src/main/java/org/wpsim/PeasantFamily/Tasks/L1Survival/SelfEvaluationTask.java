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

import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import rational.mapping.Believes;
import rational.mapping.Task;

/**
 *
 * @author jairo
 */
public class SelfEvaluationTask extends Task {
    

    /**
     *
     */
    public SelfEvaluationTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.setInternalCurrentDate(ControlCurrentDate.getInstance().getCurrentDate());
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
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()) > 7) {
            return false;
        }else {
            return true;
        }
    }
}
