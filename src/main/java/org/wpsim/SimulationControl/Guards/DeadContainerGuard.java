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
package org.wpsim.SimulationControl.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.ToControlMessage;
import org.wpsim.SimulationControl.Data.SimulationControlState;
import org.wpsim.WellProdSim.wpsStart;

import java.util.Enumeration;

/**
 *
 * @author jairo
 */
public class DeadContainerGuard extends GuardBESA {

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

        System.out.println("UPDATE: Llegó mensaje de " + agentAlias);

        if (agentAlias.equals("wps01") || agentAlias.equals("wps02") || agentAlias.equals("wps03")) {
            wpsStart.params.nodes--;
            System.out.println("UPDATE: Descontando un nodo");
        }

        if (shouldStopSimulation() && wpsStart.params.nodes == 0) {
            //waitForNodesToClose();
            System.out.println("UPDATE: cerrando simulación principal");
            wpsStart.stopSimulation();
        }
    }

    private boolean shouldStopSimulation() {
        SimulationControlState state = (SimulationControlState) this.getAgent().getState();
        return wpsStart.params.mode.equals("wpsmain") &&
                state.getDeadAgentMap().size() == wpsStart.peasantFamiliesAgents;
    }

    private void waitForNodesToClose() {
        while (wpsStart.params.nodes != 0) {
            Enumeration idlist = AdmBESA.getInstance().getIdList();
            System.out.print("UPDATE: ");
            while (idlist.hasMoreElements()) {
                System.out.print(" - " + AdmBESA.getInstance().getAliasByAid(idlist.nextElement().toString()));
            }
            System.out.println("\nUPDATE: esperando cerrar el principal, nodos activos " + wpsStart.params.nodes);
            wpsStart.pauseThread(100);
        }
    }

}
