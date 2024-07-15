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
package org.wpsim.ViewerLens.Agent;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.ViewerLens.Data.ViewerLensState;
import org.wpsim.ViewerLens.Guards.ViewerLensGuard;
import org.wpsim.ViewerLens.Server.WebsocketServer;
import org.wpsim.WellProdSim.Config.wpsConfig;

/**
 *
 * @author jairo
 */
public class ViewerLens extends AgentBESA {

    Thread websocketServer;
    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public ViewerLens(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static ViewerLens createAgent(String alias, double passwd) throws ExceptionBESA{
        return new ViewerLens(alias, createState(), createStruct(new StructBESA()), passwd);
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("wpsViewerAgentGuard");
        structBESA.bindGuard("wpsViewerAgentGuard", ViewerLensGuard.class);
        return structBESA;
    }
    
    private static ViewerLensState createState() throws ExceptionBESA {
        return new ViewerLensState();
    }
    
    /**
     *
     */
    @Override
    public void setupAgent() {
        if (wpsConfig.getInstance().getBooleanProperty("viewer.webui")) {
            Thread websocketServerThread = new Thread(WebsocketServer.getInstance());
            websocketServerThread.start();
        }
    }

    /**
     *
     */
    @Override
    public void shutdownAgent() {
    }
    
}
