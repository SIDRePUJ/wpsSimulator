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
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsDataMessage;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.ToControlMessage;
import org.wpsim.SimulationControl.Guards.DeadContainerGuard;
import org.wpsim.WellProdSim.wpsStart;

/**
 * Peasant Helper Guard
 * @author jairo
 */
public class SocietyWorkerContractGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        CommunityDynamicsDataMessage communityDynamicsDataMessage = (CommunityDynamicsDataMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();
        believes.setContractor(communityDynamicsDataMessage.getPeasantFamilyContractor());
        believes.setDaysToWorkForOther(communityDynamicsDataMessage.getAvailableDays());

        try {
            //System.out.println("UPDATE: cerrando simulación desde " + wpsStart.params.mode);
            AdmBESA.getInstance().getHandlerByAlias(
                    believes.getContractor()
            ).sendEvent(
                    new EventBESA(
                            SocietyWorkerDateSyncGuard.class.getName(),
                            new CommunityDynamicsDataMessage(
                                    believes.getAlias(),
                                    believes.getInternalCurrentDate()
                            )
                    )
            );
            //System.out.println("UPDATE: Enviando Sincronización de Fecha entre " + this.agent.getAlias() + " y " + believes.getContractor());
        } catch (Exception ex) {
            System.err.println("UPDATE: " + ex.getMessage() + "wpsmain");
        }

    }
}
