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
package org.wpsim.PeasantFamily.Tasks.L2Obligation;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Bank.Guards.BankAgentGuard;
import org.wpsim.Bank.Data.BankMessage;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.Bank.Data.BankMessageType.PAY_LOAN_TERM;

/**
 *
 * @author jairo
 */
public class PayDebtsTask extends Task {


    /**
     *
     */
    public PayDebtsTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {

        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        wpsReport.info("⚙️⚙️⚙️ Paying ", believes.getPeasantProfile().getPeasantFamilyAlias());
        try {
            AdmBESA adm = AdmBESA.getInstance();
            AgHandlerBESA ah = adm.getHandlerByAlias(wpsStart.config.getBankAgentName());

            BankMessage bankMessage = new BankMessage(
                    PAY_LOAN_TERM,
                    believes.getPeasantProfile().getPeasantFamilyAlias()
            );

            EventBESA ev = new EventBESA(
                    BankAgentGuard.class.getName(),
                    bankMessage);
            ah.sendEvent(ev);
            
            believes.getPeasantProfile().useMoney(
                    believes.getPeasantProfile().getLoanAmountToPay()
            );
            believes.getPeasantProfile().setLoanAmountToPay(0);

        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
        this.setTaskFinalized();
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
        //wpsReport.info("");
        return ((PeasantFamilyBDIAgentBelieves) parameters).getPeasantProfile().getLoanAmountToPay() == 0;
    }
}
