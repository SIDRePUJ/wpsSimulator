package org.wpsim.PeasantFamily.Tasks.Base;

import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;

public class wpsLandTask extends wpsTask {
    private boolean isStarted = false;

    protected void updateConfig(PeasantFamilyBDIAgentBelieves believes, int totalRequiredTime) {
        if (!isStarted) {
            for (LandInfo currentLandInfo : believes.getAssignedLands()) {
                currentLandInfo.setTotalRequiredTime(totalRequiredTime);
                currentLandInfo.setElapsedWorkTime(0);
            }
            this.isStarted = true;
        }
    }

    protected void increaseWorkDone(PeasantFamilyBDIAgentBelieves believes, String landName, int workDone) {
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                currentLandInfo.increaseElapsedWorkTime(workDone);
            }
        }
    }

    protected void resetLand(PeasantFamilyBDIAgentBelieves believes, String landName) {
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                currentLandInfo.resetElapsedWorkTime();
            }
        }
    }

    protected boolean isWorkDone(PeasantFamilyBDIAgentBelieves believes, String landName) {
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getLandName().equals(landName)) {
                return currentLandInfo.elapsedWorkTimeIsDone();
            }
        }
        return false;
    }

}
