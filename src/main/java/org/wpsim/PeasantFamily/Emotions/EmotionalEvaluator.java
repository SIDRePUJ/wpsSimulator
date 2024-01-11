package org.wpsim.PeasantFamily.Emotions;

import BESA.Emotional.EmotionAxis;
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.activation.General;
import com.fuzzylite.activation.Highest;
import com.fuzzylite.defuzzifier.*;
import com.fuzzylite.norm.s.*;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.norm.t.BoundedDifference;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import org.wpsim.Simulator.wpsStart;

import java.util.ArrayList;
import java.util.List;

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

public class EmotionalEvaluator {

    private Engine engine;

    public EmotionalEvaluator(String mode) {
        engine = new Engine();
        engine.setName("EmotionalEvaluator");

        // Definir variables de entrada (por ejemplo, para "Happiness/Sadness")
        InputVariable HappinessSadness = new InputVariable();
        HappinessSadness.setName("HappinessSadness");
        HappinessSadness.setRange(-1.0, 1.0);
        HappinessSadness.addTerm(new Trapezoid("Sad", -1.0, -1.0, -0.6, 0.2));
        HappinessSadness.addTerm(new Triangle("Neutral", -0.2, 0.0, 0.2));
        HappinessSadness.addTerm(new Trapezoid("Happy", 0.2, 0.4, 1.0, 1.0));
        engine.addInputVariable(HappinessSadness);

        InputVariable HopefulUncertainty = new InputVariable();
        HopefulUncertainty.setName("HopefulUncertainty");
        HopefulUncertainty.setRange(-1.0, 1.0);
        HopefulUncertainty.addTerm(new Trapezoid("Uncertainty", -1.0, -1.0, -0.6, 0.2));
        HopefulUncertainty.addTerm(new Triangle("Neutral", -0.2, 0.0, 0.2));
        HopefulUncertainty.addTerm(new Trapezoid("Hopeful", 0.2, 0.4, 1.0, 1.0));
        engine.addInputVariable(HopefulUncertainty);

        InputVariable SecureInsecure = new InputVariable();
        SecureInsecure.setName("SecureInsecure");
        SecureInsecure.setRange(-1.0, 1.0);
        SecureInsecure.addTerm(new Trapezoid("Insecure", -1.0, -1.0, -0.6, 0.2));
        SecureInsecure.addTerm(new Triangle("Neutral", -0.2, 0.0, 0.2));
        SecureInsecure.addTerm(new Trapezoid("Secure", 0.2, 0.4, 1.0, 1.0));
        engine.addInputVariable(SecureInsecure);

        /*InputVariable RelievedOverwhelmed = new InputVariable();
        RelievedOverwhelmed.setName("RelievedOverwhelmed");
        RelievedOverwhelmed.setRange(-1.0, 1.0);
        RelievedOverwhelmed.addTerm(new Trapezoid("Overwhelmed", -1.0, -1.0, -0.6, 0.2));
        RelievedOverwhelmed.addTerm(new Triangle("Neutral", -0.2, 0.0, 0.2));
        RelievedOverwhelmed.addTerm(new Trapezoid("Relieved", 0.2, 0.4, 1.0, 1.0));
        engine.addInputVariable(RelievedOverwhelmed);

        InputVariable FocusedDistracted = new InputVariable();
        FocusedDistracted.setName("FocusedDistracted");
        FocusedDistracted.setRange(-1.0, 1.0);
        FocusedDistracted.addTerm(new Trapezoid("Distracted", -1.0, -1.0, -0.5, 0.0));
        FocusedDistracted.addTerm(new Triangle("Neutral", -0.2, 0.0, 0.2));
        FocusedDistracted.addTerm(new Trapezoid("Focused", 0.2, 0.4, 1.0, 1.0));
        engine.addInputVariable(FocusedDistracted);*/

        OutputVariable emotionalState = new OutputVariable();
        emotionalState.setName("EmotionalState");
        emotionalState.setRange(0.0, 1.0);
        emotionalState.setDefaultValue(Double.NaN);
        emotionalState.addTerm(new Trapezoid("Negative", 0.0, 0.0, 0.2, 0.4));
        emotionalState.addTerm(new Triangle("Neutral", 0.3, 0.5, 0.7));
        emotionalState.addTerm(new Trapezoid("Positive", 0.6, 0.8, 1.0, 1.0));
        emotionalState.setDefuzzifier(new Centroid());
        emotionalState.setAggregation(new Maximum());
        engine.addOutputVariable(emotionalState);

        // 243 Combinaciones posibles
        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.setConjunction(new Minimum());
        ruleBlock.setDisjunction(new Maximum());
        ruleBlock.setImplication(new Minimum());
        ruleBlock.setActivation(new General());

        List<String> rules = wpsStart.config.getFuzzyRulesList(mode);

        for (String rule : rules) {
            ruleBlock.addRule(Rule.parse(rule, engine));
        }

        engine.addRuleBlock(ruleBlock);
    }

    public double evaluate(List<EmotionAxis> emotions) {
        for (EmotionAxis emotion : emotions) {
            //System.out.println(emotion.getPositiveName() + emotion.getNegativeName() + " = " + emotion.getCurrentValue());
            engine.setInputValue(emotion.getPositiveName() + emotion.getNegativeName(), emotion.getCurrentValue());
        }
        engine.process();
        //System.out.println("EmotionalState: " + engine.getOutputValue("EmotionalState"));
        return engine.getOutputValue("EmotionalState");
    }

    public double evaluateSingleEmotion(List<EmotionAxis> emotions, String emotionToEvaluate) {
        for (EmotionAxis emotion : emotions) {
            if (emotion.getPositiveName().equals(emotionToEvaluate)) {
                //System.out.println(emotion.getPositiveName() + emotion.getNegativeName() + " = " + emotion.getCurrentValue());
                engine.setInputValue(emotion.getPositiveName() + emotion.getNegativeName(), emotion.getCurrentValue());
            }
        }
        engine.process();
        //System.out.println("EmotionalState: " + engine.getOutputValue("EmotionalState"));
        return engine.getOutputValue("EmotionalState");
    }

    public double emotionalFactor(List<EmotionAxis> emotions, String emotionToEvaluate){
        double internalFactor = evaluateSingleEmotion(emotions, emotionToEvaluate);
        if (internalFactor >= 0.5){
            return 1.1;
        }else if(internalFactor > 0.4){
            return 1.0;
        }else{
            return 0.90;
        }
    }

}
