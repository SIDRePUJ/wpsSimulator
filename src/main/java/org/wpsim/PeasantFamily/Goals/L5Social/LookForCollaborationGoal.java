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
package org.wpsim.PeasantFamily.Goals.L5Social;

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Goals.Base.wpsGoalBDI;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Tasks.L5Social.LookForCollaborationTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class LookForCollaborationGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static LookForCollaborationGoal buildGoal() {
        LookForCollaborationTask lookForCollaborationTask = new LookForCollaborationTask();
        Plan lookForCollaborationPlan = new Plan();
        lookForCollaborationPlan.addTask(lookForCollaborationTask);
        RationalRole lookForCollaborationRole = new RationalRole(
                "LookForCollaborationTask",
                lookForCollaborationPlan);
        return new LookForCollaborationGoal(
                wpsStart.getPlanID(),
                lookForCollaborationRole,
                "LookForCollaborationTask",
                GoalBDITypes.NEED);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public LookForCollaborationGoal(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double detectGoal(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }

        if (believes.getAssignedLands().size() > 1 && believes.getPeasantFamilyHelper().isEmpty()) {
            for (LandInfo currentLandInfo : believes.getAssignedLands()) {
                if (!currentLandInfo.getCurrentSeason().equals(SeasonType.NONE)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     *
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return evaluateEmotionalContribution(stateBDI, 1.0);
    }

    @Override
    public double evaluateViability(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        // Para dos semanas de ayuda @TODO: AJUSTAR
        if (believes.getPeasantProfile().getMoney() > (50000*believes.getAssignedLands().size()*10)) {
            return 1;
        }
        return 0;
    }
}
