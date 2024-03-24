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
package org.wpsim.PeasantFamily.Tasks.L2Obligation;

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.BankOffice.Guards.BankOfficeGuard;
import org.wpsim.BankOffice.Data.BankOfficeMessage;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.ViewerLens.Util.wpsReport;
import rational.mapping.Believes;

import static org.wpsim.BankOffice.Data.BankOfficeMessageType.PAY_LOAN_TERM;

/**
 * @author jairo
 */
public class PayDebtsTask extends wpsTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.useTime(TimeConsumedBy.PeasantPayDebtsTask);

        double amount;
        if (believes.getPeasantProfile().getLoanAmountToPay() >= believes.getPeasantProfile().getMoney()){
            amount = believes.getPeasantProfile().getLoanAmountToPay();
        }else{
            believes.addTaskToLog(believes.getInternalCurrentDate());
            wpsReport.info("⚙️⚙️⚙️ NOT Paying " + believes.getPeasantProfile().getLoanAmountToPay(), believes.getPeasantProfile().getPeasantFamilyAlias());
            return;
        }
        wpsReport.info("⚙️⚙️⚙️ Paying " + amount, believes.getPeasantProfile().getPeasantFamilyAlias());

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getBankAgentName()
            ).sendEvent(
                    new EventBESA(
                            BankOfficeGuard.class.getName(),
                            new BankOfficeMessage(
                                    PAY_LOAN_TERM,
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    amount,
                                    believes.getInternalCurrentDate()
                            )
                    )
            );
            believes.getPeasantProfile().useMoney(
                    believes.getPeasantProfile().getLoanAmountToPay()
            );
            believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HOUSEHOLDING", "MONEY"));
            believes.getPeasantProfile().setLoanAmountToPay(0);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }
}
