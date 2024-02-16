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
package org.wpsim.Market.Data;

/**
 *
 */
public enum MarketMessageType {

    /**
     * 
     */
    ASK_FOR_PRICE_LIST,
    /**
     * 
     */
    BUY_SEEDS,
    
    /**
     * 
     */
    BUY_WATER,
    
    /**
     * 
     */
    BUY_PESTICIDES,
    
    /**
     * 
     */
    BUY_SUPPLIES,
    
    /**
     * 
     */
    BUY_TOOLS,
    
    /**
     * 
     */
    BUY_LIVESTOCK,
    
    /**
     * 
     */
    SELL_CROP,
    INCREASE_TOOS_PRICE,
    INCREASE_SEEDS_PRICE,
    INCREASE_CROP_PRICE,
    DECREASE_TOOLS_PRICE,
    DECREASE_SEEDS_PRICE,
    DECREASE_CROP_PRICE
    
}
