package org.wpsim.PeasantFamily.Emotions;

import BESA.Emotional.EmotionAxis;
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.activation.General;
import com.fuzzylite.activation.Highest;
import com.fuzzylite.defuzzifier.*;
import com.fuzzylite.norm.s.AlgebraicSum;
import com.fuzzylite.norm.s.BoundedSum;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.norm.t.BoundedDifference;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;

import java.util.List;

public class EmotionalEvaluator {

    private Engine engine;

    public EmotionalEvaluator() {
        engine = new Engine();
        engine.setName("EmotionalEvaluator");

        // Definir variables de entrada (por ejemplo, para "Happiness/Sadness")
        InputVariable HappinessSadness = new InputVariable();
        HappinessSadness.setName("HappinessSadness");
        HappinessSadness.setRange(-1.0, 1.0);
        HappinessSadness.addTerm(new Trapezoid("Sad", -1.0, -1.0, -0.5, 0.0));
        HappinessSadness.addTerm(new Triangle("Neutral", -0.5, 0.0, 0.5));
        HappinessSadness.addTerm(new Trapezoid("Happy", 0.0, 0.5, 1.0, 1.0));
        engine.addInputVariable(HappinessSadness);

        InputVariable SecureInsecure = new InputVariable();
        SecureInsecure.setName("SecureInsecure");
        SecureInsecure.setRange(-1.0, 1.0);
        SecureInsecure.addTerm(new Trapezoid("Secure", -1.0, -1.0, -0.5, 0.0));
        SecureInsecure.addTerm(new Triangle("Neutral", -0.5, 0.0, 0.5));
        SecureInsecure.addTerm(new Trapezoid("Insecure", 0.0, 0.5, 1.0, 1.0));
        engine.addInputVariable(SecureInsecure);

        InputVariable HopefulUncertainty = new InputVariable();
        HopefulUncertainty.setName("HopefulUncertainty");
        HopefulUncertainty.setRange(-1.0, 1.0);
        HopefulUncertainty.addTerm(new Trapezoid("Hopeful", -1.0, -1.0, -0.5, 0.0));
        HopefulUncertainty.addTerm(new Triangle("Neutral", -0.5, 0.0, 0.5));
        HopefulUncertainty.addTerm(new Trapezoid("Uncertainty", 0.0, 0.5, 1.0, 1.0));
        engine.addInputVariable(HopefulUncertainty);

        InputVariable RelievedOverwhelmed = new InputVariable();
        RelievedOverwhelmed.setName("RelievedOverwhelmed");
        RelievedOverwhelmed.setRange(-1.0, 1.0);
        RelievedOverwhelmed.addTerm(new Trapezoid("Relieved", -1.0, -1.0, -0.5, 0.0));
        RelievedOverwhelmed.addTerm(new Triangle("Neutral", -0.5, 0.0, 0.5));
        RelievedOverwhelmed.addTerm(new Trapezoid("Overwhelmed", 0.0, 0.5, 1.0, 1.0));
        engine.addInputVariable(RelievedOverwhelmed);

        InputVariable FocusedDistracted = new InputVariable();
        FocusedDistracted.setName("FocusedDistracted");
        FocusedDistracted.setRange(-1.0, 1.0);
        FocusedDistracted.addTerm(new Trapezoid("Focused", -1.0, -1.0, -0.5, 0.0));
        FocusedDistracted.addTerm(new Triangle("Neutral", -0.5, 0.0, 0.5));
        FocusedDistracted.addTerm(new Trapezoid("Distracted", 0.0, 0.5, 1.0, 1.0));
        engine.addInputVariable(FocusedDistracted);

        OutputVariable emotionalState = new OutputVariable();
        emotionalState.setName("EmotionalState");
        emotionalState.setRange(0.0, 1.0);
        emotionalState.setDefaultValue(Double.NaN);
        emotionalState.addTerm(new Trapezoid("Negative", 0.0, 0.0, 0.25, 0.5));
        emotionalState.addTerm(new Triangle("Neutral", -0.5, 0.0, 0.5));
        emotionalState.addTerm(new Trapezoid("Positive", 0.5, 0.75, 1.0, 1.0));
        //emotionalState.addTerm(new Triangle("Negative", 0.0, 0.25, 0.5));
        //emotionalState.addTerm(new Triangle("Neutral", 0.25, 0.5, 0.75));
        //emotionalState.addTerm(new Triangle("Positive", 0.5, 0.75, 1.0));
        emotionalState.setDefuzzifier(new Centroid());
        //emotionalState.setAggregation(new BoundedSum());
        emotionalState.setAggregation(new AlgebraicSum());
        engine.addOutputVariable(emotionalState);

        /**
         * Conjunction (Conjunción)
         * Se utiliza para combinar múltiples condiciones dentro de una regla, actuando como un operador "Y" lógico.
         *     Minimum: Toma el valor más bajo de los grados de verdad involucrados. Es útil para casos donde todas las
         *     condiciones deben cumplirse en algún grado.
         *     Algebraic Product: Multiplica los grados de verdad. Ofrece una interacción más suave y gradual entre
         *     las condiciones.
         *     Drastic Product: Es un operador extremo que solo considera el grado de verdad más bajo si uno de los
         *     grados es 1, de lo contrario, es 0. Es menos comúnmente utilizado debido a su naturaleza extrema.
         *
         *  Disjunction (Disyunción)
         *  La disyunción se usa para representar una relación "O" lógica entre condiciones.
         *     Maximum: Selecciona el valor más alto de los grados de verdad. Es apropiado cuando cualquiera
         *     de las condiciones es suficiente para activar la regla.
         *     Algebraic Sum: Calcula una suma que no excede 1, considerando la superposición entre los grados
         *     de verdad. Es más suave que el máximo.
         *     Drastic Sum: Es el opuesto del Drastic Product y solo se usa en casos extremos.
         *
         * Implication (Implicación)
         * La implicación determina cómo se aplica el resultado de una condición a la salida.
         *     Minimum: Limita la salida al grado de verdad de la condición. Es útil para un control directo y claro.
         *     Algebraic Product: Proporciona una salida más suave, modulando la salida según el grado de verdad.
         *     Scaled: Escala la salida en función del grado de verdad. Puede ser útil para ajustar
         *     la influencia de una regla.
         *
         * Activation (Activación)
         * La activación se refiere a cómo se aplica una regla una vez que se ha evaluado su condición.
         *     General: Es una opción flexible y común que permite la activación basada en el
         *     resultado de la implicación.
         *     Proportional: Activa las reglas en proporción a su grado de verdad. Puede ser útil para asegurar
         *     que las reglas menos ciertas tengan menos influencia.
         */
        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(new Maximum());
        ruleBlock.setImplication(new Minimum());
        ruleBlock.setActivation(new General());
        //ruleBlock.addRule(Rule.parse("if HappinessSadness is Sad then EmotionalState is Negative", engine));
        //ruleBlock.addRule(Rule.parse("if HappinessSadness is Happy then EmotionalState is Positive", engine));
        //ruleBlock.addRule(Rule.parse("if SecureInsecure is Insecure then EmotionalState is Negative", engine));
        //ruleBlock.addRule(Rule.parse("if SecureInsecure is Secure then EmotionalState is Positive", engine));
        //ruleBlock.addRule(Rule.parse("if HopefulUncertainty is Uncertainty then EmotionalState is Negative", engine));
        //ruleBlock.addRule(Rule.parse("if HopefulUncertainty is Hopeful then EmotionalState is Positive", engine));
        //ruleBlock.addRule(Rule.parse("if RelievedOverwhelmed is Overwhelmed then EmotionalState is Negative", engine));
        //ruleBlock.addRule(Rule.parse("if RelievedOverwhelmed is Relieved then EmotionalState is Positive", engine));
        //ruleBlock.addRule(Rule.parse("if FocusedDistracted is Distracted then EmotionalState is Negative", engine));
        //ruleBlock.addRule(Rule.parse("if FocusedDistracted is Focused then EmotionalState is Positive", engine));
        //ruleBlock.addRule(Rule.parse("if HappinessSadness is Happy and SecureInsecure is Secure then EmotionalState is Positive", engine));

        ruleBlock.addRule(Rule.parse("if HappinessSadness is Sad and HopefulUncertainty is Uncertainty then EmotionalState is Negative", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Sad and HopefulUncertainty is Neutral then EmotionalState is Negative", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Sad and HopefulUncertainty is Hopeful then EmotionalState is Neutral", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Neutral and HopefulUncertainty is Uncertainty then EmotionalState is Negative", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Neutral and HopefulUncertainty is Neutral then EmotionalState is Neutral", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Neutral and HopefulUncertainty is Hopeful then EmotionalState is Neutral", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Happy and HopefulUncertainty is Uncertainty then EmotionalState is Neutral", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Happy and HopefulUncertainty is Neutral then EmotionalState is Neutral", engine));
        ruleBlock.addRule(Rule.parse("if HappinessSadness is Happy and HopefulUncertainty is Hopeful then EmotionalState is Positive", engine));


        engine.addRuleBlock(ruleBlock);
    }

    public double evaluate(List<EmotionAxis> emotions) {
        for (EmotionAxis emotion : emotions) {
            System.out.println(emotion.getPositiveName() + emotion.getNegativeName() + " = " + emotion.getCurrentValue());
            engine.setInputValue(emotion.getPositiveName() + emotion.getNegativeName(), emotion.getCurrentValue());
        }
        engine.process();
        return engine.getOutputValue("EmotionalState");
    }

}