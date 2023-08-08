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

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Bank.BankAgentGuard;
import org.wpsim.Bank.BankMessage;
import org.wpsim.Control.ControlCurrentDate;
import org.wpsim.Control.DateHelper;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.Viewer.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.Bank.BankMessageType.ASK_CURRENT_TERM;

/**
 *
 *
 */
public class DoVitalsTask extends Task {

    /**
     *
     */
    public DoVitalsTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;

        believes.setNewDay(false);
        // Use time
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        believes.getPeasantProfile().discountDailyMoney();

        /*wpsReport.debug("DoVitalsTask "
                        + "health: " + believes.getPeasantProfile().getHealth()
                        + " currentDate: " + believes.getInternalCurrentDate(),
                believes.getPeasantProfile().getPeasantFamilyAlias()
        );*/

        // Check crop season
        if (DateHelper.differenceDaysBetweenTwoDates(
                believes.getInternalCurrentDate(),
                believes.getPeasantProfile().getStartRiceSeason()) == 0) {
            believes.setCurrentSeason(SeasonType.PREPARATION);
        }

        // En que gasta el tiempo el día
        believes.setRandomCurrentPeasantLeisureType();
        // Check debts
        checkBankDebt(believes);
        // Check Sync
        checkWeek(believes);

        //}
        this.setTaskFinalized();

    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {}

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {}

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.isNewDay()) {
            return false;
        } else {
            return true;
        }
    }

    private void checkWeek(PeasantFamilyBDIAgentBelieves believes) {
        if (believes.getCurrentDay() % wpsStart.DAYS_TO_CHECK == 0) {
            believes.setWeekBlock();
        }
    }

    private void checkBankDebt(PeasantFamilyBDIAgentBelieves believes) {
        // Check for the loan pay amount only on first day of month
        if (ControlCurrentDate.getInstance().isFirstDayOfMonth(believes.getInternalCurrentDate())
                && believes.getCurrentDay() > 6) {
            try {
                AdmBESA adm = AdmBESA.getInstance();
                AgHandlerBESA ah = adm.getHandlerByAlias(wpsStart.config.getBankAgentName());

                BankMessage bankMessage = new BankMessage(
                        ASK_CURRENT_TERM,
                        believes.getPeasantProfile().getPeasantFamilyAlias()
                );

                EventBESA ev = new EventBESA(
                        BankAgentGuard.class.getName(),
                        bankMessage);
                ah.sendEvent(ev);

            } catch (ExceptionBESA ex) {
                wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
            }
        }
    }
}
