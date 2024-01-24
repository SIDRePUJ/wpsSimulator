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
package org.wpsim.Viewer.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsViewerMessage;
import org.wpsim.Viewer.Server.WebsocketServer;


/**
 * @author jairo
 */
public class wpsViewerAgentGuard extends GuardBESA {
    private static final Logger logger = LoggerFactory.getLogger("wpsReport");
    private static final Logger loggerData = LoggerFactory.getLogger("wpsReportData");
    public wpsViewerAgentGuard() {
        super();
        logHeaderData();
    }

    /**
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        wpsViewerMessage viewerMessage = (wpsViewerMessage) event.getData();
        //WebsocketServer.getInstance().broadcastMessage(viewerMessage.getPeasantMessage());
        try {
            MDC.put("peasantAlias", viewerMessage.getPeasantAlias());
            switch (viewerMessage.getLevel()) {
                case "MENTAL" -> loggerData.trace(viewerMessage.getPeasantMessage());
                case "TRACE" -> logger.trace(viewerMessage.getPeasantMessage());
                case "DEBUG" -> logger.debug(viewerMessage.getPeasantMessage());
                case "WARN" -> logger.warn(viewerMessage.getPeasantMessage());
                case "ERROR" -> logger.error(viewerMessage.getPeasantMessage());
                case "WS" -> {
                    if (wpsStart.WEBUI) {
                        WebsocketServer.getInstance().broadcastMessage("j=" + viewerMessage.getPeasantMessage());
                    }
                }
                default -> logger.info(viewerMessage.getPeasantMessage());
            }
        } finally {
            MDC.clear();
        }
    }

    public void logHeaderData() {
        String csvData = "HappinessSadness,HopefulUncertainty,SecureInsecure,money,health,timeLeftOnDay," +
                "newDay,currentSeason,robberyAccount,purpose,peasantFamilyAffinity,peasantLeisureAffinity," +
                "peasantFriendsAffinity,currentPeasantLeisureType," +
                "currentResourceNeededType,currentDay,internalCurrentDate,toPay,peasantKind,rainfallConditions," +
                "peasantFamilyMinimalVital,peasantFamilyLandAlias,currentActivity,farm,loanAmountToPay," +
                "tools,seeds,waterAvailable,pesticidesAvailable,totalHarvestedWeight,contractor,daysToWorkForOther," +
                "Agent,Emotion,peasantFamilyHelper,haveEmotions";
        loggerData.trace(csvData);
    }

}
