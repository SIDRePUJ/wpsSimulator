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
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Tasks.L3Development.SearchForHelpAndNecessityTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class SearchForHelpAndNecessityGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static SearchForHelpAndNecessityGoal buildGoal() {
        SearchForHelpAndNecessityTask searchForHelpAndNecessityTask = new SearchForHelpAndNecessityTask();
        Plan searchForHelpAndNecessityPlan = new Plan();
        searchForHelpAndNecessityPlan.addTask(searchForHelpAndNecessityTask);
        RationalRole searchForHelpAndNecessityRole = new RationalRole(
                "SearchForHelpAndNecessityTask",
                searchForHelpAndNecessityPlan);
        return new SearchForHelpAndNecessityGoal(
                wpsStart.getPlanID(),
                searchForHelpAndNecessityRole,
                "SearchForHelpAndNecessityTask",
                GoalBDITypes.OPORTUNITY);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public SearchForHelpAndNecessityGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }

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
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return evaluateEmotionalContribution(stateBDI, 0.9);
    }


}
