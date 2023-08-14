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

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Control.ControlAgentState;
import org.wpsim.Control.ControlCurrentDate;
import org.wpsim.Control.ControlMessage;
import org.wpsim.PeasantFamily.Data.ToControlMessage;
import org.wpsim.PeasantFamily.Guards.FromControlGuard;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.wpsReport;

/**
 *
 * @author jairo
 */
public class AliveAgentGuard extends GuardBESA {
    /**
     *
     * @param event Event rising the Guard
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        ToControlMessage toControlMessage = (ToControlMessage) event.getData();
        String agentAlias = toControlMessage.getPeasantFamilyAlias();
        ControlAgentState state = (ControlAgentState) this.getAgent().getState();

        wpsReport.debug(agentAlias + " Alive - " + toControlMessage.getDays(), "ControlAgentGuard");
        state.addAgentToMap(agentAlias);

        try {
            int count = wpsStart.peasantFamiliesAgents;
            AgHandlerBESA agHandler = AdmBESA.getInstance().getHandlerByAlias(agentAlias);
            EventBESA eventBesa = new EventBESA(
                    FromControlGuard.class.getName(),
                    new ControlMessage(agentAlias, count--, wpsStart.DAYS_TO_CHECK)
            );
            agHandler.sendEvent(eventBesa);
            wpsReport.debug("Initial Unblock " + agentAlias + " sent " + count, "ControlAgentState");
        } catch (ExceptionBESA ex) {
            wpsReport.debug(ex, "ControlAgentState");
        }


    }


}
