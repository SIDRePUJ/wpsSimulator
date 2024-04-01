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
package org.wpsim.PeasantFamily.Goals.L3Development;

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L3Development.CheckCropsTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 * @author jairo
 */
public class CheckCropsGoal extends wpsGoalBDI {

    /**
     * @return
     */
    public static CheckCropsGoal buildGoal() {
        CheckCropsTask checkCropsTask = new CheckCropsTask();
        Plan checkCropsPlan = new Plan();
        checkCropsPlan.addTask(checkCropsTask);
        RationalRole checkCropsRole = new RationalRole(
                "CheckCropsTask",
                checkCropsPlan);
        return new CheckCropsGoal(
                wpsStart.getPlanID(),
                checkCropsRole,
                "CheckCropsTask",
                GoalBDITypes.OPORTUNITY);
    }

    /**
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public CheckCropsGoal(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
    }

    /**
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double detectGoal(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }
            for (LandInfo currentLandInfo : believes.getAssignedLands()) {
                if (currentLandInfo.getCurrentSeason().equals(SeasonType.GROWING) &&
                        believes.haveTimeAvailable(TimeConsumedBy.CheckCropsTask)) {
                    return 1;
                }
            }
        return 0;
    }

    /**
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return evaluateEmotionalContribution(stateBDI, 0.75);
        //return evaluateSingleEmotionContribution(stateBDI, Semantics.Emotions.Happiness, 0.9);
    }

}
