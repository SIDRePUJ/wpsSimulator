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
import org.wpsim.CommunityDynamics.Guards.CommunityDynamicsOffersHelpGuard;
import org.wpsim.WellProdSim.wpsStart;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;

/**
 *
 * @author jairo
 */
public class ProvideCollaborationTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(believes.getTimeLeftOnDay());

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getSocietyAgentName()
            ).sendEvent(
                    new EventBESA(CommunityDynamicsOffersHelpGuard.class.getName(),
                            new CommunityDynamicsDataMessage(
                                believes.getPeasantProfile().getPeasantFamilyAlias(),
                                believes.getPeasantProfile().getPeasantFamilyAlias(),
                                5
                            )
                    )
            );
            believes.setAskedForContractor(true);
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
    }
}
