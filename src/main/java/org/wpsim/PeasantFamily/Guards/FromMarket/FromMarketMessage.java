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
package org.wpsim.PeasantFamily.Guards.FromMarket;

import BESA.Kernel.Agent.Event.DataBESA;
import org.wpsim.PeasantFamily.Data.FarmingResource;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jairo
 */
public class FromMarketMessage extends DataBESA {

    private int quantity;
    private FromMarketMessageType fromMarketMessageType;
    private int value;
    private String cropName;
    private Map<String, FarmingResource> priceList = new HashMap<>();

    /**
     *
     * @param fromMarketMessageType
     * @param quantity
     */
    public FromMarketMessage(FromMarketMessageType fromMarketMessageType, Integer quantity) {
        this.quantity = quantity;
        this.fromMarketMessageType = fromMarketMessageType;
    }

    /**
     *
     * @param fromMarketMessageType
     * @param priceList
     */
    public FromMarketMessage(FromMarketMessageType fromMarketMessageType, Map<String, FarmingResource> priceList) {
        this.priceList = priceList;
        this.fromMarketMessageType = fromMarketMessageType;
    }

    /**
     *
     * @param fromMarketMessageType
     * @param quantity
     * @param value
     */
    public FromMarketMessage(FromMarketMessageType fromMarketMessageType, String cropName, int quantity, int value) {
        this.value = value;
        this.quantity = quantity;
        this.cropName = cropName;
        this.fromMarketMessageType = fromMarketMessageType;
    }

    /**
     *
     * @param quantity
     */
    public FromMarketMessage(Integer quantity) {
        this.quantity = quantity;
    }

    public Map<String, FarmingResource> getPriceList() {
        return priceList;
    }

    public void setPriceList(Map<String, FarmingResource> priceList) {
        this.priceList = priceList;
    }

    /**
     *
     * @return
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     *
     * @param quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getCropName() {
        return cropName;
    }

    /**
     *
     * @param cropName
     */
    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    /**
     *
     * @return
     */
    public FromMarketMessageType getMessageType() {
        return fromMarketMessageType;
    }

    /**
     *
     * @param fromMarketMessageType
     */
    public void setMessageType(FromMarketMessageType fromMarketMessageType) {
        this.fromMarketMessageType = fromMarketMessageType;
    }
}
