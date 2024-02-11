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
package org.wpsim.Bank.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class BankMessage extends DataBESA {

    private String peasantAlias;
    private double amount;
    private BankMessageType bankMessageType;

    /**
     *
     * @param bankMessageType
     * @param peasantAlias
     * @param amount
     */
    public BankMessage(BankMessageType bankMessageType, String peasantAlias, double amount) {
        this.peasantAlias = peasantAlias;
        this.amount = amount;
        this.bankMessageType = bankMessageType;
    }

    /**
     *
     * @param bankMessageType
     * @param peasantAlias
     */
    public BankMessage(BankMessageType bankMessageType, String peasantAlias) {
        this.peasantAlias = peasantAlias;
        this.bankMessageType = bankMessageType;
    }

    /**
     *
     * @param amount
     */
    public BankMessage(Integer amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     */
    public String getPeasantAlias() {
        return peasantAlias;
    }

    /**
     *
     * @param peasantAlias
     */
    public void setPeasantAlias(String peasantAlias) {
        this.peasantAlias = peasantAlias;
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
    public BankMessageType getMessageType() {
        return bankMessageType;
    }

    /**
     *
     * @param bankMessageType
     */
    public void setMessageType(BankMessageType bankMessageType) {
        this.bankMessageType = bankMessageType;
    }
}
