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
package org.wpsim.PeasantFamily.Tasks.L1Survival;

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.BankOffice.Data.BankOfficeMessage;
import org.wpsim.BankOffice.Guards.BankOfficeGuard;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.WellProdSim.wpsStart;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.BankOffice.Data.BankOfficeMessageType.ASK_CURRENT_TERM;

/**
 *
 */
public class DoVoidTask extends Task {

    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        return !believes.isWaiting();
    }

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        wpsReport.info("Adelantado, esperando...", believes.getAlias());
        believes.setWait(false);
    }

    @Override
    public void interruptTask(Believes believes) {

    }

    @Override
    public void cancelTask(Believes believes) {

    }

}
