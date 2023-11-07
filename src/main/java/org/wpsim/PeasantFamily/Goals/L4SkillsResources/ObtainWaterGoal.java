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
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.ResourceNeededType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L4SkillsResources.ObtainWaterTask;
import org.wpsim.Viewer.Data.wpsReport;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class ObtainWaterGoal extends GoalBDI {

    /**
     *
     * @return
     */
    public static ObtainWaterGoal buildGoal() {
        ObtainWaterTask obtainWaterTask = new ObtainWaterTask();
        Plan obtainWaterPlan = new Plan();
        obtainWaterPlan.addTask(obtainWaterTask);
        RationalRole obtainWaterRole = new RationalRole(
                "ObtainWaterTask",
                obtainWaterPlan);
        ObtainWaterGoal obtainWaterGoal = new ObtainWaterGoal(
                wpsStart.getPlanID(),
                obtainWaterRole,
                "ObtainWaterTask",
                GoalBDITypes.REQUIREMENT);
        return obtainWaterGoal;
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public ObtainWaterGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        if (believes.getCurrentResourceNeededType() == ResourceNeededType.WATER
                && believes.getPeasantProfile().getMoney() >= 1000000) {
            wpsReport.warn("Detectada necesidad de comprar agua ", believes.getPeasantProfile().getPeasantFamilyAlias());
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
    public double evaluatePlausibility(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.haveTimeAvailable(TimeConsumedBy.ObtainWaterTask)) {
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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
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
        //wpsReport.info("");
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
        return true;
    }

    /**
     *
     * @param parameters
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public boolean goalSucceeded(Believes parameters) throws KernellAgentEventExceptionBESA {
        return true;
    }

}
