
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
package org.wpsim.Market.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.Market.Data.MarketAgentState;
import org.wpsim.Market.Data.MarketMessage;
import org.wpsim.Market.Data.MarketMessageType;
import org.wpsim.PeasantFamily.Guards.FromMarket.FromMarketGuard;
import org.wpsim.PeasantFamily.Guards.FromMarket.FromMarketMessage;
import org.wpsim.PeasantFamily.Guards.FromMarket.FromMarketMessageType;
import org.wpsim.Viewer.Data.wpsReport;

import static org.wpsim.Market.Data.MarketMessageType.ASK_FOR_PRICE_LIST;
import static org.wpsim.Market.Data.MarketMessageType.SELL_CROP;

/**
 *
 * @author jairo
 */
public class MarketInfoAgentGuard extends GuardBESA {
    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        MarketMessage marketMessage = (MarketMessage) event.getData();
        MarketAgentState state = (MarketAgentState) this.agent.getState();

        switch (marketMessage.getMessageType()){
            case DECREASE_CROP_PRICE -> {
                state.decreaseCropPrice(marketMessage.getQuantity());
            }
            case INCREASE_CROP_PRICE -> {
                state.increaseCropPrice(marketMessage.getQuantity());
            }
            case INCREASE_TOOS_PRICE -> {
                state.increaseToolsPrice(marketMessage.getQuantity());
            }
            case DECREASE_TOOLS_PRICE -> {
                state.decreaseToolsPrice(marketMessage.getQuantity());
            }
            case DECREASE_SEEDS_PRICE -> {
                state.decreaseSeedsPrice(marketMessage.getQuantity());
            }
            case INCREASE_SEEDS_PRICE -> {
                state.increaseSeedsPrice(marketMessage.getQuantity());
            }

        }

    }

}
