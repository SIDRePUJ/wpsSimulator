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

import BESA.Kernel.Agent.StateBESA;
import org.wpsim.SimulationControl.Data.Coin;
import org.wpsim.PeasantFamily.Data.Utils.FarmingResource;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Util.wpsReport;

import java.io.Serializable;
import java.util.*;

/**
 * @author jairo
 */
public class MarketPlaceState extends StateBESA implements Serializable {

    Map<String, FarmingResource> resources = new HashMap<>();
    private Map<String, Integer> basePrices = new HashMap<>();
    private Map<String, List<Long>> productAgentMap = new HashMap<>();
    private double diversityFactor;
    private final int maxPeasantFamiliesAgents;

    /**
     * Constructor for MarketAgentState
     */
    public MarketPlaceState() {
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
    public synchronized void updateAgentProductMapAndDiversityFactor(String agentName, String productName) {
        productAgentMap.computeIfAbsent(productName, k -> new ArrayList<>()).add(System.currentTimeMillis());
        adjustDiversityFactor();
    }

    /**
     * Ajusta el diversityFactor basado en la distribución actual de productos.
     */
    private synchronized void adjustDiversityFactor() {
        final long freshnessThreshold = 200; // 200 milisegundos para definir la "frescura" de una oferta
        long currentTime = System.currentTimeMillis();

        // Cuenta las ofertas "frescas" basadas en el umbral de frescura
        int freshOffersCount = productAgentMap.values().stream()
                .flatMap(List::stream)
                .mapToInt(timestamp -> (currentTime - timestamp < freshnessThreshold) ? 1 : 0)
                .sum();

        // Cuenta el total de ofertas, frescas o no
        int totalOffers = productAgentMap.values().stream()
                .mapToInt(List::size)
                .sum();

        // Ajusta el diversityFactor basado en la proporción de ofertas frescas sobre el total de ofertas
        diversityFactor = totalOffers > 0 ? (double) freshOffersCount / totalOffers : 0;
    }


    /**
     * Lógica para ajustar precios basados en la rareza de cada producto.
     */
    public synchronized void adjustPrices() {
        final long freshnessThreshold = 50; // 200 milisegundos para la frescura
        long currentTime = System.currentTimeMillis();

        // Redefine el umbral de frescura y los factores de ajuste para ser menos agresivos
        for (Map.Entry<String, List<Long>> entry : productAgentMap.entrySet()) {
            String productName = entry.getKey();
            List<Long> offerTimestamps = entry.getValue();

            // Cuenta las ofertas frescas
            long freshOffersCount = offerTimestamps.stream()
                    .filter(timestamp -> currentTime - timestamp < freshnessThreshold)
                    .count();

            double basePrice = basePrices.getOrDefault(productName, 0);

            // Inicialmente, establece el nuevo precio al precio base para enfatizar el anclaje al precio inicial
            double newPrice = basePrice;

            // Aplica ajustes moderados basados en la escasez o abundancia relativa
            if (freshOffersCount > 1) {
                // Si hay abundancia, reduce ligeramente el precio
                newPrice *= 0.95; // Reduce solo un 5%
            } else if (freshOffersCount == 0) {
                // Si hay escasez, incrementa ligeramente el precio
                newPrice *= 1.05; // Incrementa solo un 5%
            }

            // Implementa un factor de estabilidad más fuerte para mantener los precios cerca del precio inicial
            final double stabilityFactor = 0.8;
            double currentPrice = resources.get(productName).getCost();
            newPrice = (currentPrice * (1 - stabilityFactor)) + (newPrice * stabilityFactor);

            resources.get(productName).setCost((int) newPrice);
            resources.get(productName).setBehavior(Double.compare(currentPrice, newPrice));
        }
        //wpsReport.info("Adjusted prices around the initial price with moderated fluctuations.", "wpsMarket");
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

    public synchronized Map<String, FarmingResource> getResources() {
        return resources;
    }

    public synchronized void setResources(Map<String, FarmingResource> resources) {
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
    public synchronized void increaseCropPrice(int factor) {
        String productName;
        if (Coin.flipCoin()) {
            productName = "rice";
        } else {
            productName = "roots";
        }
        changePrice(productName, factor);
    }

    public synchronized void increaseToolsPrice(int factor) {
        changePrice("tools", factor);
    }

    public synchronized void decreaseToolsPrice(int factor) {
        changePrice("tools", factor * -1);
    }

    public synchronized void decreaseSeedsPrice(int factor) {
        changePrice("seeds", factor * -1);
    }

    public synchronized void increaseSeedsPrice(int factor) {
        changePrice("seeds", factor);
    }
    public synchronized void changePrice(String productName, double factor){
        factor = 1 + (factor / 100);
        double basePrice = resources.get(productName).getCost();
        double newPrice = basePrice * factor;
        resources.get(productName).setCost((int) newPrice);
        resources.get(productName).setBehavior(Double.compare(basePrice, newPrice));
        //System.out.println("Adjusted price for " + productName + " from " + price + " to " + (int) newPrice);
    }
}
