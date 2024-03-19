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
package org.wpsim.PeasantFamily.Goals.L1Survival;

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Tasks.L1Survival.DoVoidTask;
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;
import rational.tasks.VoidTask;

/**
 *
 * @author jairo
 */
public class DoVoidGoal extends wpsGoalBDI {

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public DoVoidGoal(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
    }

    /**
     *
     * @return
     */
    public static DoVoidGoal buildGoal() {
        DoVoidTask doVoidTask = new DoVoidTask();
        Plan doVoidTaskPlan = new Plan();
        doVoidTaskPlan.addTask(doVoidTask);
        RationalRole doVoidTaskRole = new RationalRole(
                "doVoidTask",
                doVoidTaskPlan);
        return new DoVoidGoal(
                wpsStart.getPlanID(),
                doVoidTaskRole,
                "doVoidTask",
                GoalBDITypes.SURVIVAL);
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double detectGoal(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;

        if (believes.isWaiting()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return 1;
    }
}
