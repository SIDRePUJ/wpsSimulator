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
package org.wpsim.Society;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.Society.Data.SocietyAgentState;
import org.wpsim.Society.Guards.SocietyAgentOffersHelpGuard;
import org.wpsim.Society.Guards.SocietyAgentRequestHelpGuard;

/**
 *
 * @author jairo
 */
public class SocietyAgent extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public SocietyAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static SocietyAgent createAgent(String alias, double passwd) throws ExceptionBESA{        
        SocietyAgent societyAgent = new SocietyAgent(alias, createState(), createStruct(new StructBESA()), passwd);
        return societyAgent;
        
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("SocietyAgentGuard");
        structBESA.bindGuard("SocietyAgentGuard", SocietyAgentOffersHelpGuard.class);
        structBESA.bindGuard("SocietyAgentGuard", SocietyAgentRequestHelpGuard.class);
        return structBESA;
    }
    
    private static SocietyAgentState createState() throws ExceptionBESA {
        SocietyAgentState societyAgentState = new SocietyAgentState();
        return societyAgentState;
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
