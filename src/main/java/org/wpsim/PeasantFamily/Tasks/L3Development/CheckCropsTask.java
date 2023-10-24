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

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Government.LandInfo;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.Viewer.wpsReport;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.ResourceNeededType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

import java.util.List;
import java.util.Set;

import static org.wpsim.World.Messages.WorldMessageType.CROP_INFORMATION;
import static org.wpsim.World.Messages.WorldMessageType.CROP_OBSERVE;

/**
 *
 * @author jairo
 */
public class CheckCropsTask extends Task {

    /**
     *
     */
    public CheckCropsTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        // @TODO: falta calcular el tiempo necesario para el cultivo
        List<LandInfo> landInfos = believes.getAssignedLands();
        for (LandInfo currentLandInfo : landInfos) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.GROWING)) {
                believes.useTime(TimeConsumedBy.CheckCropsTask);
                System.out.println("revisando cultivos en " + currentLandInfo.getLandName());
                try {
                    WorldMessage worldMessage;
                    if (Math.random() < 0.2) {
                        worldMessage = new WorldMessage(
                                CROP_INFORMATION,
                                believes.getPeasantProfile().getCurrentCropName(),
                                believes.getInternalCurrentDate(),
                                currentLandInfo.getLandName()
                        );
                        wpsReport.warn("enviado CROP_INFORMATION a " + currentLandInfo.getLandName(), believes.getPeasantProfile().getPeasantFamilyAlias());
                    } else {
                        worldMessage = new WorldMessage(
                                CROP_OBSERVE,
                                believes.getPeasantProfile().getCurrentCropName(),
                                believes.getInternalCurrentDate(),
                                currentLandInfo.getLandName()
                        );
                        wpsReport.warn("enviado CROP_OBSERVE a " + currentLandInfo.getLandName(), believes.getPeasantProfile().getPeasantFamilyAlias());
                    }
                    AdmBESA.getInstance().getHandlerByAlias(
                            currentLandInfo.getLandName()).sendEvent(
                                    new EventBESA(
                                        WorldGuard.class.getName(),
                                        worldMessage)
                    );
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
        believes.setCheckedToday();
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
    }

    /**
     * Check if the task was finished
     * @param parameters believes from agent
     * @return true if the task was finished
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.isCheckedToday();
    }
}
