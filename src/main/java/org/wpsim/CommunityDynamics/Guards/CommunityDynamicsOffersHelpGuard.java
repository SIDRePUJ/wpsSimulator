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

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsState;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsDataMessage;

/**
 *
 * @author jairo
 */
public class CommunityDynamicsOffersHelpGuard extends GuardBESA  {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        CommunityDynamicsDataMessage communityDynamicsDataMessage = (CommunityDynamicsDataMessage) event.getData();
        CommunityDynamicsState state = (CommunityDynamicsState) this.getAgent().getState();
        state.addPeasantFamilyToStack(communityDynamicsDataMessage.getPeasantFamilyHelper());
    }
    
}
