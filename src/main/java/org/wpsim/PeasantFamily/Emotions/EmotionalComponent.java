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

public abstract class EmotionalComponent extends EmotionalModel {

    /**
     * Creates a new emotional component.
     */
    public EmotionalComponent() {

/*
        // Event Rating Desires (Adjusted based on the new Event classes provided)
        this.configureEventDesire(Semantics.Events.ReceivesNews, Semantics.EventRating._4_SomewhatDesirable.getName());
        this.configureEventDesire(Semantics.Events.ObservesDirtiness, Semantics.EventRating._2_SomewhatUndesirable.getName());
        this.configureEventDesire(Semantics.Events.ObservesTheft, Semantics.EventRating._1_Undesirable.getName());
        this.configureEventDesire(Semantics.Events.ObservesDisaster, Semantics.EventRating._1_Undesirable.getName());
        this.configureEventDesire(Semantics.Events.Eats, Semantics.EventRating._5_Desirable.getName());
        this.configureEventDesire(Semantics.Events.ReceivesOffer, Semantics.EventRating._5_Desirable.getName());
        this.configureEventDesire(Semantics.Events.ObservesSell, Semantics.EventRating._5_Desirable.getName());

        // Object Relationships (Adjusted based on the new Object classes provided)
        this.configureObjectRelationship(Semantics.Objects.HarvestedProduce, Semantics.ObjectRating._5_Important.getName());
        this.configureObjectRelationship(Semantics.Objects.Warehouse, Semantics.ObjectRating._4_Valuable.getName());
        this.configureObjectRelationship(Semantics.Objects.HospitalFacility, Semantics.ObjectRating._5_Important.getName());
        this.configureObjectRelationship(Semantics.Objects.Food, Semantics.ObjectRating._5_Important.getName());
        this.configureObjectRelationship(Semantics.Objects.WaterResource, Semantics.ObjectRating._5_Important.getName());
        this.configureObjectRelationship(Semantics.Objects.Crop, Semantics.ObjectRating._5_Important.getName());
        this.configureObjectRelationship(Semantics.Objects.FertilizerResource, Semantics.ObjectRating._4_Valuable.getName());
        ;
        this.configureObjectRelationship(Semantics.Objects.Housing, Semantics.ObjectRating._5_Important.getName());
        this.configureObjectRelationship(Semantics.Objects.Tools, Semantics.ObjectRating._4_Valuable.getName());

        // Person Relationships (Adjusted based on the new People classes provided)
        this.configurePersonRelationship(Semantics.People.Buyer, Semantics.PeopleRating._4_Friend.getName());
        this.configurePersonRelationship(Semantics.People.Neighbor, Semantics.PeopleRating._4_Friend.getName());
        this.configurePersonRelationship(Semantics.People.Seller, Semantics.PeopleRating._3_Unknown.getName());
        this.configurePersonRelationship(Semantics.People.PeasantFamilies, Semantics.PeopleRating._5_Close.getName());
        this.configurePersonRelationship(Semantics.People.Thief, Semantics.PeopleRating._2_NotFriendly.getName());
        this.configurePersonRelationship(Semantics.People.Landowner, Semantics.PeopleRating._3_Unknown.getName());
        this.configurePersonRelationship(Semantics.People.MarketVendor, Semantics.PeopleRating._3_Unknown.getName());
        this.configurePersonRelationship(Semantics.People.BankOfficer, Semantics.PeopleRating._3_Unknown.getName());
*/

    }

