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
package org.wpsim.PeasantFamily.Goals.L2Obligation;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L2Obligation.LookForLoanTask;
import org.wpsim.Viewer.wpsReport;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class LookForLoanGoal extends GoalBDI {

    /**
     *
     * @return
     */
    public static LookForLoanGoal buildGoal() {
        LookForLoanTask lookForLoanTask = new LookForLoanTask();
        Plan lookForLoanPlan = new Plan();
        lookForLoanPlan.addTask(lookForLoanTask);
        RationalRole lookForLoanRole = new RationalRole(
                "LookForLoanTask",
                lookForLoanPlan);
        LookForLoanGoal lookForLoanGoal = new LookForLoanGoal(
                wpsStart.getPlanID(),
                lookForLoanRole,
                "LookForLoanTask",
                GoalBDITypes.DUTY);
        return lookForLoanGoal;
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public LookForLoanGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        if (believes.getPeasantProfile().getMoney() <= 70000
                //&& !believes.isLoanDenied()
                    && !believes.isAskedForLoanToday()
                        && believes.getToPay()==0 ){
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
        if (believes.haveTimeAvailable(TimeConsumedBy.LookForLoanTask)) {
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
        return 1;
    }

    /**
     *
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        //wpsReport.info(stateBDI.getMachineBDIParams().getPyramidGoals());
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
        return ((PeasantFamilyBDIAgentBelieves) parameters).isAskedForLoanToday();
    }

}
