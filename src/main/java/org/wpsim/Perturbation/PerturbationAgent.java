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
package org.wpsim.Perturbation;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.Perturbation.Data.wpsPerturbationState;
import org.wpsim.Perturbation.Guards.NaturalPhenomena;
import org.wpsim.Perturbation.Guards.wpsPerturbationGuard;
import org.wpsim.Simulator.wpsStart;

/**
 *
 * @author jairo
 */
public class PerturbationAgent extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public PerturbationAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static PerturbationAgent createAgent(String AgentName, double passwd) throws ExceptionBESA {
        return new PerturbationAgent(
                AgentName,
                createState(),
                createStruct(
                        new StructBESA()
                ),
                passwd
        );

    }

    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("PerturbationAgentGuard");
        structBESA.bindGuard("PerturbationAgentGuard", wpsPerturbationGuard.class);
        structBESA.bindGuard("PerturbationAgentGuard", NaturalPhenomena.class);
        return structBESA;
    }

    private static wpsPerturbationState createState() throws ExceptionBESA {
        return new wpsPerturbationState();
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
