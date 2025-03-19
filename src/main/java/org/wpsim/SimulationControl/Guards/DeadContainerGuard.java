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
import java.util.Set;

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
        ToControlMessage toControlMessage = (ToControlMessage) event.getData();
        String agentAlias = toControlMessage.getPeasantFamilyAlias();

        System.out.println("UPDATE: Llegó mensaje de " + agentAlias);

        if (isWpsAlias(agentAlias)) {
            decreaseNodeCount();
        }

        if (shouldStopSimulation() && wpsStart.params.nodes == 0) {
            System.out.println("UPDATE: cerrando simulación principal");
            wpsStart.stopSimulation();
        }
    }

    // Método auxiliar para verificar si el alias pertenece a los WPS
    private boolean isWpsAlias(String agentAlias) {
        Set<String> wpsAliases = Set.of("single", "web", "wps01", "wps02", "wps03", "wps04", "wps05");
        return wpsAliases.contains(agentAlias);
    }

    // Método auxiliar para reducir la cantidad de nodos de forma segura
    private void decreaseNodeCount() {
        if (wpsStart.params.nodes > 0) {
            wpsStart.params.nodes--;
            System.out.println("UPDATE: Descontando un nodo");
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
