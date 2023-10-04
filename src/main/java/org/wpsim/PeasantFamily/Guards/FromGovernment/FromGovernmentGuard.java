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
package org.wpsim.PeasantFamily.Guards.FromGovernment;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jairo
 */
public class FromGovernmentGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        FromGovernmentMessage fromGovernmentMessage = (FromGovernmentMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) state.getBelieves();

        String landName = fromGovernmentMessage.getLandName();
        Map<String, String> assignedLands = fromGovernmentMessage.getAssignedLands();

        /*System.out.println("Peaasant family: " + believes.getPeasantProfile().getPeasantFamilyAlias()
                + " Assigned farm: " + landName + " Assigned lands: " + assignedLands);*/

        if (landName != null && !landName.equals("")) {
            believes.getPeasantProfile().setPeasantFamilyLandAlias(landName);
            believes.setAssignedLands(assignedLands);
        } else {
            System.err.println("Error: Received null or empty land name.");
        }

    }
}
