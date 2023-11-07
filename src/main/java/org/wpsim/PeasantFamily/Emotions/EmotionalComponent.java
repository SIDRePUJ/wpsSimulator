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
        //System.out.println("Emotional State Changed " + this.toString());
        /*try {
            System.out.println("Emotional State Changed " + this.getTopEmotionAxis().getDescription());//.getMostActivatedEmotion().getDescription());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }*/
    }

    public float getEmotionCurrentValue(String emotionName){
        for (EmotionAxis emotion: emotionalState.getEmotions()){
            //System.out.println("P " + emotion.getPositiveName() + " - N " + emotion.getNegativeName());
            if (emotion.getPositiveName().equals(emotionName) || emotion.getNegativeName().equals(emotionName)){
                return emotion.getCurrentValue();
            }
        }
        return 0;
    }

    public boolean isEnergized(){
        // @TODO: revisar relación con salud o parte física
        // @TODO: En la tabla incluir emoción, salud, fisico y dinero para las metas.
        return getEmotionCurrentValue("Energized") > 0.2f;
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

        //System.out.println("Semantic Dictionary: " + sd.toString());

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
        //EmotionAxis emoAxis;

        /*
        // Emotions related to the Health axis
        emoAxis = new EmotionAxis(
                Semantics.Sensations.Healthy,
                Semantics.Sensations.Sick,
                0.5f, 0.0f, 0.1f
        );
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.3f);
        emoAxis.setEventInfluence(EmotionalEventType.STARVING.name(), 0.6f);
        this.addEmotionAxis(emoAxis);
        */
        // Emotions related to the Energy axis
        EmotionAxis emoAxis = new EmotionAxis(
                Semantics.Sensations.Energized,
                Semantics.Sensations.Exhausted,
                0.0f, 0.0f, 0.2f
        );
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 1.0f);
        emoAxis.setEventInfluence(EmotionalEventType.STARVING.name(), 0.5f);
        emoAxis.setEventInfluence(EmotionalEventType.CHECKCROP.name(), 0.5f);
        emoAxis.setEventInfluence(EmotionalEventType.PLANTING.name(), 0.5f);
        emoAxis.setEventInfluence(EmotionalEventType.IRRIGATING.name(), 0.2f);
        emoAxis.setEventInfluence(EmotionalEventType.HARVESTING.name(), 0.5f);
        emoAxis.setEventInfluence(EmotionalEventType.SELLING.name(), 0.2f);
        this.addEmotionAxis(emoAxis);

        /*
        // Emotions related to the Happiness axis
        emoAxis = new EmotionAxis(
                Semantics.Emotions.Happiness,
                Semantics.Emotions.Sadness,
                0.0f, 0.0f, 0.01f
        );
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.2f);
        emoAxis.setEventInfluence(EmotionalEventType.THIEVING.name(), 0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.STARVING.name(), 0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.SELLING.name(), 0.2f);
        this.addEmotionAxis(emoAxis);
        // Emotions related to the Security axis
        emoAxis = new EmotionAxis(
                Semantics.Emotions.Confident,
                Semantics.Emotions.Insecure,
                0.5f, 0.0f, 0.1f
        );
        emoAxis.setEventInfluence(EmotionalEventType.IRRIGATING.name(), 0.2f);
        emoAxis.setEventInfluence(EmotionalEventType.HARVESTING.name(), 0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.SELLING.name(), 0.2f);
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.2f);
        this.addEmotionAxis(emoAxis);
        */

    }
}

