package org.wpsim.WellProdSim.Base;

import BESA.Emotional.Semantics;
import BESA.Log.ReportBESA;
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
        int newWorkDone = 0;
        if (believes.isHaveEmotions()) {
            factor = evaluator.emotionalFactor(believes.getEmotionsListCopy(), Semantics.Emotions.Happiness);
        }
        newWorkDone = (int) Math.ceil(workDone * factor);
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                currentLandInfo.increaseElapsedWorkTime(newWorkDone);
                //ReportBESA.info(currentLandInfo.getLandName() + ", sumando " + newWorkDone + " al trabajo realizado, con un factor de " + factor + ", originalmente era " + workDone);
            }
        }
    }

    protected boolean isWorkDone(PeasantFamilyBelieves believes, String landName) {
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                //ReportBESA.info("currentLandInfo.getLandName() " + currentLandInfo.getLandName());
                return currentLandInfo.elapsedWorkTimeIsDone();
            }
        }
        return false;
    }

}
