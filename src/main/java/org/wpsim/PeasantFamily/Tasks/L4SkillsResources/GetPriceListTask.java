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
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Simulator.Base.wpsTask;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Market.Guards.MarketAgentGuard;
import org.wpsim.Market.Data.MarketMessage;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.PeasantActivityType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.Market.Data.MarketMessageType.ASK_FOR_PRICE_LIST;

/**
 *
 * @author jairo
 */
public class GetPriceListTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        believes.setCurrentActivity(PeasantActivityType.NONE);

        // @TODO: Se debe calcular cuanto necesitas prestar hasta que se coseche.
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getMarketAgentName()
            ).sendEvent(
                    new EventBESA(
                            MarketAgentGuard.class.getName(),
                            new MarketMessage(
                                    ASK_FOR_PRICE_LIST,
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getInternalCurrentDate()
                            )
                    )
            );
            believes.setUpdatePriceList(false);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
    }

}
