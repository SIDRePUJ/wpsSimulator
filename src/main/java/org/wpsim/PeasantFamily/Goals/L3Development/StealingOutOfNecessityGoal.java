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
package org.wpsim.PeasantFamily.Goals.L3Development;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L3Development.StealingOutOfNecessityTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class StealingOutOfNecessityGoal extends GoalBDI {

    /**
     *
     * @return
     */
    public static StealingOutOfNecessityGoal buildGoal() {
        StealingOutOfNecessityTask stealingOutOfNecessityTask = new StealingOutOfNecessityTask();
        Plan stealingOutOfNecessityPlan = new Plan();
        stealingOutOfNecessityPlan.addTask(stealingOutOfNecessityTask);
        RationalRole stealingOutOfNecessityRole = new RationalRole(
                "StealingOutOfNecessityTask",
                stealingOutOfNecessityPlan);
        StealingOutOfNecessityGoal stealingOutOfNecessityGoal = new StealingOutOfNecessityGoal(
                wpsStart.getPlanID(),
                stealingOutOfNecessityRole,
                "StealingOutOfNecessityTask",
                GoalBDITypes.OPORTUNITY);
        return stealingOutOfNecessityGoal;
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public StealingOutOfNecessityGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        if (believes.getPeasantProfile().getMoney() <= 15000) {
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
    public double evaluatePlausibility(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.getPeasantProfile().getHealth() > 0) {
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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.haveTimeAvailable(TimeConsumedBy.StealingOutOfNecessityTask)) {
            //wpsReport.trace("SI " + believes.toSmallJson(), believes.getPeasantProfile().getPeasantFamilyAlias());
            return 1;
        } else {
            //wpsReport.trace("NO " + believes.toSmallJson(), believes.getPeasantProfile().getPeasantFamilyAlias());
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
        return ((PeasantFamilyBDIAgentBelieves) parameters).isRobbedToday();
    }

}
