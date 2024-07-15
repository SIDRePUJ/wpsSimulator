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
import BESA.Kernel.Agent.*;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Util.PeriodicDataBESA;
import org.wpsim.CivicAuthority.Data.CivicAuthorityState;
import org.wpsim.CivicAuthority.Guards.CivicAuthorityHelpGuard;
import org.wpsim.CivicAuthority.Guards.CivicAuthorityLandGuard;
import org.wpsim.CivicAuthority.Guards.CivicAuthorityReleaseLandGuard;
import org.wpsim.CivicAuthority.Guards.TrainingOfferGuard;
import org.wpsim.PeasantFamily.PeriodicGuards.HeartBeatGuard;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.WellProdSim.wpsStart;

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
        structBESA.addBehavior("GovernmentAgentBehavior");
        structBESA.bindGuard("GovernmentAgentBehavior", CivicAuthorityHelpGuard.class);
        structBESA.bindGuard("GovernmentAgentBehavior", CivicAuthorityLandGuard.class);
        structBESA.bindGuard("GovernmentAgentBehavior", CivicAuthorityReleaseLandGuard.class);

        structBESA.addBehavior("TrainingOfferBehavior");
        structBESA.bindGuard("TrainingOfferBehavior", TrainingOfferGuard.class);

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
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getGovernmentAgentName()
            ).sendEvent(
                    new EventBESA(
                            TrainingOfferGuard.class.getName(),
                            new PeriodicDataBESA(
                                    800,
                                    PeriodicGuardBESA.START_PERIODIC_CALL
                            )
                    )
            );
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    @Override
    public void shutdownAgent() {

    }

}
