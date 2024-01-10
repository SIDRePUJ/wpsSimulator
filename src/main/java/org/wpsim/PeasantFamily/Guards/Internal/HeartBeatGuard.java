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
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
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

import java.util.Timer;
import java.util.TimerTask;

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
    public synchronized void funcPeriodicExecGuard(EventBESA event) {
        PeasantFamilyBDIAgent PeasantFamily = (PeasantFamilyBDIAgent) this.getAgent();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) ((StateBDI) PeasantFamily.getState()).getBelieves();

        if (believes.getPeasantProfile().getHealth() <= 0 || believes.getInternalCurrentDate().equals("01/01/2023")) {
            System.out.println("data: " + believes.getPeasantProfile().getPeasantFamilyAlias() + " " + believes.toJson());
            this.stopPeriodicCall();
            this.agent.shutdownAgent();

            Timer timer = new Timer();
            TimerTask tarea = new TimerTask() {
                public void run() {
                    System.out.println("Tiempo terminado. El programa se cerrará.");
                    System.exit(0); // Termina el programa
                }
            };

            // Programa la tarea para que se ejecute después de 2 minutos (120000 milisegundos)
            timer.schedule(tarea, 60000);

            return;
        }

        StateBDI state = (StateBDI) PeasantFamily.getState();
        String PeasantFamilyAlias = believes.getPeasantProfile().getPeasantFamilyAlias();

        if (ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()) < -(wpsStart.DAYS_TO_CHECK)) {
            System.out.println("Jump PeasantFamilyAlias: " + PeasantFamilyAlias
                    + " - getDaysBetweenDates " + ControlCurrentDate.getInstance().getDaysBetweenDates(
                    believes.getInternalCurrentDate()
            ));
            believes.setInternalCurrentDate(ControlCurrentDate.getInstance().getCurrentDate());
            believes.setCurrentActivity(PeasantActivityType.BLOCKED);
            believes.makeNewDayWOD();
        } else {
            believes.setCurrentActivity(PeasantActivityType.NONE);
        }

        /*try {
            wpsReport.debug(PeasantFamilyAlias + " getEmotionsListCopy " + believes.getEmotionsListCopy().toString(), PeasantFamilyAlias);
            wpsReport.debug(PeasantFamilyAlias + " getMostActivatedEmotion " + believes.getMostActivatedEmotion().toString(),PeasantFamilyAlias);
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }*/

        try {
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
        }

        /*
          BDI Information Flow Guard - sends an event to continue the BDI flow
         */
        try {
            AdmBESA.getInstance().getHandlerByAlias(PeasantFamilyAlias).sendEvent(
                    new EventBESA(
                            InformationFlowGuard.class.getName(),
                            null
                    )
            );
        } catch (ExceptionBESA ex) {
            ReportBESA.error(ex);
        }

        wpsReport.ws(believes.toJson(), believes.getPeasantProfile().getPeasantFamilyAlias());

        int waitTime = wpsStart.stepTime;
        if (state.getMainRole() != null) {
            waitTime = TimeConsumedBy.valueOf(state.getMainRole().getRoleName()).getTime() * wpsStart.stepTime;
            //System.out.println("PeasantFamilyAlias: " + PeasantFamilyAlias + " - waitTime: " + waitTime + " rol " + state.getMainRole().getRoleName());
        }
        this.setDelayTime(waitTime);

    }

}
