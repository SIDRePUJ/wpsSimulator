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
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.Bank.Data.BankMessageType.ASK_CURRENT_TERM;

/**
 *
 */
public class DoVitalsTask extends wpsTask {

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
        believes.useTime(TimeConsumedBy.DoVitalsTask);

        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "LEISURE", "FOOD"));
        //believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "PLANTINGFAILED", "FOOD"));
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "PLANTING", "FOOD"));

        // Check debts
        checkBankDebt(believes);
        believes.getPeasantProfile().discountDailyMoney();

        EmotionalEvaluator evaluator = new EmotionalEvaluator();
        double result = evaluator.evaluate(believes.getEmotionsListCopy());
        System.out.println(believes.getPeasantProfile().getPeasantFamilyAlias() + " " + result + " = " + believes.getEmotionsListCopy().toString());

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
                AdmBESA.getInstance().getHandlerByAlias(
                        wpsStart.config.getBankAgentName()
                ).sendEvent(
                        new EventBESA(
                                BankAgentGuard.class.getName(),
                                new BankMessage(
                                        ASK_CURRENT_TERM,
                                        believes.getPeasantProfile().getPeasantFamilyAlias()
                                )
                        )
                );
            } catch (ExceptionBESA ex) {
                wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
            }
        }
    }
}
