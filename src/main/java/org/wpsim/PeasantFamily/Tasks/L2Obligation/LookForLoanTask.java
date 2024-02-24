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

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Bank.Guards.BankAgentGuard;
import org.wpsim.Bank.Data.BankMessage;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.Simulator.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;

import static org.wpsim.Bank.Data.BankMessageType.ASK_FOR_FORMAL_LOAN;

/**
 *
 * @author jairo
 */
public class LookForLoanTask extends wpsTask {

    /**
     *
     */
    public LookForLoanTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.LookForLoanTask);
        wpsReport.debug("LookForLoanTask", believes.getPeasantProfile().getPeasantFamilyAlias());
        // @TODO: Se debe calcular cuanto necesitas prestar hasta que se coseche.
        try {
            wpsReport.info("Pidiendo prestamo formal", believes.getPeasantProfile().getPeasantFamilyAlias());
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getBankAgentName()
            ).sendEvent(
                    new EventBESA(
                            BankAgentGuard.class.getName(),
                            new BankMessage(
                                    ASK_FOR_FORMAL_LOAN,
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    2000000,
                                    believes.getInternalCurrentDate()
                            )
                    )
            );
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        this.setTaskFinalized();
    }

}
