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
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.Viewer.wpsReport;
import org.wpsim.World.Agent.WorldAgent;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

import static org.wpsim.World.Messages.WorldMessageType.CROP_HARVEST;

/**
 *
 * @author jairo
 */
public class HarvestCropsTask extends Task {

    private String landName;
    private boolean finished;

    /**
     *
     */
    public HarvestCropsTask() {
        finished = false;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        finished = false;
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentSeason().equals(SeasonType.HARVEST)) {
                landName = currentLandInfo.getLandName();
                try {
                    WorldMessage worldMessage = new WorldMessage(
                            CROP_HARVEST,
                            believes.getPeasantProfile().getCurrentCropName(),
                            believes.getInternalCurrentDate(),
                            believes.getPeasantProfile().getPeasantFamilyAlias());
                    EventBESA ev = new EventBESA(
                            WorldGuard.class.getName(),
                            worldMessage);
                    AdmBESA.getInstance().getHandlerByAlias(landName).sendEvent(ev);
                    wpsReport.debug("enviando mensaje de corte", believes.getPeasantProfile().getPeasantFamilyAlias());
                    WorldAgent land = (WorldAgent) AdmBESA.getInstance().getHandlerByAlias(landName).getAg();
                    land.shutdownAgent();
                } catch (ExceptionBESA ex) {
                    wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
                }
            }
        }
        believes.setCurrentSeason(landName, SeasonType.SELL_CROP);
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
