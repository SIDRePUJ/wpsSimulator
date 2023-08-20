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
package org.wpsim.PeasantFamily.Guards;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Log.ReportBESA;
import org.wpsim.Control.Guards.ControlAgentGuard;
import org.wpsim.PeasantFamily.Agent.PeasantFamilyBDIAgent;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.PeasantFamily.Data.ToControlMessage;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.wpsReport;
import rational.guards.InformationFlowGuard;

/**
 *
 * @author jairo
 */
public class HeartBeatGuard extends PeriodicGuardBESA {
    
    /**
     *
     * @param event
     */
    @Override
    public void funcPeriodicExecGuard(EventBESA event) {
        PeasantFamilyBDIAgent PeasantFamily = (PeasantFamilyBDIAgent) this.getAgent();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) ((StateBDI) PeasantFamily.getState()).getBelieves();
        StateBDI state = (StateBDI) PeasantFamily.getState();
        String PeasantFamilyAlias = believes.getPeasantProfile().getPeasantFamilyAlias();
        //System.out.println(PeasantFamilyAlias + " alive " + believes.getInternalCurrentDate() + " " + believes.getCurrentDay());

        if (believes.getPeasantProfile().getHealth() <= 0) {
            this.agent.shutdownAgent();
            return;
        }
        //System.out.println("HeartBeatGuard pulse of " + getDelayTime() + " milliseconds for " + PeasantFamilyAlias);

        try {
            ToControlMessage toControlMessage = new ToControlMessage(
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getInternalCurrentDate(),
                    believes.getCurrentDay()
            );
            EventBESA eventBesa = new EventBESA(
                    ControlAgentGuard.class.getName(),
                    toControlMessage
            );
            AgHandlerBESA agHandler = AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getControlAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA ex) {
            ReportBESA.error(ex);
        }

        try {
            AgHandlerBESA agHandler = AdmBESA.getInstance().getHandlerByAlias(PeasantFamilyAlias);
            EventBESA eventBesa = new EventBESA(
                    InformationFlowGuard.class.getName(),
                    null
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, PeasantFamilyAlias);
        }

        wpsReport.ws(believes.toJson(), believes.getPeasantProfile().getPeasantFamilyAlias());

        int waitTime = wpsStart.stepTime;
        if (state.getMainRole() != null) {
            waitTime = TimeConsumedBy.valueOf(state.getMainRole().getRoleName()).getTime() * wpsStart.stepTime;
            //System.out.println("waitTime: " + waitTime);
        }

        this.setDelayTime(waitTime);

    }
    
}
