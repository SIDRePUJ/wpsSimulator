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
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.Viewer.Data.wpsViewerMessage;
import org.wpsim.Viewer.Server.WebsocketServer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author jairo
 */
public class wpsViewerAgentGuard extends GuardBESA {
    private static final Logger logger = LoggerFactory.getLogger(wpsReport.class);
    private ConcurrentLinkedQueue<String> dataQueue = new ConcurrentLinkedQueue<>();
    private String FILENAME;

    public wpsViewerAgentGuard() {
        super();
        this.FILENAME = "logs/" + getFileName();
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
                case "MENTAL" -> logData(viewerMessage.getPeasantMessage());
                case "MENTAL_CLOSE" -> writeDataToFile();
                case "TRACE" -> logger.trace(viewerMessage.getPeasantMessage());
                case "DEBUG" -> logger.debug(viewerMessage.getPeasantMessage());
                case "WARN" -> logger.warn(viewerMessage.getPeasantMessage());
                case "ERROR" -> logger.error(viewerMessage.getPeasantMessage());
                case "WS" -> {
                    //logger.debug(viewerMessage.getPeasantMessage());
                    WebsocketServer.getInstance().broadcastMessage("j=" + viewerMessage.getPeasantMessage());
                }
                default -> logger.info(viewerMessage.getPeasantMessage());
            }
        } finally {
            MDC.clear();
        }
    }

    public void logData(String data) {
        dataQueue.add(data);
    }

    public void logHeaderData() {
        String csvData = "money,health,timeLeftOnDay,newDay,currentSeason,robberyAccount,purpose," +
                "peasantFamilyAffinity,peasantLeisureAffinity,peasantFriendsAffinity,currentPeasantLeisureType," +
                "currentResourceNeededType,currentDay,internalCurrentDate,toPay,peasantKind,rainfallConditions," +
                "peasantFamilyMinimalVital,peasantFamilyLandAlias,currentActivity,farm,loanAmountToPay," +
                "tools,seeds,waterAvailable,pesticidesAvailable,totalHarvestedWeight,contractor,daysToWorkForOther," +
                "Agent,Emotion,peasantFamilyHelper,Happiness/Sadness,Hopeful/Uncertainty,Secure/Insecure";
        logData(csvData);
    }

    public void writeDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.FILENAME, true))) {
            while (!dataQueue.isEmpty()) {
                writer.write(dataQueue.poll());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public String getFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        return "mentalLog_" + wpsStart.DEFAULT_AGENTS_TO_TEST + "_" + dateFormat.format(new Date()) + getRandomString(6) + ".csv";
    }

    private String getRandomString(int size) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            sb.append(letter);
        }
        return sb.toString();
    }

}
