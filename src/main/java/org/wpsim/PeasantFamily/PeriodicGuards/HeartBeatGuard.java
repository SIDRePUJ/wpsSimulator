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
import org.wpsim.PeasantFamily.Data.Utils.PeasantActivityType;
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

        if (ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()) < -60){
            ReportBESA.info(
                    "UPDATE: \n=========" + believes.getAlias() + "========= " + believes.getTimeLeftOnDay() +
                    "Intention " + state.getMachineBDIParams().getIntention() +
                    "getTasks " + state.getMachineBDIParams().getIntention().getRole().getRolePlan().getTasks().toString() +
                    "======================================================"
            );
            believes.setCurrentActivity(PeasantActivityType.BLOCKED);
            believes.getPeasantProfile().setHealth(-100);
        }
        // Check if the agent has finished
        if (checkDead(believes)) return;
        // Check if the simulation has finished
        if (checkFinish(believes)) return;
        // Send BDI Pulse to BDI Information Flow
        sendBDIPulse(this.agent.getAlias());
        wpsReport.info("Tiempo restante " + believes.getTimeLeftOnDay() + " Ya ejecutadas: " + believes.getTasksBySpecificDate(believes.getInternalCurrentDate()), believes.getAlias());
    }

    private void sleepWave(StateBDI state, PeasantFamilyBelieves believes) {
        try {
            currentRole = state.getMainRole().getRoleName();
        } catch (Exception e) {
            currentRole = "Void";
        }
        waitTime = TimeConsumedBy.valueOf(currentRole).getTime() * Integer.parseInt(wpsStart.config.getStringProperty("control.steptime"));
        if (waitTime == 0) {
            waitTime = Integer.parseInt(wpsStart.config.getStringProperty("control.steptime"));
        }
        try {
            Thread.sleep(waitTime / 4);
        } catch (InterruptedException e) {
            ReportBESA.trace("error sleepWave");
        }
    }

    private static void sendBDIPulse(String alias) {
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

    private boolean checkFinish(PeasantFamilyBelieves believes) {
        if (believes.getInternalCurrentDate().equals(wpsStart.config.getStringProperty("control.enddate"))) {
            wpsReport.info("Cerrando Agente ", this.agent.getAlias());
            this.stopPeriodicCall();
            this.agent.shutdownAgent();
            return true;
        }
        return false;
    }

    private boolean checkDead(PeasantFamilyBelieves believes) {
        if (believes.getPeasantProfile().getHealth() <= 0) {
            this.stopPeriodicCall();
            try {
                //Thread.sleep(1000);
                this.agent.shutdownAgent();
            } catch (Exception e) {
                System.out.println("Error Cerrendo Agente");
            }
            return true;
        }
        return false;
    }

}
