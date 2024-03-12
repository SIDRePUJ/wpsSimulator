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
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.FromControlGuard;
import org.wpsim.Simulator.Config.wpsConfig;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Server.WebsocketServer;
import org.wpsim.Viewer.Data.wpsReport;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlAgentState extends StateBESA implements Serializable {
    private AtomicInteger activeAgentsCount = new AtomicInteger(0);
    private AtomicBoolean unblocking = new AtomicBoolean(false);
    private ConcurrentMap<String, AgentInfo> agentMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> deadAgentMap = new ConcurrentHashMap<>();
    //private Timer timer = new Timer();
    private ConcurrentMap<String, Timer> agentTimers = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;

    public ControlAgentState() {
        super();
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkAgentsStatus, 0, 2, TimeUnit.MILLISECONDS);
    }

    public ConcurrentMap<String, AgentInfo> getAliveAgentMap() {
        return agentMap;
    }

    public ConcurrentMap<String, Boolean> getDeadAgentMap() {
        return deadAgentMap;
    }

    public synchronized void checkAgentsStatus() {
        /*System.out.println("\n------------------------------\nRevisando Lista de agentes, " + deadAgentMap.size() + " de " + wpsStart.peasantFamiliesAgents);
        for (String agentName : getAliveAgentMap().keySet()) {
            AgentInfo info = agentMap.get(agentName);
            System.out.println(agentName + " " + info.toString());
        }*/

        if (deadAgentMap.size() == wpsStart.peasantFamiliesAgents){
            wpsStart.stopSimulation();
        }

        // Revisar si se cumple la condición para el bloqueo
        int minDay = agentMap.values()
                .stream()
                .mapToInt(AgentInfo::getCurrentDay)
                .min()
                .orElse(Integer.MAX_VALUE);
        int maxDay = agentMap.values()
                .stream()
                .mapToInt(AgentInfo::getCurrentDay)
                .max()
                .orElse(Integer.MIN_VALUE);

        for (String agentName : getAliveAgentMap().keySet()) {
            AgentInfo info = agentMap.get(agentName);
            if (info != null) {
                int currentDay = info.getCurrentDay();
                try {
                    AgHandlerBESA agHandler = AdmBESA.getInstance().getHandlerByAlias(agentName);
                    EventBESA eventBesa = null;
                    if (currentDay - minDay > wpsStart.config.getIntProperty("control.daystocheck")) {
                        // Agente adelantado
                        eventBesa = new EventBESA(FromControlGuard.class.getName(), new ControlMessage(agentName, true));
                        //System.out.println("Agent " + agentName + " bloqueo");
                        // Acciones adicionales para agentes adelantados
                    } else { // if (currentDay >= maxDay) {
                        // Agente resagado que ha alcanzado el día máximo
                        eventBesa = new EventBESA(FromControlGuard.class.getName(), new ControlMessage(agentName, false));
                        //System.out.println("Agent " + agentName + " desbloqueo");
                        // Acciones adicionales para agentes que se han puesto al día
                    } //else {
                        // Agente en tiempo o atrasado pero aún no ha alcanzado el día máximo
                        // Posiblemente no se necesite enviar un evento en este caso
                        //eventBesa = new EventBESA(FromControlGuard.class.getName(), new ControlMessage(agentName, false));
                        //System.out.println("Agent " + agentName + " desbloqueo 2");
                    //}
                    agHandler.sendEvent(eventBesa);
                } catch (ExceptionBESA ex) {
                    wpsReport.debug(ex, "ControlAgentState");
                }
            }
        }
        //System.out.println("\n------------------------------\n");
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
    public void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

}


