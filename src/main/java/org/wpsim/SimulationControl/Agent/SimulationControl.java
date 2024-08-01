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
package org.wpsim.SimulationControl.Agent;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.SimulationControl.Data.SimulationControlState;
import org.wpsim.SimulationControl.Guards.AliveAgentGuard;
import org.wpsim.SimulationControl.Guards.DeadContainerGuard;
import org.wpsim.SimulationControl.Guards.SimulationControlGuard;
import org.wpsim.SimulationControl.Guards.DeadAgentGuard;

/**
 *
 * @author jairo
 */
public class SimulationControl extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public SimulationControl(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static SimulationControl createAgent(String alias, double passwd) throws ExceptionBESA{
        return new SimulationControl(alias, createState(), createStruct(new StructBESA()), passwd);
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("ControlAgentGuard");
        structBESA.bindGuard("ControlAgentGuard", SimulationControlGuard.class);
        structBESA.addBehavior("AliveAgentGuard");
        structBESA.bindGuard("AliveAgentGuard", AliveAgentGuard.class);
        structBESA.addBehavior("DeadAgentGuard");
        structBESA.bindGuard("DeadAgentGuard", DeadAgentGuard.class);
        structBESA.addBehavior("DeadContainerGuard");
        structBESA.bindGuard("DeadContainerGuard", DeadContainerGuard.class);
        return structBESA;
    }
    
    private static SimulationControlState createState() throws ExceptionBESA {
        return new SimulationControlState();
    }

    /**
     *
     */
    @Override
    public void setupAgent() {
        System.out.println("UPDATE: " + getAlias() + " Creado");
    }

    /**
     *
     */
    @Override
    public void shutdownAgent() {
        //((SimulationControlState) state).stopScheduler();
    }
    
}
