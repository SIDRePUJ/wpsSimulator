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
package org.wpsim.PeasantFamily.Tasks.L4SkillsResources;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.MarketPlace.Guards.MarketPlaceGuard;
import org.wpsim.MarketPlace.Data.MarketPlaceMessage;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.ResourceNeededType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Util.wpsReport;
import rational.mapping.Believes;

import static org.wpsim.MarketPlace.Data.MarketPlaceMessageType.BUY_TOOLS;

/**
 *
 * @author jairo
 */
public class ObtainToolsTask extends wpsTask {

    /**
     *
     * @param parameters Believes
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getMarketAgentName()
            ).sendEvent(
                    new EventBESA(
                            MarketPlaceGuard.class.getName(),
                            new MarketPlaceMessage(
                                    BUY_TOOLS,
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getPeasantProfile().getToolsNeeded(),
                                    believes.getInternalCurrentDate()
                            )
                    )
            );
            wpsReport.debug("ObtainToolsTask.executeTask: "
                            + believes.getPeasantProfile().getPeasantFamilyAlias()
                            + " BUY_TOOLS",
                    believes.getPeasantProfile().getPeasantFamilyAlias()
            );

        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "obtainToolsTask.executeTask");
        }
        believes.setCurrentResourceNeededType(ResourceNeededType.NONE);
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
