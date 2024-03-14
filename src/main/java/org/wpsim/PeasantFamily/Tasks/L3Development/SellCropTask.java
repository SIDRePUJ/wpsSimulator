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
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.MarketPlace.Guards.MarketPlaceGuard;
import org.wpsim.MarketPlace.Data.MarketPlaceMessage;
import org.wpsim.ViewerLens.Util.wpsReport;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.MarketPlace.Data.MarketPlaceMessageType.SELL_CROP;

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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        //believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "SELLING", "FOOD"));

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getMarketAgentName()
            ).sendEvent(
                    new EventBESA(
                            MarketPlaceGuard.class.getName(),
                            new MarketPlaceMessage(
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
