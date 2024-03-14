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
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;

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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();

        String landName = fromGovernmentMessage.getLandName();
        Map<String, String> assignedLands = fromGovernmentMessage.getAssignedLands();

        //System.out.println("REC: peasant family: " + believes.getAlias() + " Assigned land: " + landName + " Assigned lands: " + assignedLands);

        if (landName == null || landName.equals("")) {
            if (landName == null) {
                believes.setWorkerWithoutLand();
            } else {
                System.err.println("Error: Received empty land name.");
            }
        } else {
            believes.getPeasantProfile().setPeasantFamilyLandAlias(landName);
            believes.setAssignedLands(assignedLands);
            believes.getPeasantProfile().increaseToolsNeeded(fromGovernmentMessage.getAssignedLands().size());
        }
    }

}
