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
package org.wpsim.Market;

import BESA.Kernel.Agent.StateBESA;
import org.wpsim.PeasantFamily.Data.Utils.FarmingResource;
import org.wpsim.Simulator.wpsStart;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jairo
 */
public class MarketAgentState extends StateBESA implements Serializable {

    /**
     *
     */
    Map<String, FarmingResource> resources = new HashMap<>();

    @Override
    public String toString() {
        return "MarketAgentState{" +
                "resources=" + resources +
                '}';
    }

    /**
     *
     */
    public MarketAgentState() {
        super();
        this.resources = wpsStart.config.loadMarketConfig();
    }
    public Map<String, FarmingResource> getResources() {
        return resources;
    }
    public void setResources(Map<String, FarmingResource> resources) {
        this.resources = resources;
    }
}
