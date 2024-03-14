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
package org.wpsim.CivicAuthority.Agent;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.CivicAuthority.Data.CivicAuthorityState;
import org.wpsim.CivicAuthority.Guards.CivicAuthorityHelpGuard;
import org.wpsim.CivicAuthority.Guards.CivicAuthorityLandGuard;
import org.wpsim.CivicAuthority.Guards.CivicAuthorityReleaseLandGuard;

/**
 *
 * @author jairo
 */
public class CivicAuthority extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public CivicAuthority(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static CivicAuthority createAgent(String alias, double passwd) throws ExceptionBESA {
        return new CivicAuthority(alias, createState(), createStruct(new StructBESA()), passwd);
    }

    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("GovernmentAgentGuards");
        structBESA.bindGuard("GovernmentAgentGuards", CivicAuthorityHelpGuard.class);
        structBESA.bindGuard("GovernmentAgentGuards", CivicAuthorityLandGuard.class);
        structBESA.bindGuard("GovernmentAgentGuards", CivicAuthorityReleaseLandGuard.class);
        return structBESA;
    }

    private static CivicAuthorityState createState() throws ExceptionBESA {
        return new CivicAuthorityState();
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
