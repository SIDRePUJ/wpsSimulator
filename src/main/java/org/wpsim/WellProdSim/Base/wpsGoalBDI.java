package org.wpsim.WellProdSim.Base;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Util.wpsCSV;
import org.wpsim.WellProdSim.wpsStart;
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
        // @TODO: IMPLEMENTAR FUZZY LOGIC
        return 0;
    }

    /**
     * Evaluates the contribution of the emotion set to the goal.
     *
     * @param stateBDI     agent state
     * @param contribution default contribution
     * @return contribution
     * @throws KernellAgentEventExceptionBESA exception
     */
    public double evaluateEmotionalContribution(StateBDI stateBDI, double contribution) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) stateBDI.getBelieves();
        EmotionalEvaluator evaluator = new EmotionalEvaluator("Full");
        if (wpsStart.config.getBooleanProperty("control.showPyramid")) {
            System.out.println(stateBDI.getMachineBDIParams().getPyramidGoals());
            wpsCSV.log("Pyramid", "Día de simulación: " + believes.getCurrentDay());
            wpsCSV.log("Pyramid", stateBDI.getMachineBDIParams().getPyramidGoals().toString());
        }
        if (wpsStart.config.getBooleanProperty("pfagent.emotions") && believes.isHaveEmotions()) {
            return (evaluator.evaluate(believes.getEmotionsListCopy()) + contribution) / 2;
        } else {
            return contribution;
        }
    }

    /**
     * Evaluates the contribution of an emotion to the goal.
     *
     * @param stateBDI          agent state
     * @param emotionToEvaluate emotion
     * @param contribution      default contribution
     * @return contribution
     * @throws KernellAgentEventExceptionBESA exception
     */
    public double evaluateSingleEmotionContribution(StateBDI stateBDI, String emotionToEvaluate, double contribution) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) stateBDI.getBelieves();
        EmotionalEvaluator evaluator = new EmotionalEvaluator("Single");
        if (wpsStart.config.getBooleanProperty("pfagent.emotions") && believes.isHaveEmotions()) {
            return (evaluator.evaluateSingleEmotion(believes.getEmotionsListCopy(), emotionToEvaluate) + contribution) / 2;
        } else {
            return contribution;
        }

    }

    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) stateBDI.getBelieves();
        if (believes.getAlias().equals("PeasantFamily_1")) {
            wpsReport.info(stateBDI.getMachineBDIParams().getMainGoal(), believes.getAlias());
            wpsReport.info(believes.isWaiting() + " " + believes.getInternalCurrentDate() + "\n" + stateBDI.getMachineBDIParams().getPyramidGoals(), believes.getAlias());
        }
        return believes.getPeasantProfile().getHealth() > 0;
    }

    @Override
    public boolean goalSucceeded(Believes parameters) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        return believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName());
    }

    /**
     * Checks if the goal was already executed today
     *
     * @param believes Peasant Family BDI Believes
     * @return true if the goal was already executed today
     */
    public boolean isAlreadyExecutedToday(PeasantFamilyBelieves believes) {
        //System.out.println("Checking Finish for " + this.getClass().getSimpleName() + " on " + believes.getInternalCurrentDate() + " con " + believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName()));
        return believes.isTaskExecutedOnDate(
                believes.getInternalCurrentDate(),
                this.getClass().getSimpleName().replace(
                        "Goal", "Task"
                )
        );
    }

}
