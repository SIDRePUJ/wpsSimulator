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
package org.wpsim.PeasantFamily.Guards.FromMarketPlace;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.ViewerLens.Util.wpsReport;

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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();

        FromMarketMessageType fromMarketMessageType = fromMarketMessage.getMessageType();
        wpsReport.debug(fromMarketMessageType, believes.getPeasantProfile().getPeasantFamilyAlias());
        int discount = 0;

        //wpsReport.warn(fromMarketMessage.getMessageType());

        switch (fromMarketMessageType) {
            case SOLD_CROP:
                // Incrementa el dinero
                /*System.out.println(
                        " ++ Cosechado Total " +
                                believes.getPeasantProfile().getTotalHarvestedWeight() +
                                " ++ Cosechado " +
                                believes.getPeasantProfile().getHarvestedWeight() +
                                " ++ Vendido "
                                + fromMarketMessage.getQuantity() +
                                " ++ al Precio de "
                                + believes.getPriceList().get("rice").getCost()
                );*/
                believes.getPeasantProfile().increaseMoney(
                        believes.getPeasantProfile().getHarvestedWeight()
                                * believes.getPriceList().get("rice").getCost()
                );
                believes.setUpdatePriceList(true);
                break;
            case PRICE_LIST:
                believes.setPriceList(fromMarketMessage.getPriceList());
                break;
            case SEEDS:
                believes.getPeasantProfile().setSeeds(fromMarketMessage.getQuantity());
                believes.getPeasantProfile().decreaseSeedsNeeded(fromMarketMessage.getQuantity());
                discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("seeds").getCost();
                break;
            case WATER:
                believes.getPeasantProfile().setWaterAvailable(fromMarketMessage.getQuantity());
                discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("water").getCost();
                break;
            case PESTICIDES:
                believes.getPeasantProfile().setPesticidesAvailable(fromMarketMessage.getQuantity());
                discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("pesticides").getCost();
                break;
            case SUPPLIES:
                believes.getPeasantProfile().setSupplies(fromMarketMessage.getQuantity());
                discount = fromMarketMessage.getQuantity() * believes.getPriceList().get("supplies").getCost();
                break;
            case TOOLS:
                believes.getPeasantProfile().setTools(fromMarketMessage.getQuantity());
                believes.getPeasantProfile().decreaseToolsNeeded(fromMarketMessage.getQuantity());
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


    }

}
