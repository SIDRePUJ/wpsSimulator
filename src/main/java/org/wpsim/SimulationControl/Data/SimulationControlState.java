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
package org.wpsim.SimulationControl.Data;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.FromSimulationControlGuard;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Server.WebsocketServer;
import org.wpsim.ViewerLens.Util.wpsReport;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationControlState extends StateBESA implements Serializable {
    //private AtomicInteger activeAgentsCount = new AtomicInteger(0);
    //private AtomicBoolean unblocking = new AtomicBoolean(false);
    private ConcurrentMap<String, AgentInfo> agentMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> deadAgentMap = new ConcurrentHashMap<>();
    //private Timer timer = new Timer();
    //private ConcurrentMap<String, Timer> agentTimers = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    public SimulationControlState() {
        super();
        //this.scheduler = Executors.newScheduledThreadPool(1);
        //scheduler.scheduleAtFixedRate(this::checkAgentsStatus, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /*public ConcurrentMap<String, AgentInfo> getAliveAgentMap() {
        return agentMap;
    }*/

    public ConcurrentMap<String, Boolean> getDeadAgentMap() {
        return deadAgentMap;
    }

    public synchronized void checkAgentsStatus(String peasantFamily, int currentDay) {

        //printAgentsStatusTable();
        // Antes de comenzar, verificar si todos los agentes están muertos y detener la simulación si es necesario.
        /*if (deadAgentMap.size() == wpsStart.peasantFamiliesAgents){
            wpsStart.stopSimulation();
            return;
        }*/

        // Determinar el día mínimo y máximo entre todos los agentes vivos para entender el rango de tiempo actual.
        int minDay = agentMap.values().stream().mapToInt(AgentInfo::getCurrentDay).min().orElse(Integer.MAX_VALUE);
        int maxDay = agentMap.values().stream().mapToInt(AgentInfo::getCurrentDay).max().orElse(Integer.MIN_VALUE);

        try {
            //System.out.println("Min " + minDay + " Max " + maxDay + " " + agentName + " Days " + needsPause + " " + (currentDay - minDay) + " = " + currentDay + " - " + minDay + " >= " + wpsStart.config.getIntProperty("control.daystocheck"));
            if (currentDay - minDay >= wpsStart.config.getIntProperty("control.daystocheck")) {
                AdmBESA.getInstance().getHandlerByAlias(
                        peasantFamily
                ).sendEvent(
                        new EventBESA(
                                FromSimulationControlGuard.class.getName(),
                                new ControlMessage(
                                        peasantFamily,
                                        true
                                )
                        )
                );
            }
        } catch (ExceptionBESA ex) {
            wpsReport.debug(ex, wpsStart.config.getControlAgentName());
        }
    }

    public synchronized void printAgentsStatusTable() {
        System.out.println("+--------------------------------------+");
        System.out.println("| Agent Name         | Status | Day    |");
        System.out.println("+--------------------------------------+");

        agentMap.forEach((agentName, agentInfo) -> {
            String status = agentInfo.getState() ? "Active" : "Paused";
            String day = String.valueOf(agentInfo.getCurrentDay());
            System.out.printf("| %-18s | %-6s | %-6s |\n", agentName, status, day);
        });

        System.out.println("+--------------------------------------+");
    }

    public void addAgentToMap(String agentName, int currentDay) {
        wpsReport.debug("Agent " + agentName + " is new and alive", "ControlAgentState");
        this.agentMap.put(agentName, new AgentInfo(false, currentDay));
        //Comienza a revisar el desbloqueo de agentes por tiempo
        if (agentMap.size() == wpsStart.peasantFamiliesAgents) {
            wpsStart.started = true;
        }
    }

    public void removeAgentFromMap(String agentName) {
        wpsReport.debug("Agent " + agentName + " is dead", "ControlAgentState");
        this.agentMap.remove(agentName);
        this.deadAgentMap.put(agentName, true);

    }

    public void modifyAgentMap(String agentName, int currentDay) {
        AgentInfo info = agentMap.getOrDefault(agentName, new AgentInfo(false, currentDay));
        info.setState(true);
        info.setCurrentDay(currentDay);
        agentMap.put(agentName, info);
        if (wpsConfig.getInstance().getBooleanProperty("viewer.webui")) {
            WebsocketServer.getInstance().broadcastMessage("m=" + agentMap.toString());
        }
    }
    /*public void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }*/

}


