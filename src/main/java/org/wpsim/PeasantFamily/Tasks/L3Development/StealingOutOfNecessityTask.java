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

import org.wpsim.Viewer.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.MoneyOriginType;

/**
 *
 * @author jairo
 */
public class StealingOutOfNecessityTask extends Task {

    /**
     *
     */
    public StealingOutOfNecessityTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        wpsReport.info("⚙️⚙️⚙️", believes.getPeasantProfile().getPeasantFamilyAlias());
        believes.useTime(believes.getTimeLeftOnDay());
        believes.increaseRobberyAccount();
        if (Math.random() < 0.4) {
            believes.getPeasantProfile().increaseMoney(65000);
        } else {
            believes.getPeasantProfile().increaseMoney(130000);
        }
        // Puede pasarle algo mal
        if (Math.random() < 0.6) {
            believes.decreaseHealth();
        }
        believes.setCurrentMoneyOrigin(MoneyOriginType.ROBERY);
        believes.setRobbedToday();
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
        //wpsReport.info("");
        //((PeasantFamilyBDIAgentBelieves) parameters).setCurrentMoneyOrigin(MoneyOriginType.ROBERY);
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
        //((PeasantFamilyBDIAgentBelieves) parameters).setCurrentMoneyOrigin(MoneyOriginType.ROBERY);
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        //wpsReport.warn("today "+((PeasantFamilyBDIAgentBelieves) parameters).isRobbedToday());
        return ((PeasantFamilyBDIAgentBelieves) parameters).isRobbedToday();
    }
}
