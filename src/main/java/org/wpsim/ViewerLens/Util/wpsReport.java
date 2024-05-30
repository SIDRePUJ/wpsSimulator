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
package org.wpsim.ViewerLens.Util;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.ViewerLens.Data.ViewerLensMessage;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.ViewerLens.Guards.ViewerLensGuard;

public class wpsReport {

    private wpsReport() {

    }

    public static void mental(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            message.toString(),
                            "MENTAL",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
        }
    }

    public static void trace(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            message.toString(),
                            "TRACE",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
        }
    }

    public static void debug(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            formatMessage(message),
                            "DEBUG",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
        }
    }

    public static void info(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            formatMessage(message),
                            "INFO",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
        }
    }

    public static void warn(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            formatMessage(message),
                            "WARN",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
        }
    }

    public static void error(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            formatMessage(message),
                            "ERROR",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
        }
    }

    public static void ws(Object message, String alias) {
        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsStart.config.getViewerAgentName()
            ).sendEvent(new EventBESA(
                    ViewerLensGuard.class.getName(),
                    new ViewerLensMessage(
                            message.toString(),
                            "WS",
                            alias
                    )
            ));
        } catch (ExceptionBESA e) {
            System.err.println("falta comunicación.");
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
