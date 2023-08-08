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

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L6Leisure.LeisureActivitiesTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 *
 */
public class LeisureActivitiesGoal extends GoalBDI {

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
        LeisureActivitiesGoal leisureActivitiesGoalBDI = new LeisureActivitiesGoal(
                wpsStart.getPlanID(),
                leisureActivitiesRole,
                "LeisureActivitiesTask",
                GoalBDITypes.ATTENTION_CYCLE);
        return leisureActivitiesGoalBDI;
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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;

        if (believes.getPeasantProfile().getHealth() > 0){
            return 1;
        }else {
            return 0;
        }

        //if (believes.getCurrentPeasantLeisureType() == PeasantLeisureType.LEISURE) {
        /*if (believes.getCurrentActivity() == PeasantActivityType.LEISURE) {
            //wpsReport.debug("Detectó");
            wpsReport.debug("detectGoal " + believes.getTimeLeftOnDay(), believes.getPeasantProfile().getPeasantFamilyAlias());
            return 1;
        } else {
            //wpsReport.debug(believes.toJson());
            return 0;
        }*/
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
        if (believes.haveTimeAvailable(TimeConsumedBy.LeisureActivitiesTask)) {
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
        //wpsReport.info("evaluateContribution " + stateBDI.getMachineBDIParams().getAttentionCycleThreshold());
        return 0.7;
    }

    /**
     *
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) stateBDI.getBelieves();
        return believes.getPeasantProfile().getHealth() > 0;
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public boolean goalSucceeded(Believes parameters) throws KernellAgentEventExceptionBESA {
        //wpsReport.debug("goalSucceededgoalSucceeded");
        return true;
    }

}
