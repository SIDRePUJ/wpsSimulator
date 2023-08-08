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
package org.wpsim.Control;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Log.ReportBESA;
import org.wpsim.PeasantFamily.Guards.FromControlGuard;
import org.wpsim.PeasantFamily.Guards.KillZombieGuard;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.WebsocketServer;
import org.wpsim.Viewer.wpsReport;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ControlAgentState extends StateBESA implements Serializable {

    private AtomicInteger activeAgentsCount = new AtomicInteger(0);
    private AtomicBoolean unblocking = new AtomicBoolean(false);
    private ConcurrentMap<String, Boolean> agentMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> deadAgentMap = new ConcurrentHashMap<>();
    private Timer timer = new Timer();
    private ScheduledExecutorService scheduler;

    public ControlAgentState() {
        super();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                synchronized (unblocking) {
                    if (unblocking.get()) {
                        try {
                            AdmBESA adm = AdmBESA.getInstance();
                            int count = wpsStart.peasantFamiliesAgents;
                            for (String agentName : ControlAgentState.this.getAliveAgentMap().keySet()) {
                                EventBESA eventBesa = new EventBESA(FromControlGuard.class.getName(), count--);
                                AgHandlerBESA agHandler = adm.getHandlerByAlias(agentName);
                                agHandler.sendEvent(eventBesa);
                                ReportBESA.debug("Unblocking event to " + agentName + " sent " + count);
                            }
                        } catch (ExceptionBESA ex) {
                            ReportBESA.debug(ex);
                        }
                        resetActiveAgents();
                        WebsocketServer.getInstance().broadcastMessage("d=" + ControlCurrentDate.getInstance().getCurrentDate());
                    }
                }
            }
        }, 5, 10, TimeUnit.SECONDS);
    }

    public ConcurrentMap<String, Boolean> getAliveAgentMap() {
        return agentMap;
    }

    public ConcurrentMap<String, Boolean> getDeadAgentMap() {
        return deadAgentMap;
    }

    private ConcurrentMap<String, Timer> agentTimers = new ConcurrentHashMap<>();

    public synchronized boolean allAgentsAlive() {

        if (!wpsStart.started) {
            return false;
        }
        int trueCount = 0;
        int falseCount = 0;

        for (Boolean value : agentMap.values()) {
            if (value) {
                trueCount++;
            } else {
                falseCount++;
            }
        }

        wpsReport.debug("Number of true values: " + trueCount, "ControlAgentState");
        wpsReport.debug("Number of false values: " + falseCount, "ControlAgentState");
        WebsocketServer.getInstance().broadcastMessage("s={\"alive\":" + trueCount + ",\"dead\":" + falseCount + "}");

        if (wpsStart.peasantFamiliesAgents == (falseCount + trueCount)) {
            return !agentMap.containsValue(false);
        }
        return false;

    }

    public int getActiveAgentsCount() {
        return this.activeAgentsCount.get();
    }

    public void resetActiveAgents() {
        this.agentMap.replaceAll((k, v) -> false);
        unblocking.set(false);
    }

    public void increaseActiveAgents() {
        this.activeAgentsCount.incrementAndGet();
    }

    public String printAgentMap() {
        List<String> keys = new ArrayList<>(agentMap.keySet());
        Collections.sort(keys);
        String agentMapString = "Blocked Agents:\n";
        for (String key : keys) {
            if (!agentMap.get(key)) {
                agentMapString = agentMapString.concat(key + ": " + agentMap.get(key) + "\n");
            }
        }
        return agentMapString;
    }

    public String printDeadAgentMap() {
        List<String> keys = new ArrayList<>(deadAgentMap.keySet());
        Collections.sort(keys);
        String agentMapString = "Dead Agents:\n";
        for (String key : keys) {
            agentMapString = agentMapString.concat(key + ": " + agentMap.get(key) + "\n");
        }
        return agentMapString;
    }

    public void addAgentToMap(String agentName) {
        wpsReport.debug("Agent " + agentName + " is new and alive", "ControlAgentState");
        this.agentMap.put(agentName, false);
    }

    public void removeAgentFromMap(String agentName) {
        wpsReport.debug("Agent " + agentName + " is dead", "ControlAgentState");
        this.agentMap.remove(agentName);
        this.deadAgentMap.put(agentName, true);
    }

    public void modifyAgentMap(String agentName) {
        // Cancela cualquier temporizador existente para este agente
        Timer existingTimer = agentTimers.get(agentName);
        if (existingTimer != null) {
            existingTimer.cancel();
        }

        // Marca el agente como "vivo"
        this.agentMap.put(agentName, true);

        WebsocketServer.getInstance().broadcastMessage("m=" + agentMap.toString());

        if (allAgentsAlive()){
            unblocking.set(true);
        }

        // Inicia un nuevo temporizador para este agente
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                wpsReport.debug("Agent " + agentName + " is dead by ControlAgentState", "ControlAgentState");
                try {
                    AdmBESA adm = AdmBESA.getInstance();
                    EventBESA eventBesa = new EventBESA(KillZombieGuard.class.getName(), null);
                    AgHandlerBESA agHandler = adm.getHandlerByAlias(agentName);
                    agHandler.sendEvent(eventBesa);
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, "ControlAgentState");
                }
            }
        }, 2 * 60 * 1000); // 1 minuto
        agentTimers.put(agentName, timer);


    }

}

