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
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Market.MarketAgentGuard;
import org.wpsim.Market.MarketMessage;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

import static org.wpsim.Market.MarketMessageType.BUY_LIVESTOCK;

/**
 *
 * @author jairo
 */
public class ObtainLivestockTask extends Task {

    private boolean finished;

    /**
     *
     */
    public ObtainLivestockTask() {
        ////wpsReport.info("");
        this.finished = false;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        //wpsReport.info("$ Asking for a LOAN to the Bank " + believes.getProfile().getMoney());

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
        //this.setTaskWaitingForExecution();
        this.setFinished();
    }

    /**
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     *
     */
    public void setFinished() {
        this.finished = true;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
        this.setFinished();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
        this.setFinished();
    }

    /**
     *
     * @return
     */
    public boolean isExecuted() {
        return finished;
    }

    /**
     *
     * @param believes
     * @return
     */
    @Override
    public boolean checkFinish(Believes believes) {
        return isExecuted();
    }
}
