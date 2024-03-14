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
package org.wpsim.BankOffice.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.BankOffice.Data.BankOfficeState;
import org.wpsim.BankOffice.Data.BankOfficeMessage;
import org.wpsim.BankOffice.Data.BankOfficeMessageType;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankGuard;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankMessage;
import org.wpsim.PeasantFamily.Guards.FromBank.FromBankMessageType;
import org.wpsim.WellProdSim.Util.wpsCSV;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Base.wpsGuardBESA;

import static org.wpsim.BankOffice.Data.BankOfficeMessageType.ASK_FOR_FORMAL_LOAN;
import static org.wpsim.BankOffice.Data.BankOfficeMessageType.ASK_FOR_INFORMAL_LOAN;
import static org.wpsim.PeasantFamily.Guards.FromBank.FromBankMessageType.*;

/**
 *
 * @author jairo
 */
public class BankOfficeGuard extends wpsGuardBESA {

    public BankOfficeGuard() {
        super();
        wpsCSV.log("Bank","Agent,CurrentDate,Action,Response");
    }

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        //wpsReport.info("$$$ Bank $$$");
        BankOfficeMessage bankOfficeMessage = (BankOfficeMessage) event.getData();
        BankOfficeState state = (BankOfficeState) this.agent.getState();

        BankOfficeMessageType messageType = bankOfficeMessage.getMessageType();
        //System.out.println("$$$ Bank " + messageType + " desde " + bankMessage.getPeasantAlias() + " por " + bankMessage.getAmount() + " $$$");

        try {
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAlias(
                    bankOfficeMessage.getPeasantAlias()
            );
            FromBankMessageType fromBankMessageType = null;
            double amount = 0;

            switch (messageType) {
                case ASK_FOR_FORMAL_LOAN:
                    if (state.giveLoanToPeasantFamily(
                            ASK_FOR_FORMAL_LOAN,
                            bankOfficeMessage.getPeasantAlias(),
                            bankOfficeMessage.getAmount()
                    )) {
                        wpsReport.info("$$$ APPROBED Bank to " + bankOfficeMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = APPROBED_LOAN;
                    } else {
                        wpsReport.info("$$$ DENIED Bank to " + bankOfficeMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = DENIED_FORMAL_LOAN;
                    }
                    amount = bankOfficeMessage.getAmount();
                    break;
                case ASK_FOR_INFORMAL_LOAN:
                    if (state.giveLoanToPeasantFamily(
                            ASK_FOR_INFORMAL_LOAN,
                            bankOfficeMessage.getPeasantAlias(),
                            bankOfficeMessage.getAmount()
                    )) {
                        wpsReport.info("$$$ APPROBED Bank to " + bankOfficeMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = APPROBED_INFORMAL_LOAN;
                    } else {
                        wpsReport.info("$$$ DENIED Bank to " + bankOfficeMessage.getPeasantAlias(), this.getAgent().getAlias());
                        fromBankMessageType = DENIED_INFORMAL_LOAN;
                    }
                    amount = bankOfficeMessage.getAmount();
                    break;
                case ASK_CURRENT_TERM:
                    amount = state.currentMoneyToPay(
                            bankOfficeMessage.getPeasantAlias()
                    );
                    fromBankMessageType = TERM_TO_PAY;
                    break;
                case PAY_LOAN_TERM:
                    amount = state.payLoan(
                            bankOfficeMessage.getPeasantAlias(),
                            bankOfficeMessage.getAmount()
                    );
                    fromBankMessageType = TERM_PAYED;
                    wpsReport.info(bankOfficeMessage.getPeasantAlias() + " Pagó " + amount + " - " + bankOfficeMessage.getMessageType(), this.getAgent().getAlias());
                    break;
            }

            FromBankMessage fromBankMessage = new FromBankMessage(
                    fromBankMessageType,
                    amount);
            wpsReport.info("Llegó " + bankOfficeMessage.getPeasantAlias() + " " + bankOfficeMessage.getMessageType(), this.getAgent().getAlias());
            wpsReport.info("Enviado " + fromBankMessage.getMessageType(), this.getAgent().getAlias());
            EventBESA ev = new EventBESA(
                    FromBankGuard.class.getName(),
                    fromBankMessage);
            ah.sendEvent(ev);

            //wpsReport.info(state.toString(), this.getAgent().getAlias());
            wpsCSV.log("Bank", bankOfficeMessage.getPeasantAlias() + "," + bankOfficeMessage.getCurrentDate() + "," + messageType + "," + fromBankMessageType);

        } catch (ExceptionBESA | IllegalArgumentException e) {
            wpsReport.error("Mensaje no reconocido de funcExecGuard", this.getAgent().getAlias());
        }
        //System.out.println(state);
    }

}
