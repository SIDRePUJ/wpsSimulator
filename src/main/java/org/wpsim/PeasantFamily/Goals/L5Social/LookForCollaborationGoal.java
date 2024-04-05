/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 * \ V  V / | |_) |\__ \ *    @since 2023                                  *
 * \_/\_/  | .__/ |___/ *                                                 *
 * | |          *    @author Jairo Serrano                        *
 * |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Goals.L5Social;

import BESA.BDI.AgentStructuralModel.GoalBDITypes;
import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.KernellAgentEventExceptionBESA;
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
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Emotions.EmotionalEvaluator;
import org.wpsim.WellProdSim.Base.wpsGoalBDI;
import org.wpsim.WellProdSim.wpsStart;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Tasks.L5Social.LookForCollaborationTask;
import rational.RationalRole;
import rational.mapping.Believes;
import rational.mapping.Plan;

import java.util.List;

/**
 *
 * @author jairo
 */
public class LookForCollaborationGoal extends wpsGoalBDI {

    /**
     *
     * @return
     */
    public static LookForCollaborationGoal buildGoal() {
        LookForCollaborationTask lookForCollaborationTask = new LookForCollaborationTask();
        Plan lookForCollaborationPlan = new Plan();
        lookForCollaborationPlan.addTask(lookForCollaborationTask);
        RationalRole lookForCollaborationRole = new RationalRole(
                "LookForCollaborationTask",
                lookForCollaborationPlan);
        return new LookForCollaborationGoal(
                wpsStart.getPlanID(),
                lookForCollaborationRole,
                "LookForCollaborationTask",
                GoalBDITypes.NEED);
    }

    /**
     *
     * @param id
     * @param role
     * @param description
     * @param type
     */
    public LookForCollaborationGoal(long id, RationalRole role, String description, GoalBDITypes type) {
        super(id, role, description, type);
        InputVariable socialAffinity = new InputVariable();
        socialAffinity.setName("socialAffinity");
        socialAffinity.setRange(0, 1.0);
        socialAffinity.addTerm(new Trapezoid("low", 0, 0, 0.2, 0.4));
        socialAffinity.addTerm(new Triangle("medium", 0.3, 0.5, 0.7));
        socialAffinity.addTerm(new Trapezoid("high", 0.6, 0.8, 1.0, 1.0));
        engine.addInputVariable(socialAffinity);
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

        if (this.isAlreadyExecutedToday(believes) || believes.isAskedForCollaboration()) {
            return 0;
        }

        if (believes.getAssignedLands().size() > 1 && believes.getPeasantFamilyHelper().isEmpty()) {
            for (LandInfo currentLandInfo : believes.getAssignedLands()) {
                if (!currentLandInfo.getCurrentSeason().equals(SeasonType.NONE)) {
                    if (believes.getPeasantProfile().getMoney() >
                            (wpsStart.config.getIntProperty("government.smlv") * believes.getAssignedLands().size() * 10)) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    /**
     *
     * @param stateBDI
     * @return
     * @throws KernellAgentEventExceptionBESA
     */
    @Override
    public double evaluateContribution(StateBDI stateBDI) throws KernellAgentEventExceptionBESA {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) stateBDI.getBelieves();

        if (wpsStart.config.getBooleanProperty("pfagent.emotions") && believes.isHaveEmotions()) {

            List<String> rules = wpsStart.config.getFuzzyRulesList("GoalsSocialAffinityEmotionalRules");
            RuleBlock ruleBlock = new RuleBlock();
            ruleBlock.setConjunction(new Minimum());
            ruleBlock.setDisjunction(new Maximum());
            ruleBlock.setImplication(new Minimum());
            ruleBlock.setActivation(new General());
            for (String rule : rules) {
                ruleBlock.addRule(Rule.parse(rule, engine));
            }
            engine.addRuleBlock(ruleBlock);

            EmotionalEvaluator EmotionalEvaluator = new EmotionalEvaluator("EmotionalRulesFull");

            engine.setInputValue("EmotionalState", EmotionalEvaluator.evaluate(believes.getEmotionsListCopy()));
            engine.setInputValue("socialAffinity", believes.getPeasantProfile().getSocialAffinity());
            engine.process();

            return engine.getOutputValue("Contribution");
        } else {
            return 0.6;
        }
    }
}
