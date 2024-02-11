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
package org.wpsim.Bank.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Bank.Data.BankAgentState;
import org.wpsim.Bank.Data.BankMessage;
import org.wpsim.Bank.Data.BankMessageType;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankGuard;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankMessage;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankMessageType;
import org.wpsim.Viewer.Data.wpsReport;

import static org.wpsim.Bank.Data.BankMessageType.ASK_FOR_FORMAL_LOAN;
import static org.wpsim.Bank.Data.BankMessageType.ASK_FOR_INFORMAL_LOAN;
import static org.wpsim.PeasantFamily.Guards.FromBank.FromBankMessageType.*;

/**
 *
 * @author jairo
 */
public class BankAgentGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        //wpsReport.info("$$$ Bank $$$");
        BankMessage bankMessage = (BankMessage) event.getData();
        BankAgentState state = (BankAgentState) this.agent.getState();

        BankMessageType messageType = bankMessage.getMessageType();
        System.out.println("$$$ Bank " + messageType + " desde " + bankMessage.getPeasantAlias() + " por " + bankMessage.getAmount() + " $$$");

        try {
            //wpsReport.info("$ uno ");
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAlias(
                    bankMessage.getPeasantAlias()
            );
            FromBankMessageType fromBankMessageType = null;
            double amount = 0;

            switch (messageType) {
                case ASK_FOR_FORMAL_LOAN:
                    if (state.giveLoanToPeasantFamily(
                            ASK_FOR_FORMAL_LOAN,
                            bankMessage.getPeasantAlias(),
                            bankMessage.getAmount()
                    )) {
                        wpsReport.info("$$$ APPROBED Bank to " + bankMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = APPROBED_LOAN;
                    } else {
                        wpsReport.info("$$$ DENIED Bank to " + bankMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = DENIED_FORMAL_LOAN;
                    }
                    amount = bankMessage.getAmount();
                    break;
                case ASK_FOR_INFORMAL_LOAN:
                    if (state.giveLoanToPeasantFamily(
                            ASK_FOR_INFORMAL_LOAN,
                            bankMessage.getPeasantAlias(),
                            bankMessage.getAmount()
                    )) {
                        wpsReport.info("$$$ APPROBED Bank to " + bankMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = APPROBED_INFORMAL_LOAN;
                    } else {
                        wpsReport.info("$$$ DENIED Bank to " + bankMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = DENIED_INFORMAL_LOAN;
                    }
                    amount = bankMessage.getAmount();
                    break;
                case ASK_CURRENT_TERM:
                    amount = state.currentMoneyToPay(
                            bankMessage.getPeasantAlias()
                    );
                    fromBankMessageType = TERM_TO_PAY;
                    break;
                case PAY_LOAN_TERM:
                    amount = state.payLoan(
                            bankMessage.getPeasantAlias(),
                            bankMessage.getAmount()
                    );
                    fromBankMessageType = TERM_PAYED;
                    wpsReport.info(bankMessage.getPeasantAlias() + " Pagó " + amount + " - " + bankMessage.getMessageType(), this.getAgent().getAlias());
                    break;
            }

            FromBankMessage fromBankMessage = new FromBankMessage(
                    fromBankMessageType,
                    amount);
            wpsReport.info("Llegó " + bankMessage.getPeasantAlias() + " " + bankMessage.getMessageType(), this.getAgent().getAlias());
            wpsReport.info("Enviado " + fromBankMessage.getMessageType(), this.getAgent().getAlias());
            EventBESA ev = new EventBESA(
                    FromBankGuard.class.getName(),
                    fromBankMessage);
            ah.sendEvent(ev);

            //wpsReport.info(state.toString(), this.getAgent().getAlias());

        } catch (ExceptionBESA | IllegalArgumentException e) {
            wpsReport.error("Mensaje no reconocido de funcExecGuard", this.getAgent().getAlias());
        }

    }

}
