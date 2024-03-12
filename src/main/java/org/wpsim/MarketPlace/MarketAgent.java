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
package org.wpsim.MarketPlace;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.MarketPlace.Data.MarketAgentState;
import org.wpsim.MarketPlace.Guards.MarketAgentGuard;
import org.wpsim.MarketPlace.Guards.MarketInfoAgentGuard;

/**
 *
 * @author jairo
 */
public class MarketAgent extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public MarketAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static MarketAgent createAgent(String alias, double passwd) throws ExceptionBESA{
        return new MarketAgent(alias, createState(), createStruct(new StructBESA()), passwd);
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("MarketAgentGuard");
        structBESA.bindGuard("MarketAgentGuard", MarketAgentGuard.class);
        structBESA.bindGuard("MarketAgentGuard", MarketInfoAgentGuard.class);
        return structBESA;
    }
    
    private static MarketAgentState createState() throws ExceptionBESA {
        return new MarketAgentState();
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
