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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Market.Guards.MarketAgentGuard;
import org.wpsim.Market.Data.MarketMessage;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.Market.Data.MarketMessageType.SELL_CROP;

/**
 * @author jairo
 */
public class SellCropTask extends wpsTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        //believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "SELLING", "FOOD"));

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getMarketAgentName()
            ).sendEvent(
                    new EventBESA(
                            MarketAgentGuard.class.getName(),
                            new MarketMessage(
                                    SELL_CROP,
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getPeasantProfile().getHarvestedWeight(),
                                    "rice" // @TODO: CAMBIAR NOMBRE AL REAL
                            )
                    )
            );
            believes.getPeasantProfile().setHarvestedWeight(0);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        believes.setCurrentSeason("", SeasonType.NONE);
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
