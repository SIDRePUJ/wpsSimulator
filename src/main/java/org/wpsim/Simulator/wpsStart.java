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
package org.wpsim.Simulator;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Bank.BankAgent;
import org.wpsim.Control.ControlAgent;
import org.wpsim.Control.ControlCurrentDate;
import org.wpsim.Market.MarketAgent;
import org.wpsim.PeasantFamily.Guards.StatusGuard;
import org.wpsim.PeasantFamily.Guards.HeartBeatGuard;
import org.wpsim.PeasantFamily.Agent.PeasantFamilyBDIAgent;
import org.wpsim.Perturbation.PerturbationAgent;
import org.wpsim.Society.SocietyAgent;
import org.wpsim.Viewer.WebsocketServer;
import org.wpsim.Viewer.wpsReport;
import org.wpsim.Viewer.wpsViewerAgent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class wpsStart {

    private static int PLAN_ID = 0;
    final public static double PASSWD = 0.91;
    public static wpsConfig config;
    public static int peasantFamiliesAgents = 21;
    public static boolean started = false;
    private final static int SIMULATION_TIME = 16;
    public final static int DAYS_TO_CHECK = 7;
    public static final long startTime = System.currentTimeMillis();
    static private List<PeasantFamilyBDIAgent> peasantFamilyBDIAgents = new ArrayList<>();

    /**
     * The main method to start the simulation.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        config = wpsConfig.getInstance();

        // Set init date of simulation
        ControlCurrentDate.getInstance().setCurrentDate(config.getStartSimulationDate());

        try {
            wpsViewerAgent viewerAgent = wpsViewerAgent.createAgent(config.getViewerAgentName(), PASSWD);
            viewerAgent.start();
            ControlAgent controlAgent = ControlAgent.createAgent(config.getControlAgentName(), PASSWD);
            controlAgent.start();
            SocietyAgent societyAgent = SocietyAgent.createAgent(config.getSocietyAgentName(), PASSWD);
            societyAgent.start();
            BankAgent bankAgent = BankAgent.createBankAgent(config.getBankAgentName(), PASSWD);
            bankAgent.start();
            MarketAgent marketAgent = MarketAgent.createAgent(config.getMarketAgentName(), PASSWD);
            marketAgent.start();
            PerturbationAgent perturbationAgent = PerturbationAgent.createAgent(PASSWD);
            perturbationAgent.start();

            for (int i = 0; i < peasantFamiliesAgents; i++) {
                PeasantFamilyBDIAgent peasantFamilyBDIAgent = new PeasantFamilyBDIAgent(
                        config.getUniqueFarmerName(),
                        config.getFarmerProfile()
                );
                peasantFamilyBDIAgents.add(peasantFamilyBDIAgent);
            }

            printHeader();
            //startAgents();

        } catch (Exception ex) {
            wpsReport.error(ex, "wpsStart");
        }
    }

    public static void startAgents(){
        // Simulation Start
        try {
            startPFAgents(peasantFamilyBDIAgents);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the next plan ID.
     *
     * @return the next plan ID
     */
    public static int getPlanID() {
        return ++PLAN_ID;
    }

    /**
     * Starts all the agents and begins the simulation.
     *
     * @param peasantFamilies the list of peasant family agents
     * @throws ExceptionBESA if there is an exception while starting the agents
     */
    private static void startPFAgents(List<PeasantFamilyBDIAgent> peasantFamilies) throws ExceptionBESA {

        try {
            // Starting families agents
            for (PeasantFamilyBDIAgent peasantFamily : peasantFamilies) {
                peasantFamily.start();
                wpsReport.info(peasantFamily.getAlias() + " Started", "wpsStart");
            }
            // first heart beat to families
            for (int i = 1; i <= peasantFamiliesAgents; i++) {
                AdmBESA adm = AdmBESA.getInstance();
                EventBESA eventBesa = new EventBESA(HeartBeatGuard.class.getName(), null);
                AgHandlerBESA agHandler = adm.getHandlerByAlias("PeasantFamily_" + i);
                agHandler.sendEvent(eventBesa);
            }
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "wpsStart");
        }

    }

    /**
     * Stops the simulation after a specified time.
     *
     * @throws ExceptionBESA if there is an exception while stopping the agents
     */
    @SuppressWarnings("rawtypes")
    public static void stopSimulation() {
        getStatus();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        wpsReport.info("Simulation finished in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds.\n\n\n\n", "wpsStart");
        System.exit(0);

    }

    public static void stopSimulationByTime() throws ExceptionBESA {

        try {
            Thread.sleep((60 * SIMULATION_TIME) * 1000);
            stopSimulation();
        } catch (InterruptedException e) {
            wpsReport.error(e.getMessage(), "wpsStart");
        }

    }

    /**
     * Sends status events to the peasant family agents.
     */
    public static void getStatus() {
        // first heart beat to families
        try {
            for (int i = 1; i <= peasantFamiliesAgents; i++) {
                AdmBESA adm = AdmBESA.getInstance();
                EventBESA eventBesa = new EventBESA(StatusGuard.class.getName(), null);
                AgHandlerBESA agHandler = adm.getHandlerByAlias("PeasantFamily_" + i);
                agHandler.sendEvent(eventBesa);
            }
            Thread.sleep(5000);
        } catch (ExceptionBESA | InterruptedException ex) {
            wpsReport.error(ex, "wpsStart");
        }
    }

    /**
     * Print header at Simulation begin
     */
    public static void printHeader() {

        wpsReport.info("""
                                       
                                       
                                       
                 * ==========================================================================
                 *   __      __ _ __   ___           WellProdSim                            *
                 *   \\ \\ /\\ / /| '_ \\ / __|          @version 1.0                           *
                 *    \\ V  V / | |_) |\\__ \\          @since 2023                            *
                 *     \\_/\\_/  | .__/ |___/                                                 *
                 *             | |                   @author Jairo Serrano                  *
                 *             |_|                   @author Enrique Gonzalez               *
                 * ==========================================================================
                 * Social Simulator used to estimate productivity and well-being of peasant *
                 * families. It is event oriented, high concurrency, heterogeneous time     *
                 * management and emotional reasoning BDI.                                  *
                 * ==========================================================================
                 
                """, "wpsStart");
    }

    public static long time() {
        return System.currentTimeMillis() - startTime;
    }
}
