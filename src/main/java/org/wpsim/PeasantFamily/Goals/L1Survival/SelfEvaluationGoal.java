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
package org.wpsim.PeasantFamily.Goals.L1Survival;

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.Simulator.Base.wpsGoalBDI;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Tasks.L1Survival.SelfEvaluationTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class SelfEvaluationGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static SelfEvaluationGoal buildGoal() {
        SelfEvaluationTask selfEvaluationTask = new SelfEvaluationTask();
        Plan selfEvaluationPlan = new Plan();
        selfEvaluationPlan.addTask(selfEvaluationTask);
        RationalRole selfEvaluationRole = new RationalRole(
                "SelfEvaluationTask",
                selfEvaluationPlan);
        return new SelfEvaluationGoal(
                wpsStart.getPlanID(),
                selfEvaluationRole,
                "SelfEvaluationTask",
                GoalBDITypes.SURVIVAL);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public SelfEvaluationGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
    public double evaluateViability(Believes parameters) throws KernellAgentEventExceptionBESA {
        return 1;
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

        if (ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()) < -7) {
            System.out.println("Jump detectado para PeasantFamilyAlias: " + believes.getPeasantProfile().getPeasantFamilyAlias() + " getDaysBetweenDates" + ControlCurrentDate.getInstance().getDaysBetweenDates(believes.getInternalCurrentDate()));
            return 1;
        }else {
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
    public double evaluatePlausibility(Believes parameters) throws KernellAgentEventExceptionBESA {
        return 1;
    }

    /**
     *
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return 0.9;
    }

    /**
     *
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return true;
    }

}
