package org.wpsim.PeasantFamily.Goals.Base;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import rational.RationalRole;
import rational.mapping.Believes;

public class wpsGoalBDI extends GoalBDI {

    public wpsGoalBDI(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
    }

    @Override
    public double evaluateViability(Believes believes) throws KernellAgentEventExceptionBESA {
        return 0;
    }

    @Override
    public double detectGoal(Believes believes) throws KernellAgentEventExceptionBESA {
        return 0;
    }

    @Override
    public double evaluatePlausibility(Believes believes) throws KernellAgentEventExceptionBESA {
        return 0;
    }

    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return 0;
    }

    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return true;
    }

    @Override
    public boolean goalSucceeded(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName());
    }

    /**
     * Checks if the goal was already executed today
     * @param believes Peasant Family BDI Believes
     * @return true if the goal was already executed today
     */
    public boolean isAlreadyExecutedToday(PeasantFamilyBDIAgentBelieves believes) {
        //System.out.println("Checking Finish for " + this.getClass().getSimpleName() + " on " + believes.getInternalCurrentDate() + " con " + believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName()));
        return believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName().replace("Goal", "Task"));
    }

}
