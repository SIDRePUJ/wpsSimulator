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
package org.wpsim.PeasantFamily.Guards.Internal;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Log.ReportBESA;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.Control.Guards.ControlAgentGuard;
import org.wpsim.PeasantFamily.Agent.PeasantFamilyBDIAgent;
import org.wpsim.PeasantFamily.Data.Utils.PeasantActivityType;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Guards.FromControl.ToControlMessage;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsReport;
import rational.guards.InformationFlowGuard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jairo
 */
public class HeartBeatGuard extends PeriodicGuardBESA {

    int waitTime = wpsStart.stepTime;
    String currentRole = "";

    /**
     * The method that will be executed when the guard is triggered.
     *
     * @param event The BESA event triggering the execution of the method.
     */
    @Override
    public synchronized void funcPeriodicExecGuard(EventBESA event) {
        PeasantFamilyBDIAgent PeasantFamily = (PeasantFamilyBDIAgent) this.getAgent();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) ((StateBDI) PeasantFamily.getState()).getBelieves();
        StateBDI state = (StateBDI) PeasantFamily.getState();
        //System.out.println("HeartBeatGuard: " + this.getAgent().getAlias());
        if (!believes.isWaiting()) {
            // Update the internal current date
            UpdateControlDate(believes);
            // Check if the current date is more than 7 days before the internal current date
            //checkTimeJump(believes);
            // Check if the agent has finished
            if (checkDead(believes)) return;
            // Check if the simulation has finished
            if (checkFinish(believes)) return;
            // Report the agent's beliefs to the wpsViewer
            if (believes.isNewDay()) {
                wpsReport.ws(believes.toJson(), believes.getAlias());
            }
            // Wait time for the next execution
            sleepWave(state, believes);
            //System.out.println(" Agente funcionando, latiendo " + believes.getAlias());
            // Send BDI Pulse to BDI Information Flow
            sendBDIPulse();
        }
    }

    private void sleepWave(StateBDI state, PeasantFamilyBDIAgentBelieves believes) {
        try {
            currentRole = state.getMainRole().getRoleName();
        } catch (Exception e) {
            currentRole = "Void";
        }
        waitTime = TimeConsumedBy.valueOf(currentRole).getTime() * wpsStart.stepTime;
    }

    private void sendBDIPulse() {
        //System.out.println("Send BDI Pulse: " + this.agent.getAlias());
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    this.agent.getAlias()
            ).sendEvent(
                    new EventBESA(
                            InformationFlowGuard.class.getName(),
                            null
                    )
            );
        } catch (ExceptionBESA ex) {
            ReportBESA.error(ex);
        }
    }

    private static void UpdateControlDate(PeasantFamilyBDIAgentBelieves believes) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getControlAgentName()
            ).sendEvent(new EventBESA(
                    ControlAgentGuard.class.getName(),
                    new ToControlMessage(
                            believes.getAlias(),
                            believes.getInternalCurrentDate(),
                            believes.getCurrentDay()
                    )
            ));
        } catch (ExceptionBESA ex) {
            ReportBESA.error(ex);
        }
    }

    private static void checkTimeJump(PeasantFamilyBDIAgentBelieves believes) {
        if (ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()) <= -(wpsStart.DAYS_TO_CHECK)) {
            /*System.out.println("Jump " + believes.getAlias()
                    + " - " + ControlCurrentDate.getInstance().getDaysBetweenDates(
                    believes.getInternalCurrentDate()
            ));
            believes.setInternalCurrentDate(ControlCurrentDate.getInstance().getCurrentDate());
            believes.setCurrentActivity(PeasantActivityType.BLOCKED);
            believes.makeNewDayWOD();
             */
            /*System.out.println(believes.getAlias() + " en espera " + believes.getAlias()
                    + " - " + ControlCurrentDate.getInstance().getDaysBetweenDates(
                    believes.getInternalCurrentDate()
            ));*/
            //believes.setWait(true);
        } else {
            believes.setCurrentActivity(PeasantActivityType.NONE);
        }
    }

    private boolean checkFinish(PeasantFamilyBDIAgentBelieves believes) {
        if (believes.getInternalCurrentDate().equals(wpsStart.ENDDATE)) {
            System.out.println("Cerrando Agente " + this.agent.getAlias());
            this.agent.shutdownAgent();
            this.stopPeriodicCall();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Error Cerrendo Agente");
            }
            //wpsStart.stopSimulation();
            return true;
        }
        return false;
    }

    private boolean checkDead(PeasantFamilyBDIAgentBelieves believes) {
        if (believes.getPeasantProfile().getHealth() <= 0) {
            //writeBenchmarkLog(believes.toCSV());
            this.stopPeriodicCall();
            this.agent.shutdownAgent();
            return true;
        }
        return false;
    }

    public synchronized void writeBenchmarkLogNo(String texto) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("benckmark.csv", true))) {
            writer.write(texto);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

}
