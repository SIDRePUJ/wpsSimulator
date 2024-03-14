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
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.CommunityDynamics.Data.CommunityDynamicsDataMessage;

/**
 * Peasant Helper Guard
 * @author jairo
 */
public class PeasantWorkerContractFinishedGuard extends GuardBESA {

    /**
     * Peasant Worker Contract Finished Guard
     * @param event event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        CommunityDynamicsDataMessage communityDynamicsDataMessage = (CommunityDynamicsDataMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();

        //System.out.println(believes.getPeasantProfile().getPeasantFamilyAlias() + " Pagando por el contrato a " + believes.getPeasantFamilyHelper());
        believes.setContractor("");
        believes.setPeasantFamilyHelper("");
        believes.setDaysToWorkForOther(0);
        believes.setAskedForContractor(false);
        believes.getPeasantProfile().decreaseMoney(250000);
        believes.setAskedForCollaboration(false);
    }
}
