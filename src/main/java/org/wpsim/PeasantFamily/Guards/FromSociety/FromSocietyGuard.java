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
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentMessage;
import org.wpsim.Society.Data.SocietyDataMessage;

import java.util.Map;

/**
 *
 * @author jairo
 */
public class FromSocietyGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        SocietyDataMessage societyDataMessage = (SocietyDataMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) state.getBelieves();
        System.out.println("Ayudando a " + societyDataMessage.getPeasantFamilyHelper());
        believes.setPeasantFamilyToHelp(societyDataMessage.getPeasantFamilyHelper());
        believes.setDaysToWorkForOther(societyDataMessage.getAvailableDays());
        //believes.getPeasantProfile().setPurpose("worker");
    }
}
