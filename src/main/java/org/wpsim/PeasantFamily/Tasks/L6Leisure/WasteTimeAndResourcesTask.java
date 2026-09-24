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
package org.wpsim.PeasantFamily.Tasks.L6Leisure;

import BESA.Emotional.EmotionalEvent;
import org.wpsim.WellProdSim.Base.wpsTask;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.PeasantLeisureType;

import java.util.Random;

/**
 *
 * @author jairo
 */
public class WasteTimeAndResourcesTask extends wpsTask {

    /**
     *
     * @param parameters Believes
     */
    private static double cfg(String key, double defaultValue) {
        try {
            String v = org.wpsim.WellProdSim.wpsStart.config.getStringProperty(key);
            return (v == null || v.isBlank()) ? defaultValue : Double.parseDouble(v.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        Random random = org.wpsim.WellProdSim.Util.SimRandom.get();
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(believes.getTimeLeftOnDay());
        if (org.wpsim.WellProdSim.Util.Legacy.LEISURE) {
            believes.getPeasantProfile().useMoney(random.nextInt(100000));
        } else if (random.nextDouble() < cfg("pfagent.leisure.spendProbability", 0.5)) {
            // Revisión TCSS: el ocio puede o no implicar gasto; el gasto tiene tope por evento,
            // por fracción del dinero disponible y por presupuesto mensual.
            double money = Math.max(0, believes.getPeasantProfile().getMoney());
            double cap = Math.min(cfg("pfagent.leisure.maxPerEvent", 30000),
                    cfg("pfagent.leisure.maxMoneyFraction", 0.05) * money);
            double spend = believes.takeLeisureBudget(random.nextDouble() * cap,
                    cfg("pfagent.leisure.monthlyMax", 60000));
            if (spend >= 1) {
                believes.getPeasantProfile().useMoney((int) spend);
            }
        }
        believes.setCurrentPeasantLeisureType(PeasantLeisureType.NONE);
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "LEISURE", "MONEY"));
    }

}
