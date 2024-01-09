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
package org.wpsim.PeasantFamily.Tasks.L5Social;

import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Simulator.Config.wpsConfig;
import org.wpsim.Society.Data.SocietyDataMessage;
import org.wpsim.Society.Guards.SocietyAgentRequestHelpGuard;
import rational.mapping.Believes;
import rational.mapping.Task;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

/**
 *
 * @author jairo
 */
public class LookForCollaborationTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.LookForCollaborationTask);

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    wpsConfig.getInstance().getSocietyAgentName()
            ).sendEvent(
                    new EventBESA(SocietyAgentRequestHelpGuard.class.getName(),
                            new SocietyDataMessage(
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    believes.getPeasantProfile().getPeasantFamilyAlias(),
                                    5
                            )
                    )
            );
        } catch (ExceptionBESA ex) {
            System.out.println(ex.getMessage());
        }
    }
}
