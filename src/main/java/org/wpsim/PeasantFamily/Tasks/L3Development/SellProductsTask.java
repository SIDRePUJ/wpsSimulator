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

import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class SellProductsTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        // @TODO: Cambiar a la venta real con el agente social market
        believes.useTime(TimeConsumedBy.valueOf(this.getClass().getSimpleName()));
        believes.getPeasantProfile().increaseFarmReady();
        believes.addTaskToLog(believes.getInternalCurrentDate());
    }

}
