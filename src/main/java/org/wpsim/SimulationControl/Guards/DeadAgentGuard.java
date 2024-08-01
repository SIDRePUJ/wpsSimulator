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
package org.wpsim.SimulationControl.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.SimulationControl.Data.SimulationControlState;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.ToControlMessage;
import org.wpsim.WellProdSim.wpsStart;

/**
 *
 * @author jairo
 */
public class DeadAgentGuard extends GuardBESA {

    ToControlMessage toControlMessage = null;
    String agentAlias = null;

    /**
     *
     * @param event Event rising the Guard
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        toControlMessage = (ToControlMessage) event.getData();
        agentAlias = toControlMessage.getPeasantFamilyAlias();
        SimulationControlState state = (SimulationControlState) this.getAgent().getState();

        state.removeAgentFromMap(agentAlias);
        if (state.getDeadAgentMap().size() == wpsStart.peasantFamiliesAgents) {
            try {
                System.out.println("UPDATE: cerrando simulación desde " + wpsStart.params.mode);
                AdmBESA.getInstance().getHandlerByAlias(
                        "wpsControl"
                ).sendEvent(
                        new EventBESA(
                                DeadContainerGuard.class.getName(),
                                new ToControlMessage(
                                        wpsStart.params.mode
                                )
                        )
                );
                System.out.println("UPDATE: mensaje enviado desde " + wpsStart.params.mode);
                Thread.sleep(1000);
                if (!wpsStart.params.mode.equals("wpsmain")) {
                    wpsStart.stopSimulation();
                }
            } catch (Exception ex) {
                System.err.println("UPDATE: " + ex.getMessage() + "wpsmain");
            }
        }
    }

}
