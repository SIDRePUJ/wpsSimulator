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
package org.wpsim.CommunityDynamics.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Guards.FromCommunityDynamics.SocietyWorkerContractGuard;
import org.wpsim.PeasantFamily.Guards.FromCommunityDynamics.SocietyWorkerContractorGuard;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsState;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsDataMessage;
import org.wpsim.ViewerLens.Util.wpsReport;

/**
 *
 * @author jairo
 */
public class CommunityDynamicsRequestHelpGuard extends GuardBESA  {

    /**
     * Social Collaborator Assignment Guard
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        CommunityDynamicsDataMessage communityDynamicsDataMessage = (CommunityDynamicsDataMessage) event.getData();
        CommunityDynamicsState state = (CommunityDynamicsState) this.getAgent().getState();

        try {

            String agentHelper = state.getPeasantFamilyFromStack(); // Agente que ayuda
            String agentContractor = communityDynamicsDataMessage.getPeasantFamilyContractor(); // Agente que pidió ayuda

            //System.out.println("Buscando ayuda el agente " + agentContractor);

            if (agentHelper == null) {
                return;
            }else{
                //wpsReport.info("Disponible " + agentHelper + " para ayudar a " + agentContractor, this.getAgent().getAlias());
            }

            //wpsReport.info("enviando ayuda " +  agentHelper + " para " + societyDataMessage.getPeasantFamilyAgent(), this.getAgent().getAlias());
            //System.out.println("enviando contrato como ayudante a " +  agentHelper + " para " + societyDataMessage.getPeasantFamilyContractor());
            AdmBESA.getInstance().getHandlerByAlias(
                    agentHelper
            ).sendEvent(
                    new EventBESA(SocietyWorkerContractGuard.class.getName(),
                            new CommunityDynamicsDataMessage(
                                    agentContractor, // Contratador
                                    agentHelper, // Ayudante
                                    5
                            ))
            );
            //wpsReport.info("enviando contrato " +  agentHelper + " para " + societyDataMessage.getPeasantFamilyAgent(), this.getAgent().getAlias());
            //System.out.println("enviando contrato como contratista a " + agentContractor);
            AdmBESA.getInstance().getHandlerByAlias(
                    agentContractor
            ).sendEvent(
                    new EventBESA(SocietyWorkerContractorGuard.class.getName(),
                            new CommunityDynamicsDataMessage(
                                    agentContractor, // Contratador
                                    agentHelper, // Ayudante
                                    5
                            ))
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
