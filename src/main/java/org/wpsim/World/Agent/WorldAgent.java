package org.wpsim.World.Agent;

import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;

/**
 * AgentBESA implementation for the world agent
 */
public class WorldAgent extends AgentBESA {
    
    private static final double TH = 0.91;
    
    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @throws KernelAgentExceptionBESA
     */
    public WorldAgent(String alias, StateBESA state, StructBESA structAgent) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, TH);
    }

    /**
     *
     */
    @Override
    public void setupAgent() {
    }

    /**
     *
     */
    @Override
    public void shutdownAgent() {

    }
}
