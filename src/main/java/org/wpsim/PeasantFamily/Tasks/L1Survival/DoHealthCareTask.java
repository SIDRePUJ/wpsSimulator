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
package org.wpsim.PeasantFamily.Tasks.L1Survival;

import BESA.Emotional.EmotionalEvent;
import BESA.Emotional.Semantics;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;
import org.wpsim.WellProdSim.Base.wpsTask;
import rational.mapping.Believes;

/**
 *
 * @author jairo
 */
public class DoHealthCareTask extends wpsTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        double factor = 1;
        EmotionalEvaluator evaluator = new EmotionalEvaluator("Full");
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        if (believes.isHaveEmotions()) {
            factor = evaluator.emotionalFactor(believes.getEmotionsListCopy(), Semantics.Emotions.Happiness);
        }
        believes.useTime(TimeConsumedBy.DoHealthCareTask);
        believes.getPeasantProfile().increaseHealth(factor);
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "DOVITALS", "FOOD"));
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

    /**
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
    }
}

