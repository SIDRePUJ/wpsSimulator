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
import org.wpsim.Viewer.wpsReport;
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

    String landName;
    /**
     *
     */
    public IrrigateCropsTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentCropCareType().equals(CropCareType.IRRIGATION)) {
                landName = currentLandInfo.getLandName();
            }
        }

        double waterUsed = believes.getPeasantProfile().getCropSizeHA() * 30;
        for (LandInfo currentLandInfoWater : believes.getAssignedLands()) {
            if (currentLandInfoWater.getKind().equals("water")) {
                waterUsed = 0;
                break;
            }
        }

        try {
            AdmBESA adm = AdmBESA.getInstance();
            AgHandlerBESA ah = adm.getHandlerByAlias(landName);

            WorldMessage worldMessage;
            worldMessage = new WorldMessage(
                    CROP_IRRIGATION,
                    believes.getPeasantProfile().getCurrentCropName(),
                    believes.getInternalCurrentDate(),
                    believes.getPeasantProfile().getPeasantFamilyAlias());
            EventBESA ev = new EventBESA(
                    WorldGuard.class.getName(),
                    worldMessage);
            ah.sendEvent(ev);

            believes.getPeasantProfile().useWater((int) waterUsed);
            believes.setCurrentCropCareType(landName,CropCareType.NONE);

        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        wpsReport.info("🚰🚰🚰🚰 Irrigación de cultivos con " + waterUsed, believes.getPeasantProfile().getPeasantFamilyAlias());
        //this.setTaskFinalized();

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
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        LandInfo landInfo = believes.getLandInfo(landName);
        return landInfo.getCurrentCropCareType().equals(CropCareType.NONE);
    }
}
