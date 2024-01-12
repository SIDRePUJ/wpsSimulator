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

import BESA.BDI.AgentStructuralModel.DesireHierarchyPyramid;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import BESA.Log.ReportBESA;
import org.wpsim.Bank.Data.BankMessage;
import org.wpsim.Bank.Guards.BankAgentGuard;
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

import static org.wpsim.Bank.Data.BankMessageType.ASK_CURRENT_TERM;

/**
 * @author jairo
 */
public class HeartBeatGuard extends PeriodicGuardBESA {

    /**
     * It retrieves the current agent and its beliefs.
     * It checks if the current date is more than 7 days before the internal current date to move forward.
     * If so, it updates the internal current date and creates a new day without data.
     * It logs the emotions list and the most activated emotion of the agent.
     * It checks if the agent's health is zero or below. If so, it shuts down the agent.
     * It creates a message to send to a control agent with the agent's alias, internal current date, and current day.
     * It sends the message to the control agent.
     * It sends an event to an agent with the alias PeasantFamilyAlias.
     * It logs the agent's beliefs as a JSON string along with the agent's alias.
     * It calculates the wait time based on the agent's main role and sets the delay time for the next execution of the method.
     *
     * @param event The BESA event triggering the execution of the method.
     */
    @Override
    //public synchronized void funcPeriodicExecGuard(EventBESA event) {
    public synchronized void funcPeriodicExecGuard(EventBESA event) {
        PeasantFamilyBDIAgent PeasantFamily = (PeasantFamilyBDIAgent) this.getAgent();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) ((StateBDI) PeasantFamily.getState()).getBelieves();

        // Check if the current date is more than 7 days before the internal current date
        checkTimeJump(believes);
        // Check if the agent has finished
        if (checkDead(believes)) return;
        // Check if the simulation has finished
        if (checkFinish(believes)) return;
        // Execute vital tasks every simulated day
        this.doVitals(believes);

        // Log the activities the agent
        wpsReport.ws(believes.toJson(), believes.getPeasantProfile().getPeasantFamilyAlias());

        // Check the next time to execute pulse
        StateBDI state = (StateBDI) PeasantFamily.getState();
        String role;
        try {
            role = state.getMainRole().getRoleName();
        }catch (Exception e){
            role = "none";
        }

        int waitTime = wpsStart.stepTime;
        if (state.getMainRole() != null) {
            if (role.equals("PlantCropTask")){
                waitTime = TimeConsumedBy.valueOf(state.getMainRole().getRoleName()).getTime() * wpsStart.stepTime * 10;
            }else {
                waitTime = TimeConsumedBy.valueOf(state.getMainRole().getRoleName()).getTime() * wpsStart.stepTime;
            }
        }

        System.out.println(
                "PeasantFamilyAlias: " + believes.getPeasantProfile().getPeasantFamilyAlias()
                + " - waitTime: " + waitTime
                        + " rol " + role);
        //this.setDelayTime(waitTime);
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void checkTimeJump(PeasantFamilyBDIAgentBelieves believes) {
        if (ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()) < -(wpsStart.DAYS_TO_CHECK)) {
            System.out.println("Jump PeasantFamilyAlias: " + believes.getPeasantProfile().getPeasantFamilyAlias()
                    + " - getDaysBetweenDates " + ControlCurrentDate.getInstance().getDaysBetweenDates(
                    believes.getInternalCurrentDate()
            ));
            believes.setInternalCurrentDate(ControlCurrentDate.getInstance().getCurrentDate());
            believes.setCurrentActivity(PeasantActivityType.BLOCKED);
            believes.makeNewDayWOD();
        } else {
            believes.setCurrentActivity(PeasantActivityType.NONE);
        }
    }

    private void doVitals(PeasantFamilyBDIAgentBelieves believes) {
        if (!believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(),"DoVitalsTask")){
            believes.addTaskToLog(believes.getInternalCurrentDate(), "DoVitalsTask");
            believes.setNewDay(false);
            believes.useTime(TimeConsumedBy.DoVitalsTask);
            believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "DOVITALS", "FOOD"));
            believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HOUSEHOLDING", "TIME"));
            if (believes.getPeasantProfile().getMoney()<=100000){
                believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "STARVING", "FOOD"));
            }
            // Check debts
            checkBankDebt(believes);
            believes.getPeasantProfile().discountDailyMoney();
        }
        // BDI Information Flow Guard - sends an event to continue the BDI flow
        try {
            AdmBESA.getInstance().getHandlerByAlias(believes.getPeasantProfile().getPeasantFamilyAlias()).sendEvent(
                    new EventBESA(
                            InformationFlowGuard.class.getName(),
                            null
                    )
            );
        } catch (ExceptionBESA ex) {
            ReportBESA.error(ex);
        }
    }

    private void checkBankDebt(PeasantFamilyBDIAgentBelieves believes) {
        if (ControlCurrentDate.getInstance().isFirstDayOfMonth(believes.getInternalCurrentDate())
                && believes.getCurrentDay() > 6) {
            try {
                AdmBESA.getInstance().getHandlerByAlias(
                        wpsStart.config.getBankAgentName()
                ).sendEvent(
                        new EventBESA(
                                BankAgentGuard.class.getName(),
                                new BankMessage(
                                        ASK_CURRENT_TERM,
                                        believes.getPeasantProfile().getPeasantFamilyAlias()
                                )
                        )
                );
            } catch (ExceptionBESA ex) {
                wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
            }
        }
    }

    private boolean checkFinish(PeasantFamilyBDIAgentBelieves believes) {
        if (believes.getInternalCurrentDate().equals(wpsStart.ENDDATE)) {
            writeBenchmarkLog(believes.toJsonSimple());
            this.stopPeriodicCall();
            this.agent.shutdownAgent();

            Timer timer = new Timer();
            TimerTask tarea = new TimerTask() {
                public void run() {
                    System.out.println("Tiempo terminado. El programa se cerrará.");
                    System.exit(0); // Termina el programa
                }
            };
            timer.schedule(tarea, 120000);
            return true;
        }
        return false;
    }
    private boolean checkDead(PeasantFamilyBDIAgentBelieves believes) {
        if (believes.getPeasantProfile().getHealth() <= 0) {
            writeBenchmarkLog(believes.toJsonSimple());
            this.stopPeriodicCall();
            this.agent.shutdownAgent();
            return true;
        }
        return false;
    }

    public synchronized void writeBenchmarkLog(String texto) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("benckmark.log", true))) {
            writer.write(texto);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}


/*try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getControlAgentName()
            ).sendEvent(new EventBESA(
                    ControlAgentGuard.class.getName(),
                    new ToControlMessage(
                            believes.getPeasantProfile().getPeasantFamilyAlias(),
                            believes.getInternalCurrentDate(),
                            believes.getCurrentDay()
                    )
            ));
        } catch (ExceptionBESA ex) {
            ReportBESA.error(ex);
        }*/