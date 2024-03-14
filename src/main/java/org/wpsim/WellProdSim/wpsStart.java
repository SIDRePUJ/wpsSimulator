/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 * \ V  V / | |_) |\__ \  *    @since 2023                                  *
 * \_/\_/  | .__/ |___/   *                                                 *
 * | |                    *    @author Jairo Serrano                        *
 * |_|                    *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.WellProdSim;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Util.PeriodicDataBESA;
import org.wpsim.BankOffice.Agent.BankOffice;
import org.wpsim.SimulationControl.Agent.SimulationControl;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.CivicAuthority.Agent.CivicAuthority;
import org.wpsim.MarketPlace.Agent.MarketPlace;
import org.wpsim.PeasantFamily.Guards.Internal.StatusGuard;
import org.wpsim.PeasantFamily.Guards.Internal.HeartBeatGuard;
import org.wpsim.PeasantFamily.Agent.PeasantFamily;
import org.wpsim.PerturbationGenerator.PeriodicGuards.NaturalPhenomena;
import org.wpsim.PerturbationGenerator.Agent.PerturbationGenerator;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.CommunityDynamics.Agent.CommunityDynamics;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.ViewerLens.Agent.ViewerLens;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class wpsStart {

    public static wpsConfig config;
    private static int PLAN_ID = 0;
    public static int peasantFamiliesAgents;
    public static boolean started = false;
    public final static int DEFAULT_AGENTS_TO_TEST = 32;
    public static int CREATED_AGENTS = 0;
    public static final long startTime = System.currentTimeMillis();
    private static final List<PeasantFamily> PEASANT_FAMILIES = new ArrayList<>();

    /**
     * The main method to start the simulation.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Set initial config of simulation
        config = wpsConfig.getInstance();
        // Set initial date of simulation
        ControlCurrentDate.getInstance().setCurrentDate(
                config.getStartSimulationDate()
        );
        // Print header for simulation
        printHeader();
        // Set arguments to config
        setArgumentsConfig(args);
        //showRunningAgents();
    }

    private static void setArgumentsConfig(String[] args) {
        AdmBESA admLocal = null;
        if (args.length > 0) {
            // Agents to run simulation
            try {
                peasantFamiliesAgents = Integer.parseInt(args[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                peasantFamiliesAgents = DEFAULT_AGENTS_TO_TEST;
            }
            // Cluster mode
            admLocal = AdmBESA.getInstance("server_" + args[0] + ".xml");
            System.out.println("Centralized " + admLocal.getConfigBESA().getAliasContainer());
            System.out.println("Starting in " + args[0] + " mode");
            System.out.println("Simulating " + peasantFamiliesAgents + " agents");
            switch (args[0]) {
                case "main" -> {
                    createServices();
                }
                case "peasants_01" -> {
                    createPeasants(0, 100);
                    startAgents();
                }
                case "peasants_02" -> {
                    createPeasants(101, 200);
                    startAgents();
                }
                case "peasants_03" -> {
                    createPeasants(201, 300);
                    startAgents();
                }
                case "local" -> {
                    // Single mode
                    createServices();
                    createPeasants(1, peasantFamiliesAgents);
                }
                case "single" -> {
                    // Single benchmark mode
                    createServices();
                    createPeasants(1, peasantFamiliesAgents);
                    startAgents();
                }
                default -> System.out.println("No se reconoce el nombre del contendor BESA " + args[0]);
            }
        } else {
            System.out.println("No hay contenedores válidos: local <cantidad>");
            System.exit(0);
        }
    }

    private static void showRunningAgents() {
        var idList = AdmBESA.getInstance().getIdList();
        while (idList.hasMoreElements()) {
            String id = (String) idList.nextElement();
            try {
                System.out.println("ID: " + id + " Alias " + AdmBESA.getInstance().getHandlerByAid(id).getAlias());
            } catch (ExceptionBESA e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates the peasant family agents.
     */
    private static void createPeasants(int min, int max) {
        try {
            for (int i = min; i <= max; i++) {
                PeasantFamily peasantFamily = new PeasantFamily(
                        config.getUniqueFarmerName(),
                        config.getFarmerProfile()
                );
                CREATED_AGENTS++;
                PEASANT_FAMILIES.add(peasantFamily);
            }
        } catch (Exception ex) {
            wpsReport.error(ex, "wpsStart");
        }

    }

    /**
     * Creates the services for peasant family agents.
     */
    private static void createServices() {
        try {
            ViewerLens viewerAgent = ViewerLens.createAgent(config.getViewerAgentName(), config.getDoubleProperty("control.passwd"));
            viewerAgent.start();
            SimulationControl simulationControl = SimulationControl.createAgent(config.getControlAgentName(), config.getDoubleProperty("control.passwd"));
            simulationControl.start();
            CommunityDynamics communityDynamics = CommunityDynamics.createAgent(config.getSocietyAgentName(), config.getDoubleProperty("control.passwd"));
            communityDynamics.start();
            CivicAuthority civicAuthority = CivicAuthority.createAgent(config.getGovernmentAgentName(), config.getDoubleProperty("control.passwd"));
            civicAuthority.start();
            BankOffice bankOffice = BankOffice.createBankAgent(config.getBankAgentName(), config.getDoubleProperty("control.passwd"));
            bankOffice.start();
            MarketPlace marketPlace = MarketPlace.createAgent(config.getMarketAgentName(), config.getDoubleProperty("control.passwd"));
            marketPlace.start();
            // Starting Perturbation Agent
            PerturbationGenerator perturbationGenerator = PerturbationGenerator.createAgent(config.getPerturbationAgentName(), config.getDoubleProperty("control.passwd"));
            perturbationGenerator.start();
        } catch (Exception ex) {
            wpsReport.error(ex.getMessage(), "wpsStart_noOK");
        }
    }

    /**
     * Starts the peasant family agents.
     */
    public static void startAgents() {
        // Simulation Start
        try {
            startPFAgents(PEASANT_FAMILIES);
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
    private static void startPFAgents(List<PeasantFamily> peasantFamilies) throws ExceptionBESA {
        try {
            // Starting families agents
            for (PeasantFamily peasantFamily : peasantFamilies) {
                peasantFamily.start();
                wpsReport.info(peasantFamily.getAlias() + " Started", "wpsStart");
            }
            // first heart beat to families
            for (int i = 1; i <= peasantFamiliesAgents; i++) {
                AdmBESA.getInstance().getHandlerByAlias(
                        "PeasantFamily_" + i
                ).sendEvent(
                        new EventBESA(
                                HeartBeatGuard.class.getName(),
                                new PeriodicDataBESA(
                                        config.getLongProperty("control.steptime"),
                                        PeriodicGuardBESA.START_PERIODIC_CALL
                                )
                        )
                );
            }
            // Start Perturbation Agent Periodic Guard
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
        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, "wpsStart");
        }
    }

    /**
     * Stops the simulation after a specified time.
     */
    public static void stopSimulation() {
        System.out.println("All agents stopped");
        getStatus();
        wpsReport.info("Simulation finished in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds.\n\n\n\n", "wpsStart");
        System.exit(0);
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

        System.out.println("""
                                       
                                    
                 * ==========================================================================
                 *   __      __ _ __   ___           WellProdSim                            *
                 *   \\ \\ /\\ / /| '_ \\ / __|      @version 1.0                           *
                 *    \\ V  V / | |_) |\\__ \\       @since 2023                            *
                 *     \\_/\\_/  | .__/ |___/                                               *
                 *             | |                   @author Jairo Serrano                  *
                 *             |_|                   @author Enrique Gonzalez               *
                 * ==========================================================================
                 * Social Simulator used to estimate productivity and well-being of peasant *
                 * families. It is event oriented, high concurrency, heterogeneous time     *
                 * management and emotional reasoning BDI.                                  *
                 * ==========================================================================
                 
                """);
    }

    public static long getTime() {
        return System.currentTimeMillis() - startTime;
    }

}

/**
 * Bienestar depende de salud y mood - además de tener un ingreso sostenible, con eso siembra más fácil y rápido
 * El ingreso sostenible es un variable moduladora de las variables que alimentan el bienestar
 */