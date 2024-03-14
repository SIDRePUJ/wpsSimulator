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
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.AgroEcosystem.Guards.AgroEcosystemGuard;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.CropCareType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType.CROP_IRRIGATION;

/**
 * @author jairo
 */
public class IrrigateCropsTask extends wpsTask {

    /**
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
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
                                    AgroEcosystemGuard.class.getName(), new AgroEcosystemMessage(
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
