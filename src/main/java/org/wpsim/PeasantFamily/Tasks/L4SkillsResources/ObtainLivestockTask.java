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
package org.wpsim.PeasantFamily.Tasks.L4SkillsResources;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Market.Guards.MarketAgentGuard;
import org.wpsim.Market.Data.MarketMessage;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.Market.Data.MarketMessageType.BUY_LIVESTOCK;

/**
 *
 * @author jairo
 */
public class ObtainLivestockTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        // @TODO: Se debe calcular cuanto necesitas prestar hasta que se coseche.
        try {
            AdmBESA adm = AdmBESA.getInstance();
            AgHandlerBESA ah = adm.getHandlerByAlias(wpsStart.config.getMarketAgentName());

            MarketMessage marketMessage = new MarketMessage(
                    BUY_LIVESTOCK,
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    1);

            EventBESA ev = new EventBESA(
                    MarketAgentGuard.class.getName(),
                    marketMessage);
            ah.sendEvent(ev);

        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "ObtainLivestockTask");
        }
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
