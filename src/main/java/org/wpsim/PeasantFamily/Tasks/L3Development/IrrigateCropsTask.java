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
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Guards.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.CropCareType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.World.Messages.WorldMessageType.CROP_IRRIGATION;

/**
 * @author jairo
 */
public class IrrigateCropsTask extends wpsTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        double waterUsed = believes.getPeasantProfile().getCropSizeHA() * 30;

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getKind().equals("water")) {
                waterUsed = 0;
                wpsReport.info("🚰🚰🚰🚰 tiene agua", believes.getPeasantProfile().getPeasantFamilyAlias());
                break;
            } else {
                wpsReport.info("NO tiene agua", believes.getPeasantProfile().getPeasantFamilyAlias());
            }
        }

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentCropCareType().equals(CropCareType.IRRIGATION)) {
                //System.out.println("🚰🚰🚰🚰 Irrigación de cultivo " + currentLandInfo.getLandName() + " con " + waterUsed + " de " + believes.getPeasantProfile().getPeasantFamilyAlias());
                currentLandInfo.setCurrentCropCareType(CropCareType.NONE);
                believes.getPeasantProfile().useWater((int) waterUsed);
                try {
                    AdmBESA.getInstance().getHandlerByAlias(currentLandInfo.getLandName()).sendEvent(
                            new EventBESA(
                                    WorldGuard.class.getName(), new WorldMessage(
                                    CROP_IRRIGATION,
                                    currentLandInfo.getLandName(),
                                    believes.getInternalCurrentDate(),
                                    believes.getPeasantProfile().getPeasantFamilyAlias()
                            )
                            )
                    );
                    wpsReport.info("🚰🚰🚰🚰 Irrigación de cultivo " + currentLandInfo.getLandName() + " con " + waterUsed, believes.getPeasantProfile().getPeasantFamilyAlias());
                    return;
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
    }
}
