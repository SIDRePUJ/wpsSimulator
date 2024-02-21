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
package org.wpsim.Control;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.Control.Data.ControlAgentState;
import org.wpsim.Control.Guards.AliveAgentGuard;
import org.wpsim.Control.Guards.ControlAgentGuard;
import org.wpsim.Control.Guards.DeadAgentGuard;

/**
 *
 * @author jairo
 */
public class ControlAgent extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public ControlAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static ControlAgent createAgent(String alias, double passwd) throws ExceptionBESA{        
        return new ControlAgent(alias, createState(), createStruct(new StructBESA()), passwd);
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("ControlAgentGuard");
        structBESA.bindGuard("ControlAgentGuard", ControlAgentGuard.class);
        structBESA.addBehavior("AliveAgentGuard");
        structBESA.bindGuard("AliveAgentGuard", AliveAgentGuard.class);
        structBESA.addBehavior("DeadAgentGuard");
        structBESA.bindGuard("DeadAgentGuard", DeadAgentGuard.class);
        return structBESA;
    }
    
    private static ControlAgentState createState() throws ExceptionBESA {
        return new ControlAgentState();
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
        ((ControlAgentState) state).stopScheduler();
    }
    
}
