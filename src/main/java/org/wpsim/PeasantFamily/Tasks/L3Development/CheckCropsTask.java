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
import org.wpsim.Control.Data.Coin;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Guards.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import org.wpsim.World.Messages.WorldMessageType;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.World.Messages.WorldMessageType.CROP_INFORMATION;
import static org.wpsim.World.Messages.WorldMessageType.CROP_OBSERVE;

/**
 * @author jairo
 */
public class CheckCropsTask extends wpsTask {

    /**
     * Executes the CheckCropsTask
     *
     * @param parameters Believes of the agent
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.CheckCropsTask);

        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.GROWING)) {
                try {
                    wpsReport.warn("enviado CROP_OBSERVE a " + currentLandInfo.getLandName(), believes.getPeasantProfile().getPeasantFamilyAlias());
                    if (Coin.flipCoin()) {
                        AdmBESA.getInstance().getHandlerByAlias(
                                currentLandInfo.getLandName()
                        ).sendEvent(
                                new EventBESA(
                                        WorldGuard.class.getName(),
                                        new WorldMessage(
                                                CROP_INFORMATION,
                                                believes.getPeasantProfile().getCurrentCropName(),
                                                believes.getInternalCurrentDate(),
                                                currentLandInfo.getLandName()
                                        )
                                )
                        );
                    } else {
                        AdmBESA.getInstance().getHandlerByAlias(
                                currentLandInfo.getLandName()
                        ).sendEvent(
                                new EventBESA(
                                        WorldGuard.class.getName(),
                                        new WorldMessage(
                                                CROP_OBSERVE,
                                                believes.getPeasantProfile().getCurrentCropName(),
                                                believes.getInternalCurrentDate(),
                                                currentLandInfo.getLandName()
                                        )
                                )
                        );
                    }
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
    }

}
