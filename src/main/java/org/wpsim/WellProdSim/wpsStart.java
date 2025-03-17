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
import BESA.Kernel.System.AdmBESA;
import org.apache.commons.cli.*;
import org.wpsim.BankOffice.Agent.BankOffice;
import org.wpsim.CivicAuthority.Agent.CivicAuthority;
import org.wpsim.CommunityDynamics.Agent.CommunityDynamics;
import org.wpsim.MarketPlace.Agent.MarketPlace;
import org.wpsim.PeasantFamily.Agent.PeasantFamily;
import org.wpsim.PerturbationGenerator.Agent.PerturbationGenerator;
import org.wpsim.SimulationControl.Agent.SimulationControl;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.SimulationControl.Util.SimulationParams;
import org.wpsim.ViewerLens.Agent.ViewerLens;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Config.wpsConfig;

import java.util.Enumeration;

/**
 *
 */
public class wpsStart {

    public static wpsConfig config;
    private static int PLAN_ID = 0;
    public static int peasantFamiliesAgents;
    public static boolean started = false;
    public static int CREATED_AGENTS = 0;
    public static final long startTime = System.currentTimeMillis();
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

    private static void setArgumentsConfig(String[] args) {

        // Definir los parámetros esperados
        Options options = new Options();
        options.addOption(new Option("env", true, "Environment"));
        options.addOption(new Option("mode", true, "Mode of operation"));
        options.addOption(new Option("nodes", true, "Nodes"));
        options.addOption(new Option("agents", true, "Number of agents"));
        options.addOption(new Option("money", true, "Amount of money"));
        options.addOption(new Option("land", true, "Land area"));
        options.addOption(new Option("personality", true, "Type of personality"));
        options.addOption(new Option("tools", true, "Type of tools"));
        options.addOption(new Option("seeds", true, "Type of seeds"));
        options.addOption(new Option("water", true, "Amount of water"));
        options.addOption(new Option("irrigation", true, "Irrigation enabled"));
        options.addOption(new Option("emotions", true, "Enable Emotions"));
        options.addOption(new Option("training", true, "Enable Training"));
        options.addOption(new Option("world", true, "World Size"));
        //options.addOption(new Option("step", false, "Step Time"));

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
            if (cmd.hasOption("env")) {
                params.env = cmd.getOptionValue("env");
            }
            if (cmd.hasOption("mode")) {
                params.mode = cmd.getOptionValue("mode");
            }
            if (cmd.hasOption("nodes")) {
                params.nodes = Integer.parseInt(cmd.getOptionValue("nodes"));
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
                params.training = Integer.parseInt(cmd.getOptionValue("training"));
            }
            if (cmd.hasOption("world")) {
                params.world = cmd.getOptionValue("world");
            }

            /*if (cmd.hasOption("step")) {
                params.steptime = Integer.parseInt(cmd.getOptionValue("step"));
            }else{
                params.steptime = Integer.parseInt(wpsStart.config.getStringProperty("control.steptime"));
            }*/


        } catch (Exception e) {
            // Mostrar ayuda si hay un error en el parseo
            System.err.println(e.getMessage());
            formatter.printHelp("wpsim", options);
            System.exit(1);
        }
    }

    private static void createContainer() {
        if (!params.mode.equals("wps01")) {
            // update ControlAgent Name
            config.setControlAgentName(params.mode + "_" + config.getControlAgentName());
            // update ViewerAgent Name
            config.setViewerAgentName(params.mode + "_" + config.getViewerAgentName());
        }
        // container creation
        String path = "server_" + params.env + "_" + params.mode + ".xml";
        System.out.println("Starting in " + path + " mode");
        AdmBESA adm = AdmBESA.getInstance(path);
        System.out.println(adm.getConfigBESA());
    }

    private static void startSimulation() {

        System.out.println("Es centralizado: " + AdmBESA.getInstance().isCentralized());

        switch (params.mode) {
            case "wps01" -> {
                createServices();
                createPeasants(config.peasantSerialID, peasantFamiliesAgents);
                showRunningAgents();
            }
            case "wps02", "wps03", "wps04", "wps05" -> {
                createPeasants(config.peasantSerialID, peasantFamiliesAgents);
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                showRunningAgents();
            }
            case "web" -> {
                // Single mode
                createServices();
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                createPeasants(config.peasantSerialID, peasantFamiliesAgents);
            }
            case "single" -> {
                // Single benchmark mode
                wpsStart.CREATED_AGENTS = 0;
                createServices();
                System.out.println("Simulating " + peasantFamiliesAgents + " agents");
                createPeasants(1, peasantFamiliesAgents);
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
        System.out.println("UPDATE: Contenedores activos");
        Enumeration<String> containers = AdmBESA.getInstance().getAdmAliasList();
        while (containers.hasMoreElements()) {
            System.out.println("UPDATE:" + containers.nextElement());
        }

    }

    /**
     * Creates the peasant family agents.
     */
    private static void createPeasants(int min, int max) {

        try {
            SimulationControl simulationControl = SimulationControl.createAgent(config.getControlAgentName(), config.getDoubleProperty("control.passwd"));
            simulationControl.start();
            ViewerLens viewerAgent = ViewerLens.createAgent(config.getViewerAgentName(), config.getDoubleProperty("control.passwd"));
            viewerAgent.start();
        } catch (ExceptionBESA e) {
            System.err.println("Problemas al crear el control o Viewer decentralizados");
        }

        //wpsReport.info("Creando agentes, desde " + min + ", hasta " + max, AdmBESA.getInstance().getConfigBESA().getAliasContainer());
        try {
            for (int i = min; i <= max; i++) {
                PeasantFamily peasantFamily = new PeasantFamily(
                        config.getUniqueFarmerName(),
                        config.getFarmerProfile()
                );
                CREATED_AGENTS++;
                peasantFamily.start();
            }
        } catch (Exception ex) {
            System.err.println("error creando peasants" + ex.getMessage());
        }

    }

    /**
     * Creates the services for peasant family agents.
     */
    private static void createServices() {
        try {
            CommunityDynamics communityDynamics = CommunityDynamics.createAgent(config.getSocietyAgentName(), config.getDoubleProperty("control.passwd"));
            communityDynamics.start();
            MarketPlace marketPlace = MarketPlace.createAgent(config.getMarketAgentName(), config.getDoubleProperty("control.passwd"));
            marketPlace.start();
            CivicAuthority civicAuthority = CivicAuthority.createAgent(config.getGovernmentAgentName(), config.getDoubleProperty("control.passwd"));
            civicAuthority.start();
            BankOffice bankOffice = BankOffice.createBankAgent(config.getBankAgentName(), config.getDoubleProperty("control.passwd"));
            bankOffice.start();
            PerturbationGenerator perturbationGenerator = PerturbationGenerator.createAgent(config.getPerturbationAgentName(), config.getDoubleProperty("control.passwd"));
            perturbationGenerator.start();
        } catch (Exception ex) {
            System.err.println(ex.getMessage() + " wpsStart_noOK");
        }
        pauseThread(1000);
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
     * Stops the simulation after a specified time.
     */
    public static void stopSimulation() {
        System.out.println("All agents stopped");
        System.out.println("UPDATE: Simulation finished in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds.");

        System.exit(0);
    }

    /**
     * Print header at Simulation begin
     */
    public static void printHeader() {

        wpsReport.info("""
                                       
                                    
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
                 
                """, "wpsStart");
    }

    public static long getTime() {
        return System.currentTimeMillis() - startTime;
    }

    public static void pauseThread(int milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}


