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
package org.wpsim.PeasantFamily.PeriodicGuards;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Log.ReportBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.SimulationControl.Guards.SimulationControlGuard;
import org.wpsim.PeasantFamily.Agent.PeasantFamily;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.ToControlMessage;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Util.wpsReport;
import rational.guards.InformationFlowGuard;

/**
 * @author jairo
 */
public class HeartBeatGuard extends PeriodicGuardBESA {

    int waitTime = Integer.parseInt(wpsStart.config.getStringProperty("control.steptime"));
    String currentRole = "";

    /**
     * The method that will be executed when the guard is triggered.
     *
     * @param event The BESA event triggering the execution of the method.
     */
    @Override
    public synchronized void funcPeriodicExecGuard(EventBESA event) {
        PeasantFamily PeasantFamily = (org.wpsim.PeasantFamily.Agent.PeasantFamily) this.getAgent();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) ((StateBDI) PeasantFamily.getState()).getBelieves();
        StateBDI state = (StateBDI) PeasantFamily.getState();
        // Check if the agent has finished
        if (checkDead(believes)) return;
        // Check if the simulation has finished
        if (checkFinish(believes)) return;
        //System.out.println("HeartBeatGuard: " + this.getAgent().getAlias());
        if (!believes.isWaiting() || wpsStart.config.getBooleanProperty("control.freerun")) {
            // Update the internal current date
            UpdateControlDate(believes);
            // Report the agent's beliefs to the wpsViewer
            if (believes.isNewDay()) {
                wpsReport.ws(believes.toJson(), believes.getAlias());
            }
            // Wait time for the next execution
            sleepWave(state, believes);
            // Send BDI Pulse to BDI Information Flow
            sendBDIPulse(this.agent.getAlias());
            System.out.println(this.agent.getAlias() + " CON pulso "+ believes.isWaiting());
        }else{
            // Check if the current date is more than 8 days before the internal current date
            //checkTimeJump(believes);
            System.out.println(this.agent.getAlias() + " SIN pulso "+ believes.isWaiting());
            //System.out.println(this.agent.getAlias() + " bloqueado status " + believes.isWaiting());
        }
    }

    private void sleepWave(StateBDI state, PeasantFamilyBelieves believes) {
        try {
            currentRole = state.getMainRole().getRoleName();
        } catch (Exception e) {
            currentRole = "Void";
        }
        waitTime = TimeConsumedBy.valueOf(currentRole).getTime() * Integer.parseInt(wpsStart.config.getStringProperty("control.steptime"));
        try {
            Thread.sleep(waitTime/4);
        } catch (InterruptedException e) {
            System.out.println("error sleepWave");
        }
    }

    private static void sendBDIPulse(String alias) {
        //System.out.println("Send BDI Pulse: " + this.agent.getAlias());
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    alias
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

    private static void UpdateControlDate(PeasantFamilyBelieves believes) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getControlAgentName()
            ).sendEvent(new EventBESA(
                    SimulationControlGuard.class.getName(),
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

    private static void checkTimeJump(PeasantFamilyBelieves believes) {
        System.out.println("Micro Jump " + believes.getAlias()
                + " - " + ControlCurrentDate.getInstance().getDaysBetweenDates(
                believes.getInternalCurrentDate()
        ));

        believes.makeNewDay();
        //believes.setInternalCurrentDate(ControlCurrentDate.getInstance().getCurrentDate());
        //believes.setWait(false);

        UpdateControlDate(believes);
        sendBDIPulse(believes.getAlias());
    }

    private boolean checkFinish(PeasantFamilyBelieves believes) {
        if (believes.getInternalCurrentDate().equals(wpsStart.config.getStringProperty("control.enddate"))) {
            System.out.println("Cerrando Agente " + this.agent.getAlias());
            this.stopPeriodicCall();
            try {
                Thread.sleep(1000);
                this.agent.shutdownAgent();
            } catch (Exception e) {
                System.out.println("Error Cerrendo Agente");
            }
            //wpsStart.stopSimulation();
            return true;
        }
        return false;
    }

    private boolean checkDead(PeasantFamilyBelieves believes) {
        if (believes.getPeasantProfile().getHealth() <= 0) {
            //writeBenchmarkLog(believes.toCSV());
            this.stopPeriodicCall();
            try {
                Thread.sleep(1000);
                this.agent.shutdownAgent();
            } catch (Exception e) {
                System.out.println("Error Cerrendo Agente");
            }
            return true;
        }
        return false;
    }

}
