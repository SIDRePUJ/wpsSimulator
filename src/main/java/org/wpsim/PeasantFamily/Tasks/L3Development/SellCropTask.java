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

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.CivicAuthority.Data.LandInfo;
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
        believes.useTime(TimeConsumedBy.HarvestCropsTask.getTime());
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "SELLING", "FOOD"));

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.SELL_CROP)) {
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
                                            currentLandInfo.getCropName(),
                                            believes.getInternalCurrentDate()
                                    )
                            )
                    );
                    currentLandInfo.setCurrentSeason(SeasonType.NONE);
                    believes.getPeasantProfile().setHarvestedWeight(0);
                    believes.setUpdatePriceList(true);
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
