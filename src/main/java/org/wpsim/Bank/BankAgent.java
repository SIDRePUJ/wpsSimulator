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
package org.wpsim.Bank;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.Bank.Guard.BankAgentGuard;

/**
 *
 * @author jairo
 */
public class BankAgent extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public BankAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static BankAgent createBankAgent(String alias, double passwd) throws ExceptionBESA{        
        BankAgent bankAgent = new BankAgent(alias, createState(), createStruct(new StructBESA()), passwd);
        return bankAgent;
        
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("BankAgentGuard");
        structBESA.bindGuard("BankAgentGuard", BankAgentGuard.class);
        return structBESA;
    }
    
    private static BankAgentState createState() throws ExceptionBESA {
        BankAgentState bankAgentState = new BankAgentState();
        return bankAgentState;
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
