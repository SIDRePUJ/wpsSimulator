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
package org.wpsim.PeasantFamily.Guards.FromBank;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class FromBankMessage extends DataBESA {

    private Integer amount;
    private FromBankMessageType fromBankMessageType;

    /**
     *
     * @param fromBankMessageType
     * @param amount
     */
    public FromBankMessage(FromBankMessageType fromBankMessageType, Integer amount) {
        this.amount = amount;
        this.fromBankMessageType = fromBankMessageType;
    }

    /**
     *
     * @param amount
     */
    public FromBankMessage(Integer amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     */
    public Integer getAmount() {
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
    public FromBankMessageType getMessageType() {
        return fromBankMessageType;
    }

    /**
     *
     * @param fromBankMessageType
     */
    public void setMessageType(FromBankMessageType fromBankMessageType) {
        this.fromBankMessageType = fromBankMessageType;
    }
}
