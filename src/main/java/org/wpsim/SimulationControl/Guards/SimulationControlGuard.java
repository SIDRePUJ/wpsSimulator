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
package org.wpsim.SimulationControl.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.SimulationControl.Data.SimulationControlState;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.PeasantFamily.Guards.FromSimulationControl.ToControlMessage;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Server.WebsocketServer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * @author jairo
 */
public class SimulationControlGuard extends GuardBESA {

    private static final LocalDate MIN_DATE = LocalDate.parse(
            wpsConfig.getInstance().getStartSimulationDate(),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );
    private static final LocalDate MAX_DATE = LocalDate.parse(
            wpsStart.config.getStringProperty("control.enddate"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    );

    /**
     * @param event Event rising the Guard
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        ToControlMessage toControlMessage = (ToControlMessage) event.getData();
        String agentCurrentDate = toControlMessage.getCurrentDate();
        int currentDay = toControlMessage.getCurrentDay();
        SimulationControlState state = (SimulationControlState) this.getAgent().getState();

        //wpsReport.info("Llegó a control el agente " + toControlMessage.getPeasantFamilyAlias() + ", en el día " + toControlMessage.getCurrentDay(), this.getAgent().getAlias());

        //wpsReport.debug("ControlAgentGuard: " + agentAlias + " acd " + agentCurrentDate + " gcd " + ControlCurrentDate.getInstance().getCurrentDate(), "ControlAgentGuard");
        state.modifyAgentMap(toControlMessage.getPeasantFamilyAlias(), currentDay);

        // Update ControlCurrentDate to last date from Agents
        if (ControlCurrentDate.getInstance().isAfterDate(agentCurrentDate)) {
            // Update ControlCurrentDate
            ControlCurrentDate.getInstance().setCurrentDate(agentCurrentDate);
            // Send data to WebUI
            if (wpsStart.config.getBooleanProperty("viewer.webui")) {
                WebsocketServer.getInstance().broadcastMessage("d=" + ControlCurrentDate.getInstance().getCurrentDate());
            }
            // Check if some agent is ahead of the global advance date
            if (ControlCurrentDate.getInstance().isFirstDayOfWeek(agentCurrentDate)) {
                printProgress(agentCurrentDate);
            }
        }
        state.checkAgentsStatus(toControlMessage.getPeasantFamilyAlias(), currentDay);
        /*if (ControlCurrentDate.getInstance().isFirstDayOfWeek(agentCurrentDate)) {
            state.checkAgentsStatus();
        }*/
    }

    public static void printProgress(String currentDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.parse(currentDateStr, formatter);

        if (currentDate.isBefore(MIN_DATE) || currentDate.isAfter(MAX_DATE)) {
            System.out.println("La fecha actual está fuera del rango del período especificado.");
            return;
        }

        long totalDays = ChronoUnit.DAYS.between(MIN_DATE, MAX_DATE);
        long elapsedDays = ChronoUnit.DAYS.between(MIN_DATE, currentDate);

        double progressPercentage = (100.0 * elapsedDays) / totalDays;
        System.out.println("UPDATE: Progreso desde " +
                MIN_DATE + " hasta " + MAX_DATE +
                " - fecha actual " + currentDateStr + ": " +
                String.format("%.2f", progressPercentage) + "%"
        );
    }

}
