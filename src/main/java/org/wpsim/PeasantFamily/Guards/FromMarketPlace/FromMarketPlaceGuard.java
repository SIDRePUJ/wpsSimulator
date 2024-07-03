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
public class FromMarketPlaceGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        FromMarketPlaceMessage fromMarketPlaceMessage = (FromMarketPlaceMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();

        FromMarketPlaceMessageType fromMarketPlaceMessageType = fromMarketPlaceMessage.getMessageType();
        wpsReport.debug(fromMarketPlaceMessageType, believes.getPeasantProfile().getPeasantFamilyAlias());
        int discount = 0;

        //wpsReport.warn(fromMarketMessage.getMessageType());

        switch (fromMarketPlaceMessageType) {
            case SOLD_CROP:
                believes.getPeasantProfile().increaseMoney(
                        believes.getPeasantProfile().getHarvestedWeight()
                                * believes.getPriceList().get("rice").getCost()
                );
                believes.setUpdatePriceList(true);
                break;
            case PRICE_LIST:
                believes.setPriceList(fromMarketPlaceMessage.getPriceList());
                //System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n=============" + fromMarketPlaceMessage.getPriceList().size() + "================\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + this.getAgent().getAlias());
                break;
            case SEEDS:
                believes.getPeasantProfile().setSeeds(fromMarketPlaceMessage.getQuantity());
                believes.getPeasantProfile().decreaseSeedsNeeded(fromMarketPlaceMessage.getQuantity());
                discount = fromMarketPlaceMessage.getQuantity() * believes.getPriceList().get("seeds").getCost();
                break;
            case WATER:
                believes.getPeasantProfile().setWaterAvailable(fromMarketPlaceMessage.getQuantity());
                discount = fromMarketPlaceMessage.getQuantity() * believes.getPriceList().get("water").getCost();
                break;
            case PESTICIDES:
                believes.getPeasantProfile().setPesticidesAvailable(fromMarketPlaceMessage.getQuantity());
                discount = fromMarketPlaceMessage.getQuantity() * believes.getPriceList().get("pesticides").getCost();
                break;
            case SUPPLIES:
                believes.getPeasantProfile().setSupplies(fromMarketPlaceMessage.getQuantity());
                discount = fromMarketPlaceMessage.getQuantity() * believes.getPriceList().get("supplies").getCost();
                break;
            case TOOLS:
                believes.getPeasantProfile().setTools(fromMarketPlaceMessage.getQuantity());
                believes.getPeasantProfile().decreaseToolsNeeded(fromMarketPlaceMessage.getQuantity());
                discount = fromMarketPlaceMessage.getQuantity() * believes.getPriceList().get("tools").getCost();
                break;
            case LIVESTOCK:
                believes.getPeasantProfile().setLivestockNumber(
                        fromMarketPlaceMessage.getQuantity()
                );
                discount = fromMarketPlaceMessage.getQuantity() * believes.getPriceList().get("livestock").getCost();
                break;
        }

        believes.getPeasantProfile().useMoney(discount);


    }

}
