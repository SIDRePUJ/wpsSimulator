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
package org.wpsim.Control.Data;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.StateBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.PeasantFamily.Guards.FromControl.FromControlGuard;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Server.WebsocketServer;
import org.wpsim.Viewer.Data.wpsReport;

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
    //private Timer timer = new Timer();
    private ConcurrentMap<String, Timer> agentTimers = new ConcurrentHashMap<>();

    public ControlAgentState() {
        super();
    }

    public ConcurrentMap<String, Boolean> getAliveAgentMap() {
        return agentMap;
    }

    public ConcurrentMap<String, Boolean> getDeadAgentMap() {
        return deadAgentMap;
    }

    public synchronized boolean allAgentsAlive() {

        if (!wpsStart.started) {
            return false;
        }

        int trueCount = (int) agentMap.values().stream().filter(Boolean::booleanValue).count();
        int falseCount = agentMap.size() - trueCount;

        WebsocketServer.getInstance().broadcastMessage(
                "s={" +
                        "\"alive\":" + trueCount
                        + ",\"away\":" + falseCount
                        + ",\"dead\":" + deadAgentMap.size()
                        + "}"
        );
        WebsocketServer.getInstance().broadcastMessage("d=" + ControlCurrentDate.getInstance().getCurrentDate());

        if (wpsStart.peasantFamiliesAgents == (agentMap.size() + deadAgentMap.size())) {
            return !agentMap.containsValue(false);
        }
        return false;

    }

    public boolean checkUnblocking(int days) {
        //System.out.println("checkUnblocking " + days);
        if (unblocking.get() && (days % wpsStart.DAYS_TO_CHECK == 0)) {
            //wpsReport.debug("Unblocking event", "ControlAgentState");
            try {
                int count = wpsStart.peasantFamiliesAgents;
                for (String agentName : getAliveAgentMap().keySet()) {
                    AgHandlerBESA agHandler = AdmBESA.getInstance().getHandlerByAlias(agentName);
                    EventBESA eventBesa = new EventBESA(
                            FromControlGuard.class.getName(),
                            new ControlMessage(agentName, count--, days + wpsStart.DAYS_TO_CHECK)
                    );
                    agHandler.sendEvent(eventBesa);
                    //wpsReport.debug("Unblock " + agentName + " sent " + count, "ControlAgentState");
                }
                resetActiveAgents();
                return true;
            } catch (ExceptionBESA ex) {
                wpsReport.debug(ex, "ControlAgentState");
            }
        }
        return false;
    }

    public void resetActiveAgents() {
        this.agentMap.replaceAll((k, v) -> false);
        unblocking.set(false);
    }

    public void addAgentToMap(String agentName) {
        wpsReport.debug("Agent " + agentName + " is new and alive", "ControlAgentState");
        this.agentMap.put(agentName, false);
        //Comienza a revisar el desbloqueo de agentes por tiempo
        if (agentMap.size() == wpsStart.peasantFamiliesAgents) {
            wpsStart.started = true;
        }
    }

    public void removeAgentFromMap(String agentName) {
        wpsReport.debug("Agent " + agentName + " is dead", "ControlAgentState");
        this.agentMap.remove(agentName);
        this.deadAgentMap.put(agentName, true);
        /*Timer existingTimer = agentTimers.get(agentName);
        if (existingTimer != null) {
            existingTimer.cancel();
        }*/
    }

    public void modifyAgentMap(String agentName) {
        // Cancela cualquier temporizador existente para este agente
        /*Timer existingTimer = agentTimers.get(agentName);
        if (existingTimer != null) {
            existingTimer.cancel();
        }*/
        // Marca el agente como "vivo"
        this.agentMap.put(agentName, true);
        WebsocketServer.getInstance().broadcastMessage("m=" + agentMap.toString());
        //unblocking.set(allAgentsAlive());
        // Inicia un nuevo temporizador para este agente
        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //wpsReport.debug("Agent " + agentName + " is dead by ControlAgentState", "ControlAgentState");
                // Marca el agente como "muerto"
                try {
                    AdmBESA adm = AdmBESA.getInstance();
                    ToControlMessage toControlMessage = new ToControlMessage(agentName);
                    EventBESA eventBesa = new EventBESA(DeadAgentGuard.class.getName(), toControlMessage);
                    AgHandlerBESA agHandler = adm.getHandlerByAlias(wpsStart.config.getControlAgentName());
                    agHandler.sendEvent(eventBesa);
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, "controlAgentState");
                }
                // Enviar señal de matar al agente
                try {
                    AdmBESA adm = AdmBESA.getInstance();
                    EventBESA eventBesa = new EventBESA(KillZombieGuard.class.getName(), null);
                    AgHandlerBESA agHandler = adm.getHandlerByAlias(agentName);
                    agHandler.sendEvent(eventBesa);
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, "ControlAgentState");
                }
                removeAgentFromMap(agentName);
            }
        }, 2 * 60 * 1000); // 2 minutos
        agentTimers.put(agentName, timer);
        */
    }

}

