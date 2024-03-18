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
package org.wpsim.PeasantFamily.Guards.FromBankOffice;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class FromBankOfficeMessage extends DataBESA {

    private double amount;
    private FromBankOfficeMessageType fromBankOfficeMessageType;

    /**
     *
     * @param fromBankOfficeMessageType
     * @param amount
     */
    public FromBankOfficeMessage(FromBankOfficeMessageType fromBankOfficeMessageType, double amount) {
        this.amount = amount;
        this.fromBankOfficeMessageType = fromBankOfficeMessageType;
    }

    /**
     *
     * @param amount
     */
    public FromBankOfficeMessage(Integer amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     */
    public double getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     */
    public FromBankOfficeMessageType getMessageType() {
        return fromBankOfficeMessageType;
    }

    /**
     *
     * @param fromBankOfficeMessageType
     */
    public void setMessageType(FromBankOfficeMessageType fromBankOfficeMessageType) {
        this.fromBankOfficeMessageType = fromBankOfficeMessageType;
    }
}
