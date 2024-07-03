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

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L3Development.GetTechAssistanceTask;
import org.wpsim.SimulationControl.Data.Coin;
import org.wpsim.SimulationControl.Data.DateHelper;
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class GetTechAssistanceGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static GetTechAssistanceGoal buildGoal() {
        GetTechAssistanceTask getTechAssistanceTask = new GetTechAssistanceTask();
        Plan getTechAssistancePlan = new Plan();
        getTechAssistancePlan.addTask(getTechAssistanceTask);
        RationalRole getTechAssistanceRole = new RationalRole(
                "GetTechAssistanceTask",
                getTechAssistancePlan);
        return new GetTechAssistanceGoal(
                wpsStart.getPlanID(),
                getTechAssistanceRole,
                "GetTechAssistanceTask",
                GoalBDITypes.OPORTUNITY);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public GetTechAssistanceGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        if (believes.haveTimeAvailable(TimeConsumedBy.GetTechAssistancePlan)) {
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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }

        if (Coin.flipCoin()) {
            if (believes.getTrainingLevel() <= 0.4 && believes.isTrainingAvailable()) {
                return 1;
            }
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
        return evaluateEmotionalContribution(stateBDI, 0.8);
    }

}
