package org.wpsim.WellProdSim.Base;

import BESA.BDI.AgentStructuralModel.GoalBDI;
import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
import com.fuzzylite.Engine;
import com.fuzzylite.activation.General;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Util.wpsCSV;
import org.wpsim.WellProdSim.wpsStart;
import rational.RationalRole;
import rational.mapping.Believes;

import java.util.List;

public class wpsGoalBDI extends GoalBDI {

    protected Engine engine;

    public wpsGoalBDI(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
        engine = new Engine();
        engine.setName("GoalEvaluator");

        InputVariable EmotionalState = new InputVariable();
        EmotionalState.setName("EmotionalState");
        EmotionalState.setRange(0, 1.0);
        EmotionalState.addTerm(new Trapezoid("low", 0, 0, 0.2, 0.4));
        EmotionalState.addTerm(new Triangle("medium", 0.3, 0.5, 0.7));
        EmotionalState.addTerm(new Trapezoid("high", 0.6, 0.8, 1.0, 1.0));
        engine.addInputVariable(EmotionalState);

        OutputVariable Contribution = new OutputVariable();
        Contribution.setName("Contribution");
        Contribution.setRange(0.0, 1.0);
        Contribution.setDefaultValue(Double.NaN);
        Contribution.addTerm(new Trapezoid("low", 0.0, 0.0, 0.2, 0.4));
        Contribution.addTerm(new Triangle("medium", 0.3, 0.5, 0.7));
        Contribution.addTerm(new Trapezoid("high", 0.6, 0.8, 1.0, 1.0));
        Contribution.setDefuzzifier(new Centroid());
        Contribution.setAggregation(new Maximum());
        engine.addOutputVariable(Contribution);

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
        EmotionalEvaluator evaluator = new EmotionalEvaluator("EmotionalRulesFull");
        if (believes.isHaveEmotions()) {
            return (evaluator.evaluate(believes.getEmotionsListCopy()) + contribution) / 2;
        } else {
            return contribution;
        }
    }
    public double evaluateInvertedEmotionalContribution(StateBDI stateBDI, double contribution) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) stateBDI.getBelieves();
        EmotionalEvaluator evaluator = new EmotionalEvaluator("EmotionalRulesFull");
        if (wpsStart.config.getBooleanProperty("control.showPyramid")) {
            wpsCSV.log("Pyramid", "Día de simulación: " + believes.getCurrentDay());
            wpsCSV.log("Pyramid", stateBDI.getMachineBDIParams().getPyramidGoals().toString());
            wpsReport.info("\nDía de simulación: " + believes.getCurrentDay() + "\n " +
                    "getMainGoal " + stateBDI.getMachineBDIParams().getMainGoal() + "\n " +
                    "getIntention " + stateBDI.getMachineBDIParams().getIntention() + "\n " +
                    "getPotencialGoals " + stateBDI.getMachineBDIParams().getPotencialGoals().toString() + "\n " +
                    stateBDI.getMachineBDIParams().getPyramidGoals().toString(), believes.getAlias()
            );
        }
        if (believes.isHaveEmotions()) {
            return 1 - ((evaluator.evaluate(believes.getEmotionsListCopy()) + contribution) / 2);
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
        EmotionalEvaluator evaluator = new EmotionalEvaluator("EmotionalRules");
        if (believes.isHaveEmotions()) {
            return (evaluator.evaluateSingleEmotion(believes.getEmotionsListCopy(), emotionToEvaluate) + contribution) / 2;
        } else {
            return contribution;
        }

    }

    @Override
    public boolean predictResultUnlegality(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        return true;
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
        return believes.isTaskExecutedOnDate(
                believes.getInternalCurrentDate(),
                this.getClass().getSimpleName().replace(
                        "Goal", "Task"
                )
        );
    }

}
