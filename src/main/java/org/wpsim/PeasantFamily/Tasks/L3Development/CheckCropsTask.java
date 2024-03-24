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

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.SimulationControl.Data.Coin;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.AgroEcosystem.Guards.AgroEcosystemGuard;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType.CROP_INFORMATION;
import static org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType.CROP_OBSERVE;

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
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.useTime(TimeConsumedBy.CheckCropsTask.getTime());
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "CHECKCROPS", "FOOD"));
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.GROWING)) {
                if (Coin.flipCoin()) {
                    try {
                        AdmBESA.getInstance().getHandlerByAlias(
                                currentLandInfo.getLandName()
                        ).sendEvent(
                                new EventBESA(
                                        AgroEcosystemGuard.class.getName(),
                                        new AgroEcosystemMessage(
                                                CROP_INFORMATION,
                                                currentLandInfo.getCropName(),
                                                believes.getInternalCurrentDate(),
                                                currentLandInfo.getLandName()
                                        )
                                )
                        );
                    } catch (ExceptionBESA ex) {
                        wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                    }
                    wpsReport.warn("enviado CROP_INFORMATION a " + currentLandInfo.getLandName(), believes.getPeasantProfile().getPeasantFamilyAlias());
                    //System.out.println(believes.getAlias() + " enviado CROP_INFORMATION a " + currentLandInfo.getLandName());
                } else {
                    try {
                        AdmBESA.getInstance().getHandlerByAlias(
                                currentLandInfo.getLandName()
                        ).sendEvent(
                                new EventBESA(
                                        AgroEcosystemGuard.class.getName(),
                                        new AgroEcosystemMessage(
                                                CROP_OBSERVE,
                                                currentLandInfo.getCropName(),
                                                believes.getInternalCurrentDate(),
                                                currentLandInfo.getLandName()
                                        )
                                )
                        );
                    } catch (ExceptionBESA ex) {
                        wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                    }
                    //System.out.println(believes.getAlias() + " enviado CROP_OBSERVE a " + currentLandInfo.getLandName());
                    wpsReport.warn("enviado CROP_OBSERVE a " + currentLandInfo.getLandName(), believes.getPeasantProfile().getPeasantFamilyAlias());
                }
                believes.addTaskToLog(believes.getInternalCurrentDate());
            }
        }
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
