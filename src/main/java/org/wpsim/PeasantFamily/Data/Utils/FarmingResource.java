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

package org.wpsim.PeasantFamily.Data.Utils;

/**
 *
 * @author jairo
 */
public class FarmingResource {

    public FarmingResource(String name, int cost, int quantity) {
        this.cost = cost;
        this.name = name;
        this.quantity = quantity;
    }
    public FarmingResource(String name, String cost, String quantity) {
        this.cost = Integer.parseInt(cost);
        this.name = name;
        this.quantity = Integer.parseInt(quantity);
    }
    
    int cost;
    String name;
    int quantity;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }
    public boolean isAvailable(int quantity){
        return (quantity <= this.quantity);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void discountQuantity(int quantity) {
        this.quantity -= quantity;
    }
}
