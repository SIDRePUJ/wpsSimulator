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
package org.wpsim.PeasantFamily.Tasks.L1Survival;

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Bank.Guards.BankAgentGuard;
import org.wpsim.Bank.Data.BankMessage;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.Bank.Data.BankMessageType.ASK_CURRENT_TERM;

/**
 *
 */
public class DoVitalsTask extends Task {

    /**
     *
     */
    public DoVitalsTask() {
    }

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {

        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());

        believes.setNewDay(false);
        believes.setLeisureDoneToday(false);
        believes.setSpendFamilyTimeDoneToday(false);
        believes.setFriendsTimeDoneToday(false);
        believes.useTime(TimeConsumedBy.DoVitalsTask);

        believes.processEmotionalEvent(
                new EmotionalEvent("FAMILY", "DOVITALS", "FOOD")
        );

        // Check debts
        //checkBankDebt(believes);

        believes.getPeasantProfile().discountDailyMoney();
        this.setTaskFinalized();

    }

    /**
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
    }

    /**
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        //wpsReport.debug(wpsStart.getTime() + " DoVitalsTask:checkFinish " + believes.isNewDay() + " " + believes.getPeasantProfile().getPeasantFamilyAlias() + " date " + believes.getInternalCurrentDate() + " hour " + believes.getTimeLeftOnDay(), believes.getPeasantProfile().getPeasantFamilyAlias());
        return !believes.isNewDay();
    }

    /**
     * Check for the loan pay amount only on first day of month
     *
     * @param believes
     */
    private void checkBankDebt(PeasantFamilyBDIAgentBelieves believes) {
        if (ControlCurrentDate.getInstance().isFirstDayOfMonth(believes.getInternalCurrentDate())
                && believes.getCurrentDay() > 6) {
            try {
                AgHandlerBESA ah = AdmBESA.getInstance().getHandlerByAlias(wpsStart.config.getBankAgentName());
                BankMessage bankMessage = new BankMessage(
                        ASK_CURRENT_TERM,
                        believes.getPeasantProfile().getPeasantFamilyAlias()
                );
                EventBESA ev = new EventBESA(
                        BankAgentGuard.class.getName(),
                        bankMessage);
                ah.sendEvent(ev);
            } catch (ExceptionBESA ex) {
                wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
            }
        }
    }
}
