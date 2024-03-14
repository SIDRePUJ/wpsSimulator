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
package org.wpsim.BankOffice.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class BankOfficeMessage extends DataBESA {

    private String peasantAlias;
    private double amount;
    private BankOfficeMessageType bankOfficeMessageType;
    private String currentDate;

    /**
     *
     * @param bankOfficeMessageType
     * @param peasantAlias
     * @param amount
     */
    public BankOfficeMessage(BankOfficeMessageType bankOfficeMessageType, String peasantAlias, double amount, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.amount = amount;
        this.bankOfficeMessageType = bankOfficeMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param bankOfficeMessageType
     * @param peasantAlias
     */
    public BankOfficeMessage(BankOfficeMessageType bankOfficeMessageType, String peasantAlias, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.bankOfficeMessageType = bankOfficeMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param amount
     */
    public BankOfficeMessage(Integer amount) {
        this.amount = amount;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
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
    public BankOfficeMessageType getMessageType() {
        return bankOfficeMessageType;
    }

    /**
     *
     * @param bankOfficeMessageType
     */
    public void setMessageType(BankOfficeMessageType bankOfficeMessageType) {
        this.bankOfficeMessageType = bankOfficeMessageType;
    }
}
