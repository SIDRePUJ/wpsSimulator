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
package org.wpsim.PerturbationGenerator.Agent;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.*;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Util.PeriodicDataBESA;
import org.wpsim.PerturbationGenerator.Data.PerturbationGeneratorState;
import org.wpsim.PerturbationGenerator.PeriodicGuards.NaturalPhenomena;
import org.wpsim.PerturbationGenerator.Guards.PerturbationGeneratorGuard;
import org.wpsim.ViewerLens.Util.wpsReport;

import static org.wpsim.WellProdSim.wpsStart.config;

/**
 *
 * @author jairo
 */
public class PerturbationGenerator extends AgentBESA {

    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public PerturbationGenerator(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static PerturbationGenerator createAgent(String AgentName, double passwd) throws ExceptionBESA {
        return new PerturbationGenerator(
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
        structBESA.bindGuard("PerturbationAgentGuard", PerturbationGeneratorGuard.class);
        structBESA.bindGuard("PerturbationAgentGuard", NaturalPhenomena.class);
        return structBESA;
    }

    private static PerturbationGeneratorState createState() throws ExceptionBESA {
        return new PerturbationGeneratorState();
    }

    /**
     *
     */
    @Override
    public void setupAgent() {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    config.getPerturbationAgentName()
            ).sendEvent(
                    new EventBESA(
                            NaturalPhenomena.class.getName(),
                            new PeriodicDataBESA(
                                    config.getLongProperty("control.steptime") * 100L,
                                    PeriodicGuardBESA.START_PERIODIC_CALL
                            )
                    )
            );
        } catch (ExceptionBESA e) {
            wpsReport.error(e.getMessage(), "getPerturbationAgentName NO OK");
        }
    }

    /**
     *
     */
    @Override
    public void shutdownAgent() {

    }

}
