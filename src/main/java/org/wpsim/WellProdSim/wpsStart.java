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
import BESA.Log.ReportBESA;
import BESA.Util.PeriodicDataBESA;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.DefaultParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wpsim.BankOffice.Agent.BankOffice;
import org.wpsim.SimulationControl.Agent.SimulationControl;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.CivicAuthority.Agent.CivicAuthority;
import org.wpsim.MarketPlace.Agent.MarketPlace;
import org.wpsim.PeasantFamily.Guards.Status.StatusGuard;
import org.wpsim.PeasantFamily.PeriodicGuards.HeartBeatGuard;
import org.wpsim.PeasantFamily.Agent.PeasantFamily;
import org.wpsim.PerturbationGenerator.PeriodicGuards.NaturalPhenomena;
import org.wpsim.PerturbationGenerator.Agent.PerturbationGenerator;
import org.wpsim.SimulationControl.Util.SimulationParams;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.CommunityDynamics.Agent.CommunityDynamics;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.ViewerLens.Agent.ViewerLens;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 */
public class wpsStart {

    public static wpsConfig config;
    public static AdmBESA adm = null;
    private static int PLAN_ID = 0;
    public static int peasantFamiliesAgents;
    public static boolean started = false;
    public static int CREATED_AGENTS = 0;
    public static final long startTime = System.currentTimeMillis();
    private static final List<PeasantFamily> PEASANT_FAMILIES = new ArrayList<>();
    public static CommandLine cmd;
    public static SimulationParams params = new SimulationParams();

    /**
     * The main method to start the simulation.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Set arguments to config
        setArgumentsConfig(args);
        // Set initial config of simulation
        config = wpsConfig.getInstance();
        // Create BESA Container
        createContainer();
        // Set initial date of simulation
        ControlCurrentDate.getInstance().setCurrentDate(config.getStartSimulationDate());
        // Print header for simulation
        printHeader();
        //showRunningAgents();
        startSimulation();
    }

    private static void createContainer() {
        String path = "server_" + wpsStart.config.getStringProperty("pfagent.env") + "_" + params.mode + ".xml";
        System.out.println("Starting in " + path + " mode");

        adm = AdmBESA.getInstance(path);

        System.out.println(adm.getConfigBESA());
    }

    private static void setArgumentsConfig(String[] args) {

        Options options = new Options();
        // Definir los parámetros esperados
        options.addOption(new Option("m", "mode", true, "Mode of operation"));
        options.addOption(new Option("a", "agents", true, "Number of agents"));
        options.addOption(new Option("d", "money", true, "Amount of money"));
        options.addOption(new Option("l", "land", true, "Land area"));
        options.addOption(new Option("p", "personality", true, "Type of personality"));
        options.addOption(new Option("t", "tools", true, "Type of tools"));
        options.addOption(new Option("s", "seeds", true, "Type of seeds"));
        options.addOption(new Option("w", "water", true, "Amount of water"));
        options.addOption(new Option("i", "irrigation", true, "Irrigation enabled"));
        options.addOption(new Option("e", "emotions", true, "Enable Emotions"));
        options.addOption(new Option("r", "training", true, "Enable Training"));

        // Crear el parser para los argumentos
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            // Parsear los argumentos
            cmd = parser.parse(options, args);
            if (cmd.hasOption("agents")) {
                peasantFamiliesAgents = Integer.parseInt(cmd.getOptionValue("agents"));
            }
            if (cmd.hasOption("mode")) {
                params.mode = cmd.getOptionValue("mode");
            }
            if (cmd.hasOption("emotions")) {
                params.emotions = Integer.parseInt(cmd.getOptionValue("emotions"));
            }
            if (cmd.hasOption("money")) {
                params.money = Integer.parseInt(cmd.getOptionValue("money"));
            }
            if (cmd.hasOption("irrigation")) {
                params.irrigation = Integer.parseInt(cmd.getOptionValue("irrigation"));
            }
            if (cmd.hasOption("land")) {
                params.land = Integer.parseInt(cmd.getOptionValue("land"));
            }
            if (cmd.hasOption("personality")) {
                params.personality = Double.parseDouble(cmd.getOptionValue("personality"));
            }
            if (cmd.hasOption("tools")) {
                params.tools = Integer.parseInt(cmd.getOptionValue("tools"));
            }
            if (cmd.hasOption("seeds")) {
                params.seeds = Integer.parseInt(cmd.getOptionValue("seeds"));
            }
            if (cmd.hasOption("water")) {
                params.water = Integer.parseInt(cmd.getOptionValue("water"));
            }
            if (cmd.hasOption("training")) {
                params.water = Integer.parseInt(cmd.getOptionValue("training"));
            }

        } catch (Exception e) {
            // Mostrar ayuda si hay un error en el parseo
            System.out.println(e.getMessage());
            formatter.printHelp("wpsim", options);
            System.exit(1);
        }
    }

    private static void startSimulation() {

        switch (params.mode) {
            case "p01" -> {
                createServices();
                showRunningAgents();
            }
            case "p02" -> {
                createPeasants(0, peasantFamiliesAgents);
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                startAgents();
                showRunningAgents();
            }
            case "p03" -> {
                createPeasants(1000, 1000+peasantFamiliesAgents);
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                startAgents();
            }
            case "p04" -> {
                createPeasants(2000, 2000+peasantFamiliesAgents);
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                startAgents();
            }
            case "web" -> {
                // Single mode
                createServices();
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                createPeasants(1, peasantFamiliesAgents);
            }
            case "single" -> {
                // Single benchmark mode
                createServices();
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                createPeasants(1, peasantFamiliesAgents);
                startAgents();
                showRunningAgents();
            }
            default -> System.out.println("No se reconoce el nombre del contendor BESA " + params.mode);
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
        /*System.out.println("Contenedores activos");
        Enumeration<String> containers = adm.getAdmAliasList();
        while(containers.hasMoreElements()){
            System.out.println(containers.nextElement());
        }*/

    }

    /**
     * Creates the peasant family agents.
     */
    private static void createPeasants(int min, int max) {
        System.out.println("entrando a crear agentes.... " + min + " " + max );
        try {
            for (int i = min; i <= max; i++) {
                PeasantFamily peasantFamily = new PeasantFamily(
                        config.getUniqueFarmerName(),
                        config.getFarmerProfile()
                );
                CREATED_AGENTS++;
                PEASANT_FAMILIES.add(peasantFamily);
                System.out.println("Creado " + peasantFamily.getAlias());
            }
        } catch (Exception ex) {
            System.out.println("error creando peasants" + ex.getMessage());
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
            showRunningAgents();
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