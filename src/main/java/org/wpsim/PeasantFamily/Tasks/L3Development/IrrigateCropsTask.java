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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

import static org.wpsim.World.Messages.WorldMessageType.CROP_IRRIGATION;

/**
 *
 * @author jairo
 */
public class IrrigateCropsTask extends Task {

    private boolean finished;

    /**
     *
     */
    public IrrigateCropsTask() {
        finished = false;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        finished = false;
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        double waterUsed = believes.getPeasantProfile().getCropSizeHA() * 30;
        boolean foundLandForIrrigation = false;

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getKind().equals("water")) {
                waterUsed = 0;
                break;
            }
        }

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (!foundLandForIrrigation && currentLandInfo.getCurrentCropCareType().equals(CropCareType.IRRIGATION)) {
                try {
                    WorldMessage worldMessage = new WorldMessage(
                            CROP_IRRIGATION,
                            currentLandInfo.getLandName(),
                            believes.getInternalCurrentDate(),
                            believes.getPeasantProfile().getPeasantFamilyAlias());
                    AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName()).sendEvent(
                            new EventBESA(WorldGuard.class.getName(), worldMessage)
                    );
                    believes.getPeasantProfile().useWater((int) waterUsed);
                    currentLandInfo.setCurrentCropCareType(CropCareType.NONE);
                    wpsReport.info("🚰🚰🚰🚰 Irrigación de cultivos con " + waterUsed, believes.getPeasantProfile().getPeasantFamilyAlias());
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
        finished = true;
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
     *
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        return finished;
    }
}
