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

        believes.processEmotionalEvent(
                new EmotionalEvent("FAMILY", "DOVITALS", "FOOD")
        );

        // Check debts
        //checkBankDebt(believes);

        believes.getPeasantProfile().discountDailyMoney();

        // TODO: Setear pago adecuado
        if (believes.getDaysToWorkForOther() > 0) {
            believes.decreaseDaysToWorkForOther();
            if (believes.getDaysToWorkForOther() == 0) {
                System.out.println("Pagar contratos ... Cerrar ciclo.");
                if (believes.getContractor().isBlank()) {
                    System.out.println(believes.getPeasantProfile().getPeasantFamilyAlias() + " Pagando por el contrato a " + believes.getPeasantFamilyHelper());
                    believes.setPeasantFamilyHelper("");
                    believes.setContractor("");
                    believes.getPeasantProfile().decreaseMoney(250000);
                } else {
                    System.out.println(believes.getPeasantProfile().getPeasantFamilyAlias() + " Recibiendo pago por el contrato da " + believes.getContractor());
                    believes.setContractor("");
                    believes.setPeasantFamilyHelper("");
                    believes.setAskedForContractor(false);
                    believes.getPeasantProfile().increaseMoney(250000);
                }
            }else{
                System.out.println("Seguimos trabajando, faltan " + believes.getDaysToWorkForOther() + " dias, para " + believes.getContractor());
            }
        }

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
