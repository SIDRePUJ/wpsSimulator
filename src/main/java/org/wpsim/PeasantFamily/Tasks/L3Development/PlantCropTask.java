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
import org.wpsim.Viewer.wpsReport;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Messages.WorldMessage;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;

import static org.wpsim.World.Messages.WorldMessageType.CROP_INIT;

/**
 *
 * @author jairo
 */
public class PlantCropTask extends Task {

    /**
     *
     */
    public PlantCropTask() {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));

        try {
            AdmBESA adm = AdmBESA.getInstance();
            AgHandlerBESA ah = adm.getHandlerByAlias(
                    believes.getPeasantProfile().getPeasantFamilyLandAlias()
            );

            WorldMessage worldMessage = new WorldMessage(
                    CROP_INIT,
                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                    believes.getInternalCurrentDate(),
                    believes.getPeasantProfile().getPeasantFamilyAlias()
            );

            EventBESA ev = new EventBESA(
                    WorldGuard.class.getName(),
                    worldMessage);
            ah.sendEvent(ev);

            believes.getPeasantProfile().useSeeds(
                    believes.getPeasantProfile().getRiceSeedsByHectare()
            );
            // 2500 kilo * 20 kilos de semillas
            believes.getPeasantProfile().useMoney(
                    believes.getPriceList().get("seeds").getCost()
                    * 20
            );

        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
        believes.setCurrentSeason(SeasonType.GROWING);
        this.setTaskFinalized();
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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.getCurrentSeason() == SeasonType.GROWING;
    }
}
