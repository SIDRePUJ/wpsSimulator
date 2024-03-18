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
package org.wpsim.PeasantFamily.Guards.FromMarketPlace;

import BESA.Kernel.Agent.Event.DataBESA;
import org.wpsim.PeasantFamily.Data.Utils.FarmingResource;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jairo
 */
public class FromMarketPlaceMessage extends DataBESA {

    private int quantity;
    private FromMarketPlaceMessageType fromMarketPlaceMessageType;
    private int value;
    private String cropName;
    private Map<String, FarmingResource> priceList = new HashMap<>();

    /**
     *
     * @param fromMarketPlaceMessageType
     * @param quantity
     */
    public FromMarketPlaceMessage(FromMarketPlaceMessageType fromMarketPlaceMessageType, Integer quantity) {
        this.quantity = quantity;
        this.fromMarketPlaceMessageType = fromMarketPlaceMessageType;
    }

    /**
     *
     * @param fromMarketPlaceMessageType
     * @param priceList
     */
    public FromMarketPlaceMessage(FromMarketPlaceMessageType fromMarketPlaceMessageType, Map<String, FarmingResource> priceList) {
        this.priceList = priceList;
        this.fromMarketPlaceMessageType = fromMarketPlaceMessageType;
    }

    /**
     *
     * @param fromMarketPlaceMessageType
     * @param quantity
     * @param value
     */
    public FromMarketPlaceMessage(FromMarketPlaceMessageType fromMarketPlaceMessageType, String cropName, int quantity, int value) {
        this.value = value;
        this.quantity = quantity;
        this.cropName = cropName;
        this.fromMarketPlaceMessageType = fromMarketPlaceMessageType;
    }

    /**
     *
     * @param quantity
     */
    public FromMarketPlaceMessage(Integer quantity) {
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
    public FromMarketPlaceMessageType getMessageType() {
        return fromMarketPlaceMessageType;
    }

    /**
     *
     * @param fromMarketPlaceMessageType
     */
    public void setMessageType(FromMarketPlaceMessageType fromMarketPlaceMessageType) {
        this.fromMarketPlaceMessageType = fromMarketPlaceMessageType;
    }
}
