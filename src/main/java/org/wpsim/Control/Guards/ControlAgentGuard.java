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

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Control.ControlAgentState;
import org.wpsim.Control.ControlCurrentDate;
import org.wpsim.PeasantFamily.Guards.FromControlGuard;
import org.wpsim.PeasantFamily.Guards.ToControlMessage;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.WebsocketServer;
import org.wpsim.Viewer.wpsReport;

/**
 * @author jairo
 */
public class ControlAgentGuard extends GuardBESA {

    /**
     * @param event Event rising the Guard
     */
    @Override
    public synchronized void funcExecGuard(EventBESA event) {
        ToControlMessage toControlMessage = (ToControlMessage) event.getData();
        String agentAlias = toControlMessage.getPeasantFamilyAlias();
        String agentCurrentDate = toControlMessage.getCurrentDate();
        ControlAgentState state = (ControlAgentState) this.getAgent().getState();

        if (ControlCurrentDate.getInstance().compareNewDateGreater(agentCurrentDate)){
            ControlCurrentDate.getInstance().setCurrentDate(agentCurrentDate);
            WebsocketServer.getInstance().broadcastMessage("d=" + ControlCurrentDate.getInstance().getCurrentDate());
            state.modifyAgentMap(agentAlias);
        }

    }


}
