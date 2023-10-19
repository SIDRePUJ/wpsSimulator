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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import org.wpsim.Government.LandInfo;
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.Viewer.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class PrepareLandTask extends Task {

    private String landName = "";
    /**
     *
     */
    public PrepareLandTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.PREPARATION)) {
                System.out.println("Planting season for " + currentLandInfo.getLandName());
                landName = currentLandInfo.getLandName();
                break;
            }
        }
        LandInfo landInfo = believes.getLandInfo(landName);
        System.out.println("Preparing land " + landName + " de tipo " + landInfo.getKind());
        // Revisa si es bosque se toma varias horas en preparar y lo pasa a tipo "tierra"
        if (landInfo.getKind().equals("forest")) {
            landInfo.setKind("land");
            landInfo.setCurrentSeason(SeasonType.PREPARATION);
            believes.updateAssignedLands(landInfo);
            believes.useTime(TimeConsumedBy.Deforestation.getTime());
            wpsReport.info("Deforesting process " + landInfo.getFarmName(), believes.getPeasantProfile().getPeasantFamilyAlias());
        }else{
            // Si es tierra normal, se pasa a plantando
            believes.setCurrentSeason(landInfo.getLandName(), SeasonType.PLANTING);
            believes.useTime(TimeConsumedBy.PrepareLandTask.getTime());
        }
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
        this.setTaskFinalized();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
        this.setTaskFinalized();
    }


    /**
     *
     * @param parameters believes from agent
     * @return true if the task was finished
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        LandInfo landInfo = believes.getLandInfo(landName);
        if (landInfo == null) {
            System.out.println("nulo para " + landName);
            return true;
        }
        System.out.println("checking for " + landInfo.getLandName() +" " + landInfo.getCurrentSeason().equals(SeasonType.PLANTING));
        return landInfo.getCurrentSeason().equals(SeasonType.PLANTING);
    }
}
