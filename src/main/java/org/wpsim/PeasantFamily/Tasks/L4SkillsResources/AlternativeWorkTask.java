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
package org.wpsim.PeasantFamily.Tasks.L4SkillsResources;

import BESA.Emotional.EmotionalEvent;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.WellProdSim.Base.wpsTask;
import rational.mapping.Believes;

import java.util.Random;

/**
 *
 * @author jairo
 */
public class AlternativeWorkTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.useTime(TimeConsumedBy.AlternativeWorkTask);
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "WORK", "MONEY"));
        // https://www.eluniversal.com.co/cartagena/poner-a-trabajar-una-mototaxi-asi-se-mueve-este-negocio-en-cartagena-CX7541242
        // http://www.scielo.org.co/scielo.php?script=sci_arttext&pid=S0120-55522013000300012
        Random random = new Random();
        believes.getPeasantProfile().increaseMoney(random.nextInt(25001) + 10000);
    }

}