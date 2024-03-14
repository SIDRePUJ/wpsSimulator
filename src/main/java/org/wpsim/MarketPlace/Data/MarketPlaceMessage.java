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
package org.wpsim.MarketPlace.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class MarketPlaceMessage extends DataBESA {

    private String peasantAlias;
    private int quantity;
    private MarketPlaceMessageType marketMessageType;
    private String cropName;
    private String currentDate;

    /**
     *
     * @param marketPlaceMessageType
     * @param peasantAlias
     * @param quantity
     */
    public MarketPlaceMessage(MarketPlaceMessageType marketPlaceMessageType, String peasantAlias, int quantity, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.quantity = quantity;
        this.marketMessageType = marketPlaceMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param marketPlaceMessageType
     * @param peasantAlias
     * @param quantity
     * @param cropName
     */
    public MarketPlaceMessage(MarketPlaceMessageType marketPlaceMessageType, String peasantAlias, double quantity, String cropName, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.quantity = (int) quantity;
        this.marketMessageType = marketPlaceMessageType;
        this.cropName = cropName;
        this.currentDate = currentDate;
    }

    public MarketPlaceMessage(MarketPlaceMessageType marketPlaceMessageType, int quantity, String currentDate) {
        this.marketMessageType = marketPlaceMessageType;
        this.quantity = (int) quantity;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param marketPlaceMessageType
     * @param peasantAlias
     * @param quantity
     */
    public MarketPlaceMessage(MarketPlaceMessageType marketPlaceMessageType, String peasantAlias, double quantity, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.quantity = (int) quantity;
        this.marketMessageType = marketPlaceMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param marketPlaceMessageType
     * @param peasantAlias
     */
    public MarketPlaceMessage(MarketPlaceMessageType marketPlaceMessageType, String peasantAlias, String currentDate) {
        this.peasantAlias = peasantAlias;
        this.marketMessageType = marketPlaceMessageType;
        this.currentDate = currentDate;
    }

    /**
     *
     * @param quantity
     */
    public MarketPlaceMessage(int quantity) {
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
    public MarketPlaceMessageType getMessageType() {
        return marketMessageType;
    }

    /**
     *
     * @param marketPlaceMessageType
     */
    public void setMessageType(MarketPlaceMessageType marketPlaceMessageType) {
        this.marketMessageType = marketPlaceMessageType;
    }
}
