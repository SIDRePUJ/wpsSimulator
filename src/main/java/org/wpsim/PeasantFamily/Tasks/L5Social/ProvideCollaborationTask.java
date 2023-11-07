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
package org.wpsim.PeasantFamily.Tasks.L5Social;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentGuard;
import org.wpsim.Simulator.Config.wpsConfig;
import org.wpsim.Society.Data.SocietyDataMessage;
import org.wpsim.Society.Guards.SocietyAgentOffersHelpGuard;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class ProvideCollaborationTask extends Task {

    private boolean finished;

    /**
     *
     */
    public ProvideCollaborationTask() {
        //wpsReport.info("");
        this.finished = false;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.finished = false;
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.ProvideCollaborationTask);

        try {
            SocietyDataMessage societyDataMessageToSent = new SocietyDataMessage(
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    5
            );
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsConfig.getInstance().getSocietyAgentName()
            ).sendEvent(
                    new EventBESA(SocietyAgentOffersHelpGuard.class.getName(), societyDataMessageToSent)
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }

        this.finished = true;
    }
    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
    }

    /**
     *
     * @param believes
     * @return
     */
    @Override
    public boolean checkFinish(Believes believes) {
        //wpsReport.info("");
        return this.finished;
    }
}
