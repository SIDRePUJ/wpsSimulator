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
package org.wpsim.PeasantFamily.Guards.FromSimulationControl;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.SimulationControl.Data.ControlMessage;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;

/**
 *
 * @author jairo
 */
public class FromControlGuard extends GuardBESA {

    /**
     *
     * @param event Event rising the Guard
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        StateBDI state = (StateBDI) this.agent.getState();
        ControlMessage controlMessage = (ControlMessage) event.getData();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) state.getBelieves();
        /*if (controlMessage.isWaiting()) {
            believes.setCurrentActivity(PeasantActivityType.BLOCKED);
        }else{
            believes.setCurrentActivity(PeasantActivityType.NONE);
        }*/
        believes.setWait(controlMessage.isWaiting());
        //wpsReport.debug("desbloqueando " + believes.getPeasantProfile().getPeasantFamilyAlias(), believes.getPeasantProfile().getPeasantFamilyAlias());
    }
}
