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

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.Society.Data.SocietyAgentState;
import org.wpsim.Society.Data.SocietyDataMessage;
import org.wpsim.Viewer.Data.wpsReport;

/**
 *
 * @author jairo
 */
public class SocietyAgentOffersHelpGuard extends GuardBESA  {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        SocietyDataMessage societyDataMessage = (SocietyDataMessage) event.getData();
        SocietyAgentState state = (SocietyAgentState) this.getAgent().getState();
        state.addPeasantFamilyToStack(societyDataMessage.getPeasantFamilyHelper());
    }
    
}
