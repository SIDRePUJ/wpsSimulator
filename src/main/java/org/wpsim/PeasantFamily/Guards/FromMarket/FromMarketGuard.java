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

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.Viewer.wpsReport;

/**
 *
 * @author jairo
 */
public class FromMarketGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        FromMarketMessage fromMarketMessage = (FromMarketMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) state.getBelieves();
        
        FromMarketMessageType fromMarketMessageType = fromMarketMessage.getMessageType();
        int discount = 0;
        
        //wpsReport.warn(fromMarketMessage.getMessageType());
        
        try {
            switch (fromMarketMessageType) {
                case SOLD_CROP:
                    // Incrementa el dinero
                    believes.getPeasantProfile().increaseMoney(
                            believes.getPeasantProfile().getHarvestedWeight()
                            * believes.getPriceList().get("ñame").getCost()
                    );
                    // Incrementa el total
                    believes.getPeasantProfile().increaseTotalHarvestedWeight(
                            believes.getPeasantProfile().getHarvestedWeight()
                    );
                    // descontar
                    believes.getPeasantProfile().setHarvestedWeight(
                            believes.getPeasantProfile().getHarvestedWeight()
                             - fromMarketMessage.getQuantity()
                    );                    
                    //wpsReport.info("----- Vendido");
                    break;
                case PRICE_LIST:
                    //wpsReport.debug(fromMarketMessage.getPriceList());
                    believes.setPriceList(
                            fromMarketMessage.getPriceList()
                    );
                    break;
                case SEEDS:
                    believes.getPeasantProfile().setSeeds(
                            fromMarketMessage.getQuantity()
                    );
                    discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("seeds").getCost();
                    break;
                case WATER:
                    believes.getPeasantProfile().setWaterAvailable(
                            fromMarketMessage.getQuantity()
                    );
                    discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("water").getCost();
                    break;
                case PESTICIDES:
                    believes.getPeasantProfile().setPesticidesAvailable(
                            fromMarketMessage.getQuantity()
                    );
                    discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("pesticides").getCost();
                    break;
                case SUPPLIES:
                    believes.getPeasantProfile().setSupplies(
                            fromMarketMessage.getQuantity()
                    );
                    discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("supplies").getCost();
                    break;
                case TOOLS:
                    believes.getPeasantProfile().setTools(
                            fromMarketMessage.getQuantity()
                    );
                    discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("tools").getCost();
                    break;
                case LIVESTOCK:
                    believes.getPeasantProfile().setLivestockNumber(
                            fromMarketMessage.getQuantity()
                    );
                    discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("livestock").getCost();
                    break;
            }
            
            believes.getPeasantProfile().useMoney(discount);
            
        } catch (IllegalArgumentException e) {
            wpsReport.error("Mensaje no reconocido de FromWorldMessageType", "FromMarketGuard");
        }
        
    }
    
}
