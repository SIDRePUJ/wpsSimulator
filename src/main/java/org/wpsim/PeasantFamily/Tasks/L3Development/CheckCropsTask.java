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

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import java.util.List;

import static org.wpsim.World.Messages.WorldMessageType.CROP_INFORMATION;
import static org.wpsim.World.Messages.WorldMessageType.CROP_OBSERVE;

/**
 *
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

        believes.processEmotionalEvent(
                new EmotionalEvent("FAMILY", "STARVING", "FOOD")
        );

        believes.useTime(TimeConsumedBy.CheckCropsTask);
        // @TODO: falta calcular el tiempo necesario para el cultivo
        List<LandInfo> landInfos = believes.getAssignedLands();
        for (LandInfo currentLandInfo : landInfos) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.GROWING)) {
                // TODO: Usar menos tiempo si tiene ayuda
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
                            currentLandInfo.getLandName()
                    ).sendEvent(
                                    new EventBESA(
                                        WorldGuard.class.getName(),
                                        worldMessage
                                    )
                    );
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
    }

}