    @Override
    public void emotionalStateChanged() {
        /*try {
            HashMap<String, Object> infoServicio = new HashMap<>();
            EmotionAxis ea = getTopEmotionAxis();

            float state = ea.getCurrentValue();
            if (state > 0 && valencia != 1) {
                valencia = 1;
                tiempoEmocionPredominante = System.currentTimeMillis();
            } else if (state < 0 && valencia != -1) {
                valencia = -1;
                tiempoEmocionPredominante = System.currentTimeMillis();
            }
            leds = PepperEmotionRanges.getFromEmotionalValue(state);
            infoServicio.put("velocidad", normalizeValue(state, PepperConf.SPEED));
            infoServicio.put("tonoHabla", normalizeValue(state, PepperConf.PITCH));
            infoServicio.put("ledIntens", normalizeValue(state, PepperConf.LEDINTENSITY));
            infoServicio.put("DURATION", normalizeValue(state, PepperConf.DURATION));
            infoServicio.put("COLOR", leds.getHexa());
            System.out.println("AfueraStoryMOde" + isStoryMode());

            if (!storyMode) {
                System.out.println("StoryMOde" + isStoryMode());
                infoServicio.put("EmotionalTag", leds.toString());
            }
            if (storyMode) {
                infoServicio.put("velHabla", normalizeValue(state - 0.3f, PepperConf.TALKSPEED));
            } else {
                infoServicio.put("velHabla", normalizeValue(state, PepperConf.TALKSPEED));
            }
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Valores Emocionales para: " + ea.getNegativeName());
            System.out.println("Valores Emocionales para: " + state);
            System.out.println("EmotionalTag: " + leds.toString());
            System.out.println("Velocidad " + normalizeValue(state, PepperConf.SPEED));
            System.out.println("velHabla " + normalizeValue(state, PepperConf.TALKSPEED));
            System.out.println("tonoHabla " + normalizeValue(state, PepperConf.PITCH));
            System.out.println("ledIntens " + normalizeValue(state, PepperConf.LEDINTENSITY));
            System.out.println("DURATION " + normalizeValue(state, PepperConf.DURATION));
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            ServiceDataRequest srb = ServiceRequestBuilder.buildRequest(RobotStateServiceRequestType.ROBOTEMOTION, infoServicio);
            ResPwaUtils.requestService(srb);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(BEstadoRobot.class.getName()).log(Level.SEVERE, null, ex);
        }*/

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
        }

        for (EmotionalEventType evt : EmotionalEventType.values()) {
            setEventDesirability(evt.toString(), evt.getConfig());
        }

        for (EmotionalObjectType obj : EmotionalObjectType.values()) {
            if (!obj.equals(EmotionalObjectType.NULL)) {
                setObjectRelationship(obj.toString(), obj.getConfig());
            }
        }

    }

    @Override
    public void loadEmotionalAxes() {
        EmotionAxis emoAxis;

        // Emotions related to the Health axis
        emoAxis = new EmotionAxis(
                Semantics.Sensations.Healthy,
                Semantics.Sensations.Sick,
                0.5f, 0.0f, 0.1f
        );
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.STARVING.name(), 0.6f);
        this.addEmotionAxis(emoAxis);

        // Emotions related to the Energy axis
        emoAxis = new EmotionAxis(
                Semantics.Sensations.Energized,
                Semantics.Sensations.Exhausted,
                0.5f, 0.0f, 0.1f
        );
        emoAxis.setEventInfluence(EmotionalEventType.PLANTING.name(), -0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.IRRIGATING.name(), -0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.HARVESTING.name(), -0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.SELLING.name(), -0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.1f);
        this.addEmotionAxis(emoAxis);

        // Emotions related to the Happiness axis
        emoAxis = new EmotionAxis(
                Semantics.Emotions.Happiness,
                Semantics.Emotions.Sadness,
                0.0f, 0.0f, 0.01f
        );
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.THIEVING.name(), -0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.STARVING.name(), -0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.SELLING.name(), 0.1f);
        this.addEmotionAxis(emoAxis);

        // Emotions related to the Security axis
        emoAxis = new EmotionAxis(
                Semantics.Emotions.Confident,
                Semantics.Emotions.Insecure,
                0.5f, 0.0f, 0.1f
        );
        emoAxis.setEventInfluence(EmotionalEventType.PLANTING.name(), -0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.IRRIGATING.name(), -0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.HARVESTING.name(), 0.4f);
        emoAxis.setEventInfluence(EmotionalEventType.SELLING.name(), -0.1f);
        emoAxis.setEventInfluence(EmotionalEventType.DOVITALS.name(), 0.1f);
        this.addEmotionAxis(emoAxis);

    }
}

