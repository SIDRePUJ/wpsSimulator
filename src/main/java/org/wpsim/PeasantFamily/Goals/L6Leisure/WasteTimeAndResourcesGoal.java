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
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.Simulator.Base.wpsGoalBDI;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Tasks.L6Leisure.WasteTimeAndResourcesTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class WasteTimeAndResourcesGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static WasteTimeAndResourcesGoal buildGoal() {
        WasteTimeAndResourcesTask wasteTimeAndResourcesTask = new WasteTimeAndResourcesTask();
        Plan wasteTimeAndResourcesPlan = new Plan();
        wasteTimeAndResourcesPlan.addTask(wasteTimeAndResourcesTask);
        RationalRole wasteTimeAndResourcesRole = new RationalRole(
                "WasteTimeAndResourcesTask",
                wasteTimeAndResourcesPlan);
        return new WasteTimeAndResourcesGoal(
                wpsStart.getPlanID(),
                wasteTimeAndResourcesRole,
                "WasteTimeAndResourcesTask",
                GoalBDITypes.ATTENTION_CYCLE);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public WasteTimeAndResourcesGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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

        if (this.isAlreadyExecutedToday(believes)) {
            return 0;
        }
        // @TODO: Si tengo bastante dinero, se activa proporcionalmente hacer fiesta
        // @TODO: el rendimiento sería menor
        // @TODO: Si tiene mucho dinero, puede gastar más.
        return 1;
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
        return evaluateEmotionalContribution(stateBDI, 0.1);
    }

}
