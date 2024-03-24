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
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsDataMessage;
import org.wpsim.CommunityDynamics.Guards.CommunityDynamicsRequestHelpGuard;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class LookForCollaborationTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.useTime(TimeConsumedBy.LookForCollaborationTask.getTime());
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.setAskedForCollaboration(true);

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsConfig.getInstance().getSocietyAgentName()
            ).sendEvent(
                    new EventBESA(CommunityDynamicsRequestHelpGuard.class.getName(),
                            new CommunityDynamicsDataMessage(
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    5
                            )
                    )
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
    }
}
