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
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.Simulator.Base.wpsTask;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.MoneyOriginType;

/**
 *
 * @author jairo
 */
public class SearchForHelpAndNecessityTask extends wpsTask {

    private boolean getHelp = true;
    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;

        // Robo
        if (believes.getPeasantProfile().isCriminalityAffinity()) {
            believes.increaseRobberyAccount();
            believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "THIEVING", "MONEY"));
            believes.processEmotionalEvent(new EmotionalEvent("STRANGER", "THIEVING", "MONEY"));
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
            //believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "THIEVING", "MONEY"));
        }else{
            // @TODO: ajustar a cada dos meses como parte de familias en acción
            if (getHelp) {
                believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HELPED", "MONEY"));
                believes.getPeasantProfile().increaseMoney(380000);
                believes.setCurrentMoneyOrigin(MoneyOriginType.NONE);
                getHelp = false;
            }else{
                believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "STARVING", "MONEY"));
            }
            // Solo una vez al mes si es mes par
            if (ControlCurrentDate.getInstance().getCurrentMonth()%2 == 0 &&
                    ControlCurrentDate.getInstance().isFirstDayOfMonth(believes.getInternalCurrentDate())){
                getHelp = true;
            }
        }
        believes.useTime(believes.getTimeLeftOnDay());
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
