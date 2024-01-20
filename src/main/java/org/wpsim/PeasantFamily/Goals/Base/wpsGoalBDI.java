package org.wpsim.PeasantFamily.Goals.Base;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;
import org.wpsim.Simulator.wpsStart;
import rational.RationalRole;
import rational.mapping.Believes;

public class wpsGoalBDI extends GoalBDI {

    public wpsGoalBDI(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
    }

    @Override
    public double evaluateViability(Believes believes) throws KernellAgentEventExceptionBESA {
        return 1;
    }

    @Override
    public double detectGoal(Believes believes) throws KernellAgentEventExceptionBESA {
        return 0;
    }

    @Override
    public double evaluatePlausibility(Believes believes) throws KernellAgentEventExceptionBESA {
        return 1;
    }

    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return 0;
    }

    /**
     * Evaluates the contribution of the emotion set to the goal.
     * @param stateBDI agent state
     * @param contribution default contribution
     * @return contribution
     * @throws KernellAgentEventExceptionBESA exception
     */
    public double evaluateEmotionalContribution(StateBDI stateBDI, double contribution) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) stateBDI.getBelieves();
        EmotionalEvaluator evaluator = new EmotionalEvaluator("Full");
        if (wpsStart.EMOTIONS && believes.isHaveEmotions()) {
            return (evaluator.evaluate(believes.getEmotionsListCopy()) + contribution) / 2;
        }else{
            return contribution;
        }
    }

    /**
     * Evaluates the contribution of an emotion to the goal.
     * @param stateBDI agent state
     * @param emotionToEvaluate emotion
     * @param contribution default contribution
     * @return contribution
     * @throws KernellAgentEventExceptionBESA exception
     */
    public double evaluateSingleEmotionContribution(StateBDI stateBDI, String emotionToEvaluate, double contribution) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) stateBDI.getBelieves();
        EmotionalEvaluator evaluator = new EmotionalEvaluator("Single");
        if (wpsStart.EMOTIONS && believes.isHaveEmotions()) {
            return (evaluator.evaluateSingleEmotion(believes.getEmotionsListCopy(), emotionToEvaluate) + contribution) / 2;
        }else{
            return contribution;
        }

    }

    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) stateBDI.getBelieves();
        //System.out.println(stateBDI.getMachineBDIParams().getPyramidGoals());
        return believes.getPeasantProfile().getHealth() > 0;
    }

    @Override
    public boolean goalSucceeded(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName());
    }

    /**
     * Checks if the goal was already executed today
     *
     * @param believes Peasant Family BDI Believes
     * @return true if the goal was already executed today
     */
    public boolean isAlreadyExecutedToday(PeasantFamilyBDIAgentBelieves believes) {
        //System.out.println("Checking Finish for " + this.getClass().getSimpleName() + " on " + believes.getInternalCurrentDate() + " con " + believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName()));
        return believes.isTaskExecutedOnDate(
                believes.getInternalCurrentDate(),
                this.getClass().getSimpleName().replace(
                        "Goal", "Task"
                )
        );
    }

}
