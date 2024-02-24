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
package org.wpsim.Market.Data;

import BESA.Kernel.Agent.Event.DataBESA;
import org.wpsim.Market.Data.MarketMessageType;

/**
 *
 * @author jairo
 */
public class MarketMessage extends DataBESA {

    private String peasantAlias;
    private int quantity;
    private MarketMessageType marketMessageType;
    private String cropName;
    private String currentDate;

    /**
     *
     * @param marketMessageType
     * @param peasantAlias
     * @param quantity
     */
    public MarketMessage(MarketMessageType marketMessageType, String peasantAlias, int quantity, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.quantity = quantity;
        this.marketMessageType = marketMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param marketMessageType
     * @param peasantAlias
     * @param quantity
     * @param cropName
     */
    public MarketMessage(MarketMessageType marketMessageType, String peasantAlias, double quantity, String cropName, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.quantity = (int) quantity;
        this.marketMessageType = marketMessageType;
        this.cropName = cropName;
        this.currentDate = currentDate;
    }

    public MarketMessage(MarketMessageType marketMessageType, int quantity, String currentDate) {
        this.marketMessageType = marketMessageType;
        this.quantity = (int) quantity;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param marketMessageType
     * @param peasantAlias
     * @param quantity
     */
    public MarketMessage(MarketMessageType marketMessageType, String peasantAlias, double quantity, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.quantity = (int) quantity;
        this.marketMessageType = marketMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param marketMessageType
     * @param peasantAlias
     */
    public MarketMessage(MarketMessageType marketMessageType, String peasantAlias, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.marketMessageType = marketMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param quantity
     */
    public MarketMessage(int quantity) {
        this.quantity = quantity;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
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
    public int getQuantity() {
        return quantity;
    }

    /**
     *
     * @param quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     *
     * @return
     */
    public MarketMessageType getMessageType() {
        return marketMessageType;
    }

    /**
     *
     * @param marketMessageType
     */
    public void setMessageType(MarketMessageType marketMessageType) {
        this.marketMessageType = marketMessageType;
    }
}
