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
package org.wpsim.Viewer;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.AgentBESA;
import BESA.Kernel.Agent.KernelAgentExceptionBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.Agent.StructBESA;
import org.wpsim.Viewer.Data.wpsViewerAgentState;
import org.wpsim.Viewer.Guards.wpsViewerAgentGuard;
import org.wpsim.Viewer.Server.WebsocketServer;

/**
 *
 * @author jairo
 */
public class wpsViewerAgent extends AgentBESA {

    Thread websocketServer;
    /**
     *
     * @param alias
     * @param state
     * @param structAgent
     * @param passwd
     * @throws KernelAgentExceptionBESA
     */
    public wpsViewerAgent(String alias, StateBESA state, StructBESA structAgent, double passwd) throws KernelAgentExceptionBESA {
        super(alias, state, structAgent, passwd);
    }

    public static wpsViewerAgent createAgent(String alias, double passwd) throws ExceptionBESA{        
        return new wpsViewerAgent(alias, createState(), createStruct(new StructBESA()), passwd);
    }
    
    private static StructBESA createStruct(StructBESA structBESA) throws ExceptionBESA {
        structBESA.addBehavior("wpsViewerAgentGuard");
        structBESA.bindGuard("wpsViewerAgentGuard", wpsViewerAgentGuard.class);
        return structBESA;
    }
    
    private static wpsViewerAgentState createState() throws ExceptionBESA {
        return new wpsViewerAgentState();
    }
    
    /**
     *
     */
    @Override
    public void setupAgent() {
        Thread websocketServerThread = new Thread(WebsocketServer.getInstance());
        websocketServerThread.start();

    }

    /**
     *
     */
    @Override
    public void shutdownAgent() {
    }
    
}
