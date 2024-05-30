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
package org.wpsim.PeasantFamily.Guards.FromCivicAuthority;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;

import java.util.Map;

/**
 *
 * @author jairo
 */
public class FromCivicAuthorityGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        FromCivicAuthorityMessage fromCivicAuthorityMessage = (FromCivicAuthorityMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();

        String landName = fromCivicAuthorityMessage.getLandName();
        Map<String, String> assignedLands = fromCivicAuthorityMessage.getAssignedLands();

        if (landName == null || landName.equals("")) {
            if (landName == null) {
                believes.setWorkerWithoutLand();
            } else {
                System.err.println("Error: Received empty land name.");
            }
        } else {
            believes.getPeasantProfile().setPeasantFamilyLandAlias(landName);
            believes.setAssignedLands(assignedLands);
            believes.getPeasantProfile().increaseToolsNeeded(fromCivicAuthorityMessage.getAssignedLands().size());
        }
    }
}