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
import org.wpsim.Government.LandInfo;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.L3Development.IrrigateCropsTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

/**
 *
 * @author jairo
 */
public class IrrigateCropsGoal extends GoalBDI {

    /**
     *
     * @return
     */
    public static IrrigateCropsGoal buildGoal() {
        IrrigateCropsTask irrigateCropsTask = new IrrigateCropsTask();
        Plan irrigateCropsPlan = new Plan();
        irrigateCropsPlan.addTask(irrigateCropsTask);
        RationalRole irrigateCropsRole = new RationalRole(
                "IrrigateCropsTask",
                irrigateCropsPlan);
        IrrigateCropsGoal irrigateCropsGoalBDI = new IrrigateCropsGoal(
                wpsStart.getPlanID(),
                irrigateCropsRole,
                "IrrigateCropsTask",
                GoalBDITypes.OPORTUNITY);
        return irrigateCropsGoalBDI;
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public IrrigateCropsGoal(long id, RationalRole role, String description, GoalBDITypes type) {
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
        //wpsReport.info("viable");
        return 1;
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
        for (LandInfo currentLandInfo : believes.getAssignedLands()) {
            if (currentLandInfo.getCurrentCropCareType().equals(CropCareType.IRRIGATION)) {
                int waterUsed = (believes.getPeasantProfile().getCropSize()/1000) * 30;
                for (LandInfo currentLandInfoWater : believes.getAssignedLands()) {
                    if (currentLandInfoWater.getKind().equals("water") ||
                            believes.getPeasantProfile().getWaterAvailable() >= waterUsed) {
                        return 1;
                    }
                }
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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        if (believes.haveTimeAvailable(TimeConsumedBy.IrrigateCropsTask)) {
            //wpsReport.info("Si");
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
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) stateBDI.getBelieves();
        //wpsReport.info("legal " + believes.getPeasantProfile().getHealth());
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
        return true;
    }

}
