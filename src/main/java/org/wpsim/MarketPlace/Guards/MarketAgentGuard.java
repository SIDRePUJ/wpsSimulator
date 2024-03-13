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
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.MarketPlace.Data.MarketAgentState;
import org.wpsim.MarketPlace.Data.MarketMessage;
import org.wpsim.MarketPlace.Data.MarketMessageType;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketGuard;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketMessage;
import org.wpsim.PeasantFamily.Guards.FromMarketPlace.FromMarketMessageType;
import org.wpsim.Simulator.Base.wpsGuardBESA;
import org.wpsim.Simulator.Util.wpsCSV;
import org.wpsim.Viewer.Data.wpsReport;

import static org.wpsim.MarketPlace.Data.MarketMessageType.ASK_FOR_PRICE_LIST;
import static org.wpsim.MarketPlace.Data.MarketMessageType.SELL_CROP;

/**
 *
 * @author jairo
 */
public class MarketAgentGuard extends wpsGuardBESA {
    private int currentWeek;

    public MarketAgentGuard() {
        super();
        this.currentWeek = 0;
        wpsCSV.log("Market","Agent,CurrentDate,Action,Response");
    }

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        MarketMessage marketMessage = (MarketMessage) event.getData();
        MarketAgentState state = (MarketAgentState) this.agent.getState();
        String productType = "";

        MarketMessageType messageType = marketMessage.getMessageType();
        int quantity = marketMessage.getQuantity();

        try {
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAlias(
                    marketMessage.getPeasantAlias()
            );
            FromMarketMessageType fromMarketMessageType = null;
            FromMarketMessage fromMarketMessage;

            if (messageType == ASK_FOR_PRICE_LIST) {
                fromMarketMessageType = FromMarketMessageType.PRICE_LIST;
                fromMarketMessage = new FromMarketMessage(
                        fromMarketMessageType,
                        state.getResources()
                );
            } else if (messageType == SELL_CROP) {
                state.getResources().get(marketMessage.getCropName()).setQuantity(
                        quantity
                                + marketMessage.getQuantity()
                );
                // Update product price
                state.updateAgentProductMapAndDiversityFactor(marketMessage.getPeasantAlias(), marketMessage.getCropName());
                if (currentWeek != ControlCurrentDate.getInstance().getCurrentWeek()) {
                    currentWeek = ControlCurrentDate.getInstance().getCurrentWeek();
                    state.adjustPrices();
                }
                //wpsReport.warn("Comprado");
                fromMarketMessageType = FromMarketMessageType.SOLD_CROP;
                fromMarketMessage = new FromMarketMessage(
                        fromMarketMessageType,
                        marketMessage.getCropName(),
                        quantity,
                        state.getResources().get(
                                marketMessage.getCropName()
                        ).getCost() * quantity
                );
            } else {
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
                fromMarketMessageType = FromMarketMessageType.valueOf(productType.toUpperCase());
                fromMarketMessage = new FromMarketMessage(fromMarketMessageType, quantity);
            }
            ah.sendEvent(
                    new EventBESA(
                            FromMarketGuard.class.getName(),
                            fromMarketMessage
                    )
            );
            wpsCSV.log("Market", marketMessage.getPeasantAlias() + "," + marketMessage.getCurrentDate() + "," + messageType + "," + fromMarketMessage.getMessageType());
            wpsReport.debug(marketMessage.getPeasantAlias() + " sending... " + productType, this.getAgent().getAlias());
        } catch (Exception e) {
            wpsReport.error("Mensaje no reconocido de funcExecGuard", this.getAgent().getAlias());
        }
    }

}
