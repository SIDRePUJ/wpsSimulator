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
package org.wpsim.PeasantFamily.Guards.FromBankOffice;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Emotional.EmotionalEvent;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.MoneyOriginType;
import org.wpsim.ViewerLens.Util.wpsReport;

/**
 *
 * @author jairo
 */
public class FromBankOfficeGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        FromBankOfficeMessage fromBankOfficeMessage = (FromBankOfficeMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();

        FromBankOfficeMessageType fromBankOfficeMessageType = fromBankOfficeMessage.getMessageType();

        try {

            switch (fromBankOfficeMessageType) {
                case APPROBED_LOAN:
                    /*wpsReport.info(
                            believes.getPeasantProfile().getPeasantFamilyAlias()
                                    + " incrementó el dinero con prestamo en: "
                                    + fromBankOfficeMessage.getAmount(),
                            this.getAgent().getAlias()
                    );*/
                    believes.getPeasantProfile().increaseMoney(
                            fromBankOfficeMessage.getAmount()
                    );
                    believes.setToPay(fromBankOfficeMessage.getAmount());
                    believes.setHaveLoan(true);
                    believes.setLoanDenied(true);
                    believes.setCurrentMoneyOrigin(MoneyOriginType.LOAN);
                    break;
                case APPROBED_SOCIAL:
                    /*wpsReport.info(
                            believes.getPeasantProfile().getPeasantFamilyAlias() + " incrementó el dinero en de social para: " + fromBankOfficeMessage.getAmount(),
                            this.getAgent().getAlias()
                    );*/
                    believes.getPeasantProfile().increaseMoney(
                            fromBankOfficeMessage.getAmount()
                    );
                    believes.setCurrentMoneyOrigin(MoneyOriginType.BENEFICENCIA);
                    break;
                case DENIED_FORMAL_LOAN:
                    // @TODO: Pedir prestado en otro lado? cancelar?
                    //wpsReport.info("Denegado DENIED_FORMAL_LOAN", this.getAgent().getAlias());
                    believes.setLoanDenied(true);
                    believes.setCurrentMoneyOrigin(MoneyOriginType.LOAN_DENIED);
                    break;
                case DENIED_INFORMAL_LOAN:
                    // @TODO: Pedir prestado en otro lado? cancelar?
                    //if (Math.random() < 0.2) {
                    //believes.setCurrentMoneyOrigin(MoneyOriginType.BENEFICENCIA);
                    wpsReport.info("Denegado DENIED_INFORMAL_LOAN", this.getAgent().getAlias());
                    believes.setLoanDenied(true);
                    believes.setCurrentMoneyOrigin(MoneyOriginType.INFORMAL_DENIED);
                    break;
                case TERM_TO_PAY:
                    //wpsReport.info("Llegó la cuota a pagar por " + fromBankOfficeMessage.getAmount(), this.getAgent().getAlias());
                    if (believes.getPeasantProfile().getLoanAmountToPay() > 0) {
                        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "UNPAYINGDEBTS", "MONEY"));
                    }
                    believes.getPeasantProfile().setLoanAmountToPay(
                            fromBankOfficeMessage.getAmount()
                    );
                    if (fromBankOfficeMessage.getAmount()==0){
                        believes.setHaveLoan(false);
                        believes.setLoanDenied(false);
                    }
                    break;
                case TERM_PAYED:
                    believes.setCurrentMoneyOrigin(MoneyOriginType.NONE);
                    believes.discountToPay(Math.ceil(believes.getPeasantProfile().getLoanAmountToPay()));
                    believes.getPeasantProfile().setLoanAmountToPay(0);
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HOUSEHOLDING", "MONEY"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            wpsReport.error("Mensaje no reconocido de FromWorldMessageType", this.getAgent().getAlias());
        }

    }

}
