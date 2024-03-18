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
package org.wpsim.MarketPlace.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.MarketPlace.Data.MarketPlaceState;
import org.wpsim.MarketPlace.Data.MarketPlaceMessage;
import org.wpsim.MarketPlace.Data.MarketPlaceMessageType;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketPlaceGuard;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketPlaceMessage;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketPlaceMessageType;
import org.wpsim.WellProdSim.Base.wpsGuardBESA;
import org.wpsim.WellProdSim.Util.wpsCSV;
import org.wpsim.ViewerLens.Util.wpsReport;

/**
 * @author jairo
 */
public class MarketPlaceGuard extends wpsGuardBESA {
    private int currentWeek;

    public MarketPlaceGuard() {
        super();
        this.currentWeek = 0;
        wpsCSV.log("Market", "Agent,CurrentDate,Action,Response");
    }

    /**
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        MarketPlaceMessage marketPlaceMessage = (MarketPlaceMessage) event.getData();
        MarketPlaceState state = (MarketPlaceState) this.agent.getState();
        String productType = "";

        MarketPlaceMessageType messageType = marketPlaceMessage.getMessageType();
        int quantity = marketPlaceMessage.getQuantity();

        try {
            FromMarketPlaceMessageType fromMarketPlaceMessageType = null;
            FromMarketPlaceMessage fromMarketPlaceMessage;

            switch (messageType) {
                case ASK_FOR_PRICE_LIST -> {
                    fromMarketPlaceMessageType = FromMarketPlaceMessageType.PRICE_LIST;
                    fromMarketPlaceMessage = new FromMarketPlaceMessage(
                            fromMarketPlaceMessageType,
                            state.getResources()
                    );
                }
                case SELL_CROP -> {
                    //System.out.println(marketPlaceMessage.getPeasantAlias() + " Recalculando para " + marketPlaceMessage.getCropName());
                    state.getResources().get(
                            marketPlaceMessage.getCropName()
                    ).setQuantity(
                            quantity + marketPlaceMessage.getQuantity()
                    );
                    // Update product price
                    state.updateAgentProductMapAndDiversityFactor(marketPlaceMessage.getPeasantAlias(), marketPlaceMessage.getCropName());
                    if (currentWeek != ControlCurrentDate.getInstance().getCurrentWeek()) {
                        currentWeek = ControlCurrentDate.getInstance().getCurrentWeek();
                        state.adjustPrices();
                    }
                    //wpsReport.warn("Comprado");
                    fromMarketPlaceMessageType = FromMarketPlaceMessageType.SOLD_CROP;
                    fromMarketPlaceMessage = new FromMarketPlaceMessage(
                            fromMarketPlaceMessageType,
                            marketPlaceMessage.getCropName(),
                            quantity,
                            state.getResources().get(
                                    marketPlaceMessage.getCropName()
                            ).getCost() * quantity
                    );
                }
                default -> {
                    productType = switch (messageType) {
                        case BUY_SEEDS -> "seeds";
                        case BUY_WATER -> "water";
                        case BUY_PESTICIDES -> "pesticides";
                        case BUY_SUPPLIES -> "supplies";
                        case BUY_TOOLS -> "tools";
                        case BUY_LIVESTOCK -> "livestock";
                        default -> throw new IllegalArgumentException("Invalid message type: " + messageType);
                    };
                    if (state.getResources().get(productType).isAvailable(quantity)) {
                        state.getResources().get(productType).discountQuantity(quantity);
                    }
                    fromMarketPlaceMessageType = FromMarketPlaceMessageType.valueOf(productType.toUpperCase());
                    fromMarketPlaceMessage = new FromMarketPlaceMessage(fromMarketPlaceMessageType, quantity);
                }
            }

            this.agent.getAdmLocal().getHandlerByAlias(
                    marketPlaceMessage.getPeasantAlias()
            ).sendEvent(
                    new EventBESA(
                            FromMarketPlaceGuard.class.getName(),
                            fromMarketPlaceMessage
                    )
            );
            wpsCSV.log("Market", marketPlaceMessage.getPeasantAlias() + "," + marketPlaceMessage.getCurrentDate() + "," + messageType + "," + fromMarketPlaceMessage.getMessageType());
            wpsReport.debug(marketPlaceMessage.getPeasantAlias() + " sending... " + productType, this.getAgent().getAlias());
        } catch (Exception e) {
            wpsReport.error("Mensaje no reconocido de funcExecGuard", this.getAgent().getAlias());
        }
    }

}
