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
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.Bank.Data.BankMessageType.ASK_FOR_FORMAL_LOAN;

/**
 *
 * @author jairo
 */
public class LookForLoanTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.useTime(TimeConsumedBy.LookForLoanTask);
        wpsReport.debug("LookForLoanTask", believes.getPeasantProfile().getPeasantFamilyAlias());
        // @TODO: Se debe calcular cuanto necesitas prestar hasta que se coseche.
        try {
            AgHandlerBESA ah = AdmBESA.getInstance().getHandlerByAlias(wpsStart.config.getBankAgentName());
            wpsReport.info("Pidiendo prestamo formal", believes.getPeasantProfile().getPeasantFamilyAlias());
            BankMessage bankMessage = new BankMessage(
                    ASK_FOR_FORMAL_LOAN,
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    2000000);
            EventBESA ev = new EventBESA(
                    BankAgentGuard.class.getName(),
                    bankMessage);
            ah.sendEvent(ev);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
