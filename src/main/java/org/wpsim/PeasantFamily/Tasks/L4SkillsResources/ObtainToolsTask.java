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
import org.wpsim.Market.Guards.MarketAgentGuard;
import org.wpsim.Market.Data.MarketMessage;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.ResourceNeededType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.Simulator.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;

import static org.wpsim.Market.Data.MarketMessageType.BUY_TOOLS;

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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getMarketAgentName()
            ).sendEvent(
                    new EventBESA(
                            MarketAgentGuard.class.getName(),
                            new MarketMessage(
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
