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
package org.wpsim.PeasantFamily.Goals.L4SkillsResources;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.PeasantFamily.Goals.Base.wpsGoalBDI;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L4SkillsResources.ObtainALandTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class ObtainALandGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static ObtainALandGoal buildGoal() {
        ObtainALandTask task = new ObtainALandTask();
        Plan plan = new Plan();
        plan.addTask(task);
        RationalRole role = new RationalRole(
                "ObtainALandTask",
                plan);
        return new ObtainALandGoal(
                wpsStart.getPlanID(),
                role,
                "ObtainALandTask",
                GoalBDITypes.REQUIREMENT);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public ObtainALandGoal(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluateViability(Believes parameters) throws KernellAgentEventExceptionBESA {
        //wpsReport.info("");
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.getPeasantProfile().getHealth() > 0) {
            return 1;
        } else {
            return 0;
        }
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

        if (believes.getPeasantProfile().getFarmName()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluatePlausibility(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.haveTimeAvailable(TimeConsumedBy.ObtainALandTask)) {
            return 1;
        } else {
            return 0;
        }
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
