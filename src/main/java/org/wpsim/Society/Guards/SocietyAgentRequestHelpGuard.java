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
import org.wpsim.PeasantFamily.Guards.FromSociety.SocietyWorkerContractGuard;
import org.wpsim.PeasantFamily.Guards.FromSociety.SocietyWorkerContractorGuard;
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
        // Agente que ayuda
        String agentHelper = state.getPeasantFamilyFromStack();
        // Agente que pidió ayuda
        String agentContractor = societyDataMessage.getPeasantFamilyAgent();

        try {
            SocietyDataMessage societyDataMessageToSent = new SocietyDataMessage(
                    agentHelper, //Ayudante
                    agentContractor, //Contratista
                    5
            );
            System.out.println("enviando ayuda " +  agentHelper + " para " + societyDataMessage.getPeasantFamilyAgent());
            AdmBESA.getInstance().getHandlerByAlias(
                    societyDataMessage.getPeasantFamilyAgent()
            ).sendEvent(
                    new EventBESA(SocietyWorkerContractGuard.class.getName(), societyDataMessageToSent)
            );
            System.out.println("enviando contrato " +  agentHelper + " para " + societyDataMessage.getPeasantFamilyAgent());
            AdmBESA.getInstance().getHandlerByAlias(
                    societyDataMessage.getPeasantFamilyAgent()
            ).sendEvent(
                    new EventBESA(SocietyWorkerContractorGuard.class.getName(), societyDataMessageToSent)
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
