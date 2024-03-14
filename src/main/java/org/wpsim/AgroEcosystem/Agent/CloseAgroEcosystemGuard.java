package org.wpsim.AgroEcosystem.Agent;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;

/**
 * BESA world's guard, holds the actions that receive from the peasant agent
 */
public class CloseAgroEcosystemGuard extends GuardBESA {

    /**
     *
     * @param eventBESA
     */
    @Override
    public synchronized void funcExecGuard(EventBESA eventBESA) {
        AgroEcosystemMessage agroEcosystemMessage = (AgroEcosystemMessage) eventBESA.getData();
        System.out.println("Cerrando agent 🚩🚩🚩 " +  this.agent.getAlias());
        this.agent.shutdownAgent();
    }
}
