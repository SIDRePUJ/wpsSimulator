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

import BESA.Kernel.Agent.StateBESA;
import org.wpsim.Control.Data.Coin;
import org.wpsim.PeasantFamily.Data.Utils.FarmingResource;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsReport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jairo
 */
public class MarketAgentState extends StateBESA implements Serializable {

    Map<String, FarmingResource> resources = new HashMap<>();
    private Map<String, Integer> basePrices = new HashMap<>();
    private Map<String, Set<String>> productAgentMap = new HashMap<>();
    private double diversityFactor;
    private final int maxPeasantFamiliesAgents;

    /**
     * Constructor for MarketAgentState
     */
    public MarketAgentState() {
        super();
        this.resources = wpsStart.config.loadMarketConfig();
        this.diversityFactor = 0.5;
        this.maxPeasantFamiliesAgents = wpsStart.peasantFamiliesAgents;
        resources.forEach((key, value) -> basePrices.put(key, value.getCost()));
    }

    /**
     * Actualiza el conjunto de agentes para un producto específico y ajusta el diversityFactor.
     *
     * @param agentName   El nombre del agente que ofrece el producto.
     * @param productName El nombre del producto ofrecido.
     */
    public void updateAgentProductMapAndDiversityFactor(String agentName, String productName) {
        productAgentMap.computeIfAbsent(productName, k -> new HashSet<>()).add(agentName);
        adjustDiversityFactor();
    }

    /**
     * Ajusta el diversityFactor basado en la distribución actual de productos.
     */
    private void adjustDiversityFactor() {
        int productTypeCount = productAgentMap.size();
        int uniqueAgentsOffering = productAgentMap.values().stream()
                .mapToInt(Set::size)
                .sum();
        int leastOfferedProductAgentCount = productAgentMap.values().stream()
                .mapToInt(Set::size)
                .min()
                .orElse(0);
        if (uniqueAgentsOffering > 0) {
            double leastOfferedRatio = (double) leastOfferedProductAgentCount / uniqueAgentsOffering;
            diversityFactor = 1.0 - ((double) productTypeCount / maxPeasantFamiliesAgents) * leastOfferedRatio;
        }
    }

    /**
     * Lógica para ajustar precios basados en la rareza de cada producto.
     */
    public void adjustPrices() {
        for (Map.Entry<String, Set<String>> entry : productAgentMap.entrySet()) {
            String productName = entry.getKey();
            Set<String> agents = entry.getValue();
            double price = resources.get(productName).getCost();
            double basePrice = basePrices.getOrDefault(productName, 0);
            double newPrice = price * Math.pow(diversityFactor, agents.size());
            newPrice = Math.max(newPrice, basePrice / 2.0);
            resources.get(productName).setCost((int) newPrice);
            System.out.println("Adjusted price for " + productName + " from " + price + " to " + newPrice);
        }
        wpsReport.info("Adjusted prices based on rarity " + this.toString(), "wpsMarket");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MarketAgentState{");
        sb.append("resources=");

        if (resources.isEmpty()) {
            sb.append("No resources available");
        } else {
            sb.append("[");
            resources.forEach((key, value) -> sb.append("{Product Name: ")
                    .append(key)
                    .append(", Details: ")
                    .append(value.toString())
                    .append("}, "));
            // Remueve la última coma y espacio si hay elementos en el mapa
            sb.delete(sb.length() - 2, sb.length());
            sb.append("]");
        }

        sb.append('}');
        return sb.toString();
    }

    public Map<String, FarmingResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, FarmingResource> resources) {
        this.resources = resources;
    }

    public void decreaseCropPrice(int factor) {
        String productName;
        if (Coin.flipCoin()) {
            productName = "rice";
        } else {
            productName = "roots";
        }
        changePrice(productName, factor * -1);
    }
    public void increaseCropPrice(int factor) {
        String productName;
        if (Coin.flipCoin()) {
            productName = "rice";
        } else {
            productName = "roots";
        }
        changePrice(productName, factor);
    }

    public void increaseToolsPrice(int factor) {
        changePrice("tools", factor);
    }

    public void decreaseToolsPrice(int factor) {
        changePrice("tools", factor * -1);
    }

    public void decreaseSeedsPrice(int factor) {
        changePrice("seeds", factor * -1);
    }

    public void increaseSeedsPrice(int factor) {
        changePrice("seeds", factor);
    }
    public void changePrice(String productName, double factor){
        factor = 1 + (factor / 100);
        double price = resources.get(productName).getCost();
        double newPrice = price * factor;
        resources.get(productName).setCost((int) newPrice);
        System.out.println("Adjusted price for " + productName + " from " + price + " to " + (int) newPrice);
    }
}
