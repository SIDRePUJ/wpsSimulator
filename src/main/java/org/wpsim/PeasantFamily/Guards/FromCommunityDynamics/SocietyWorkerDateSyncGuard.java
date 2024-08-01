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
package org.wpsim.PeasantFamily.Guards.FromCommunityDynamics;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsDataMessage;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.FromSimulationControlGuard;
import org.wpsim.SimulationControl.Data.AgentInfo;
import org.wpsim.SimulationControl.Data.ControlMessage;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.wpsStart;

/**
 * Peasant Helper Guard
 * @author jairo
 */
public class SocietyWorkerDateSyncGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        CommunityDynamicsDataMessage communityDynamicsDataMessage = (CommunityDynamicsDataMessage) event.getData();

        try {
            if (ControlCurrentDate.getInstance().isAfterDate(communityDynamicsDataMessage.getCurrentDate())) {
                System.out.println("UPDATE: Sincronizando con el otro agente " +
                        communityDynamicsDataMessage.getCurrentDate() + " - C " +
                        communityDynamicsDataMessage.getAgentAlias() + " " +
                        this.agent.getAlias()
                );
                AdmBESA.getInstance().getHandlerByAlias(
                        communityDynamicsDataMessage.getAgentAlias()
                ).sendEvent(
                        new EventBESA(
                                FromSimulationControlGuard.class.getName(),
                                new ControlMessage(
                                        communityDynamicsDataMessage.getAgentAlias(),
                                        true
                                )
                        )
                );
            }
        } catch (ExceptionBESA ex) {
            wpsReport.debug(ex, wpsStart.config.getControlAgentName());
        }


    }
}
