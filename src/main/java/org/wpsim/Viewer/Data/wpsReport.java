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
package org.wpsim.Viewer.Data;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Guards.wpsViewerAgentGuard;

public class wpsReport {

    private wpsReport() {

    }

    public static void trace(Object message, String alias) {
        try {
            AdmBESA adm = AdmBESA.getInstance();
            wpsViewerMessage viewerMessage = new wpsViewerMessage(
                    formatMessage(message),
                    "TRACE",
                    alias
            );
            EventBESA eventBesa = new EventBESA(
                    wpsViewerAgentGuard.class.getName(),
                    viewerMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    public static void debug(Object message, String alias) {
        try {
            AdmBESA adm = AdmBESA.getInstance();
            wpsViewerMessage viewerMessage = new wpsViewerMessage(
                    formatMessage(message),
                    "DEBUG",
                    alias
            );
            EventBESA eventBesa = new EventBESA(
                    wpsViewerAgentGuard.class.getName(),
                    viewerMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    public static void info(Object message, String alias) {
        try {
            AdmBESA adm = AdmBESA.getInstance();
            wpsViewerMessage viewerMessage = new wpsViewerMessage(
                    formatMessage(message),
                    "INFO",
                    alias
            );
            EventBESA eventBesa = new EventBESA(
                    wpsViewerAgentGuard.class.getName(),
                    viewerMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    public static void warn(Object message, String alias) {
        try {
            AdmBESA adm = AdmBESA.getInstance();
            wpsViewerMessage viewerMessage = new wpsViewerMessage(
                    formatMessage(message),
                    "WARN",
                    alias
            );
            EventBESA eventBesa = new EventBESA(
                    wpsViewerAgentGuard.class.getName(),
                    viewerMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    public static void error(Object message, String alias) {
        try {
            AdmBESA adm = AdmBESA.getInstance();
            wpsViewerMessage viewerMessage = new wpsViewerMessage(
                    formatMessage(message),
                    "ERROR",
                    alias
            );
            EventBESA eventBesa = new EventBESA(
                    wpsViewerAgentGuard.class.getName(),
                    viewerMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }
    public static void ws(Object message, String alias) {
        try {
            AdmBESA adm = AdmBESA.getInstance();
            wpsViewerMessage viewerMessage = new wpsViewerMessage(
                    message.toString(),
                    "WS",
                    alias
            );
            EventBESA eventBesa = new EventBESA(
                    wpsViewerAgentGuard.class.getName(),
                    viewerMessage
            );
            AgHandlerBESA agHandler = adm.getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            );
            agHandler.sendEvent(eventBesa);
        } catch (ExceptionBESA e) {
            throw new RuntimeException(e);
        }
    }

    public static void fatal(Object message, String alias) {
        error(message, alias);
    }

    private static String formatMessage(Object message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callingClassName = stackTrace[3].getClassName();
        String callingMethodName = stackTrace[3].getMethodName();
        String simpleClassName = callingClassName.substring(callingClassName.lastIndexOf('.') + 1);
        //if (simpleClassName.contains("DoVitalsTask")) {
        //    return Thread.currentThread().getName()
        //             +"\n-------------------------------------------------------------------------------------------------\n"
        //             + String.format("%23s:%-20s %-65s", simpleClassName, callingMethodName, message);
        // } else {
        return Thread.currentThread().getName() + " " + String.format("%31s:%-20s %-65s", simpleClassName, callingMethodName, message);
        // }
    }

}
