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
package org.wpsim.PeasantFamily.Goals.L5Social;

import org.wpsim.Simulator.Base.wpsGoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Tasks.L5Social.ProvideCollaborationTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class ProvideCollaborationGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static ProvideCollaborationGoal buildGoal() {
        ProvideCollaborationTask provideCollaborationTask = new ProvideCollaborationTask();
        Plan provideCollaborationPlan = new Plan();
        provideCollaborationPlan.addTask(provideCollaborationTask);
        RationalRole provideCollaborationRole = new RationalRole(
                "ProvideCollaborationTask",
                provideCollaborationPlan);
        return new ProvideCollaborationGoal(
                wpsStart.getPlanID(),
                provideCollaborationRole,
                "ProvideCollaborationTask",
                GoalBDITypes.NEED);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public ProvideCollaborationGoal(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
        //wpsReport.info("");
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

        if (believes.getPeasantProfile().getPurpose().equals("worker")) {
            if (believes.isAskedForContractor()) {
                return 0;
            } else {
                return 1;
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

}
