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
package org.wpsim.Society.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentGuard;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentMessage;
import org.wpsim.PeasantFamily.Guards.FromSociety.FromSocietyGuard;
import org.wpsim.Society.Data.SocietyAgentState;
import org.wpsim.Society.Data.SocietyDataMessage;
import org.wpsim.Viewer.Data.wpsReport;

/**
 *
 * @author jairo
 */
public class SocietyAgentRequestHelpGuard extends GuardBESA  {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        wpsReport.debug("Llegada al agente Sociedad desde " + event.getSource(), this.getAgent().getAlias());
        SocietyDataMessage societyDataMessage = (SocietyDataMessage) event.getData();
        SocietyAgentState state = (SocietyAgentState) this.getAgent().getState();
        String agentHelper = state.getPeasantFamilyFromStack();
        try {
            SocietyDataMessage societyDataMessageToSent = new SocietyDataMessage(
                    agentHelper,
                    societyDataMessage.getPeasantFamilyAgent(),
                    5
            );
            System.out.println("enviando ayuda " +  agentHelper + " para " + societyDataMessage.getPeasantFamilyAgent());
            AdmBESA.getInstance().getHandlerByAlias(
                    societyDataMessage.getPeasantFamilyAgent()
            ).sendEvent(
                    new EventBESA(FromSocietyGuard.class.getName(), societyDataMessageToSent)
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
