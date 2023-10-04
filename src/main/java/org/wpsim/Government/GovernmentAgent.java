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
package org.wpsim.Government;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.PeasantFamily.Guards.StatusGuard;

/**
 *
 * @author jairo
 */
public class GovernmentAgent extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public GovernmentAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static GovernmentAgent createAgent(String alias, double passwd) throws ExceptionBESA{
        GovernmentAgent governmentAgent = new GovernmentAgent(alias, createState(), createStruct(new StructBESA()), passwd);
        return governmentAgent;
        
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("GovernmentAgentGuards");
        structBESA.bindGuard("GovernmentAgentGuards", GovernmentAgentHelpGuard.class);
        structBESA.bindGuard("GovernmentAgentGuards", GovernmentAgentLandGuard.class);
        return structBESA;
    }
    
    private static GovernmentAgentState createState() throws ExceptionBESA {
        GovernmentAgentState governmentAgentState = new GovernmentAgentState();
        return governmentAgentState;
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
