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
package org.wpsim.PeasantFamily.Goals.L6Leisure;

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L6Leisure.LeisureActivitiesTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 *
 */
public class LeisureActivitiesGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static LeisureActivitiesGoal buildGoal() {
        LeisureActivitiesTask leisureActivitiesTask = new LeisureActivitiesTask();
        Plan leisureActivitiesPlan = new Plan();
        leisureActivitiesPlan.addTask(leisureActivitiesTask);
        RationalRole leisureActivitiesRole = new RationalRole(
                "LeisureActivitiesTask",
                leisureActivitiesPlan);
        return new LeisureActivitiesGoal(
                wpsStart.getPlanID(),
                leisureActivitiesRole,
                "LeisureActivitiesTask",
                GoalBDITypes.ATTENTION_CYCLE);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public LeisureActivitiesGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }
        if (believes.getPeasantProfile().getMoney() > 0) {
            return 1;
        }

        return 0;
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluatePlausibility(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        if (believes.haveTimeAvailable(TimeConsumedBy.LeisureActivitiesTask)) {
            //wpsReport.trace("SI " + believes.toSmallJson(), believes.getPeasantProfile().getPeasantFamilyAlias());
            return 1;
        } else {
            //wpsReport.trace("NO " + believes.toSmallJson(), believes.getPeasantProfile().getPeasantFamilyAlias());
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
    public double evaluateViability(Believes parameters) throws KernellAgentEventExceptionBESA {
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
        return evaluateEmotionalContribution(stateBDI, 0.2);
    }

}
