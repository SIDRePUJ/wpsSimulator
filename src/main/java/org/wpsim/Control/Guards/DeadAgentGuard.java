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
package org.wpsim.Control.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.Control.Data.ControlAgentState;
import org.wpsim.PeasantFamily.Guards.FromControl.ToControlMessage;
import org.wpsim.Viewer.Data.wpsReport;

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
        ControlAgentState state = (ControlAgentState) this.getAgent().getState();

        wpsReport.debug(agentAlias + " Dead - " + toControlMessage.getDays(), "ControlAgentGuard");
        state.removeAgentFromMap(agentAlias);
        //wpsReport.debug(state.printDeadAgentMap(), "ControlAgentGuard");
    }

}
