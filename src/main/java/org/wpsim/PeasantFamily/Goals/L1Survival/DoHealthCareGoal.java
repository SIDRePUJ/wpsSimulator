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
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Tasks.L1Survival.DoHealthCareTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class DoHealthCareGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static DoHealthCareGoal buildGoal() {
        DoHealthCareTask doHealthCareTask = new DoHealthCareTask();
        Plan doHealthCarePlan = new Plan();
        doHealthCarePlan.addTask(doHealthCareTask);
        RationalRole doHealthCareRole = new RationalRole(
                "DoHealthCareTask",
                doHealthCarePlan);
        return new DoHealthCareGoal(
                wpsStart.getPlanID(),
                doHealthCareRole,
                "DoHealthCareTask",
                GoalBDITypes.SURVIVAL);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public DoHealthCareGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        //wpsReport.info("getHealthProgramsAvailability=" + believes.getProfile().getHealthProgramsAvailability());
        return 1;
        /*if (believes.getPeasantProfile().getHealthProgramsAvailability() > 0) {
            return 1;
        } else {
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
    public double detectGoal(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        int timeToHeal;

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }

        if (believes.getPeasantProfile().getMoney() > 2000000){
            timeToHeal = 60;
        }else{
            timeToHeal = 30;
        }

        if (believes.getPeasantProfile().getHealth() < timeToHeal) {
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
        //wpsReport.info("");
        return 1;
    }
}
