package org.wpsim.WellProdSim.Base;

import BESA.Emotional.Semantics;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;

public class wpsLandTask extends wpsTask {
    private boolean isStarted = false;

    protected void updateConfig(PeasantFamilyBelieves believes, int totalRequiredTime) {
        if (!isStarted) {
            for (LandInfo currentLandInfo : believes.getAssignedLands()) {
                currentLandInfo.setTotalRequiredTime(totalRequiredTime);
                currentLandInfo.setElapsedWorkTime(0);
            }
            this.isStarted = true;
        }
    }

    protected void increaseWorkDone(PeasantFamilyBelieves believes, String landName, int workDone) {
        EmotionalEvaluator evaluator = new EmotionalEvaluator("Full");
        double factor = 1;
        if (believes.isHaveEmotions()) {
            factor = evaluator.emotionalFactor(believes.getEmotionsListCopy(), Semantics.Emotions.Happiness);
        }
        workDone = (int) Math.round(workDone * factor);
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                currentLandInfo.increaseElapsedWorkTime(workDone);
            }
        }
    }

    protected void resetLand(PeasantFamilyBelieves believes, String landName) {
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                currentLandInfo.resetElapsedWorkTime();
            }
        }
    }

    protected boolean isWorkDone(PeasantFamilyBelieves believes, String landName) {
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                return currentLandInfo.elapsedWorkTimeIsDone();
            }
        }
        return false;
    }

}
