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
package org.wpsim.PeasantFamily.Emotions;


import BESA.Emotional.*;

import java.nio.file.FileSystemNotFoundException;
import java.util.List;

public abstract class EmotionalComponent extends EmotionalModel {

    /**
     * Creates a new emotional component.
     */
    public EmotionalComponent() {
    }

    @Override
    public void emotionalStateChanged() {
    }

    public float getEmotionCurrentValue(String emotionName){
        for (EmotionAxis emotion: emotionalState.getEmotions()){
            if (emotion.getPositiveName().equals(emotionName) || emotion.getNegativeName().equals(emotionName)){
                return emotion.getCurrentValue();
            }
        }
        return 0;
    }

    @Override
    public void loadSemanticDictionary() {

        SemanticDictionary sd = SemanticDictionary.getInstance();
        for (EmotionalConfig.People who : EmotionalConfig.People.values()) {
            sd.addSemanticItem(Personality.EmotionElementType.Person, new SemanticValue(who.toString(), who.getValue()));
        }

        for (EmotionalConfig.Events evt : EmotionalConfig.Events.values()) {
            sd.addSemanticItem(Personality.EmotionElementType.Event, new SemanticValue(evt.toString(), evt.getValue()));
        }

        for (EmotionalConfig.Objects obj : EmotionalConfig.Objects.values()) {
            sd.addSemanticItem(Personality.EmotionElementType.Object, new SemanticValue(obj.toString(), obj.getValue()));
        }

    }

    @Override
    public void loadCharacterDescriptor() {

        for (EmotionalSubjectType who : EmotionalSubjectType.values()) {
            setPersonRelationship(who.toString(), who.getConfig());
            //System.out.println("EmotionalSubjectType: " + who.toString() + " Config: " + who.getConfig());
        }

        for (EmotionalEventType evt : EmotionalEventType.values()) {
            setEventDesirability(evt.toString(), evt.getConfig());
            //System.out.println("EmotionalEventType: " + evt.toString() + " Config: " + evt.getConfig());
        }

        for (EmotionalObjectType obj : EmotionalObjectType.values()) {
            if (!obj.equals(EmotionalObjectType.NULL)) {
                setObjectRelationship(obj.toString(), obj.getConfig());
                //System.out.println("EmotionalObjectType: " + obj.toString() + " Config: " + obj.getConfig());
            }
        }

    }

    @Override
    public void loadEmotionalAxes() {

        EmotionAxis HappinessSadness = new EmotionAxis(
                Semantics.Emotions.Happiness,
                Semantics.Emotions.Sadness,
                0.0f, 0.0f, 0.1f
        );
        HappinessSadness.setEventInfluence(EmotionalEventType.LEISURE.name(), 0.7f);
        HappinessSadness.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.3f);
        HappinessSadness.setEventInfluence(EmotionalEventType.STARVING.name(), 0.6f);
        HappinessSadness.setEventInfluence(EmotionalEventType.HELPED.name(), 0.3f);
        HappinessSadness.setEventInfluence(EmotionalEventType.THIEVING.name(), 0.6f);
        this.addEmotionAxis(HappinessSadness);

        EmotionAxis HopefulUncertainty = new EmotionAxis(
                Semantics.Emotions.Hopeful,
                Semantics.Emotions.Uncertainty,
                0.0f, 0.0f, 0.1f
        );
        HopefulUncertainty.setEventInfluence(EmotionalEventType.PLANTING.name(), 1.0f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.SELLING.name(), 0.8f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.4f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.CHECKCROPS.name(), 0.2f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.PLANTINGFAILED.name(), 0.3f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.THIEVING.name(), 1.0f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.HELPED.name(), 0.3f);
        HopefulUncertainty.setEventInfluence(EmotionalEventType.WORK.name(), 0.4f);
        this.addEmotionAxis(HopefulUncertainty);


        EmotionAxis SecureInsecure = new EmotionAxis(
                Semantics.Emotions.Secure,
                Semantics.Emotions.Insecure,
                0.0f, 0.0f, 0.1f
        );
        SecureInsecure.setEventInfluence(EmotionalEventType.HOUSEHOLDING.name(), 0.5f);
        SecureInsecure.setEventInfluence(EmotionalEventType.THIEVING.name(), 1.0f);
        SecureInsecure.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.4f);
        SecureInsecure.setEventInfluence(EmotionalEventType.PLANTING.name(), 0.2f);
        this.addEmotionAxis(SecureInsecure);

        /*EmotionAxis RelievedOverwhelmed = new EmotionAxis(
                Semantics.Emotions.Relieved,
                Semantics.Emotions.Overwhelmed,
                0.0f, 0.0f, 0.2f
        );
        RelievedOverwhelmed.setEventInfluence(EmotionalEventType.HOUSEHOLDING.name(), 0.5f);
        this.addEmotionAxis(RelievedOverwhelmed);

        EmotionAxis FocusedDistracted = new EmotionAxis(
                Semantics.Emotions.Focused,
                Semantics.Emotions.Distracted,
                0.0f, 0.0f, 0.2f
        );
        FocusedDistracted.setEventInfluence(EmotionalEventType.HOUSEHOLDING.name(), 0.5f);
        this.addEmotionAxis(FocusedDistracted);
        */

    }
}

