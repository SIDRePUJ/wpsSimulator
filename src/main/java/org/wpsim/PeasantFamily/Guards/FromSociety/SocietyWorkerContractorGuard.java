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
package org.wpsim.PeasantFamily.Guards.FromSociety;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.Society.Data.SocietyDataMessage;

/**
 * Peasant Contractor Guard
 * @author jairo
 */
public class SocietyWorkerContractorGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        SocietyDataMessage societyDataMessage = (SocietyDataMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) state.getBelieves();
        /*System.out.println("Contratado por " + societyDataMessage.getPeasantFamilyContractor()
                + " " + societyDataMessage.getPeasantFamilyHelper()
                + " llegó a " + believes.getPeasantProfile().getPeasantFamilyAlias());*/
        believes.setPeasantFamilyHelper(societyDataMessage.getPeasantFamilyHelper());
        believes.setPeasantFamilyHelperDays(societyDataMessage.getAvailableDays());
        believes.setContractorStartDate(ControlCurrentDate.getInstance().getCurrentDate());
    }
}
