/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Tasks.L6Leisure;

import BESA.Emotional.EmotionalEvent;
import org.wpsim.Control.Data.Coin;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import java.util.Random;

/**
 *
 * @author jairo
 */
public class SpendFamilyTimeTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());

        if (Coin.flipCoin()) {
            believes.useTime(TimeConsumedBy.SpendFamilyTimeTask);
        } else {
            believes.useTime(believes.getTimeLeftOnDay());
        }
        believes.processEmotionalEvent(
                new EmotionalEvent("FRIEND", "LEISURE", "FOOD")
        );
    }

}
