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
package org.wpsim.Control.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.Control.Data.ControlAgentState;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.PeasantFamily.Data.ToControlMessage;
import org.wpsim.Viewer.Server.WebsocketServer;

/**
 * @author jairo
 */
public class ControlAgentGuard extends GuardBESA {

    /**
     * @param event Event rising the Guard
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        ToControlMessage toControlMessage = (ToControlMessage) event.getData();
        String agentAlias = toControlMessage.getPeasantFamilyAlias();
        String agentCurrentDate = toControlMessage.getCurrentDate();
        int currentDay = toControlMessage.getDays();
        ControlAgentState state = (ControlAgentState) this.getAgent().getState();

        //wpsReport.debug("ControlAgentGuard: " + agentAlias + " acd " + agentCurrentDate + " gcd " + ControlCurrentDate.getInstance().getCurrentDate(), "ControlAgentGuard");
        state.modifyAgentMap(agentAlias);

        if (ControlCurrentDate.getInstance().isAfterDate(agentCurrentDate)) {
            ControlCurrentDate.getInstance().setCurrentDate(agentCurrentDate);
            WebsocketServer.getInstance().broadcastMessage("d=" + ControlCurrentDate.getInstance().getCurrentDate());
        }
        //state.checkUnblocking(currentDay);

    }

}
