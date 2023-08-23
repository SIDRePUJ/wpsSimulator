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


import BESA.Emotional.Semantics;
import BESA.Emotional.EmotionAxis;
import BESA.Emotional.EmotionalActor;

public class EmotionalComponent extends EmotionalActor {

    /**
     * Creates a new emotional component.
     */
    public EmotionalComponent() {

        // Emotions
        this.addEmotionalAxis(
                Semantics.Emotions.Happiness,
                Semantics.Emotions.Sadness,
                0.0f, 0.0f, 0.05f
        );
        this.addEmotionalAxis(
                Semantics.Emotions.Confident,
                Semantics.Emotions.Insecure,
                0.0f, 0.0f, 0.05f
        );

        // Sensations
        this.addEmotionalAxis(
                Semantics.Sensations.NoHunger,
                Semantics.Sensations.Hunger,
                0.5f, 0.0f, 0.1f
        );
        this.addEmotionalAxis(
                Semantics.Sensations.Energized,
                Semantics.Sensations.Exhausted,
                0.5f, 0.0f, 0.1f
        );
        this.addEmotionalAxis(
                Semantics.Sensations.Healthy,
                Semantics.Sensations.Sick,
                0.5f, 0.0f, 0.1f
        );

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


    }


    /**
     * Adds a new emotional axis to the system.
     *
     * @param  positivePole     the positive pole of the emotional axis
     * @param  negativePole     the negative pole of the emotional axis
     * @param  currentValue    the current value of the emotional axis
     * @param  baseline         the baseline value of the emotional axis
     * @param  attenuationFactor the attenuation factor of the emotional axis
     */
    public void addEmotionalAxis(String positivePole, String negativePole, float currentValue, float baseline, float attenuationFactor) {
        EmotionAxis emotionAxis = new EmotionAxis(positivePole, negativePole, currentValue, baseline, attenuationFactor);
        this.addEmotionAxis(emotionAxis);
    }

    /**
     * Configures the influence of an event on the emotion axis.
     *
     * @param  positivePole     the positive pole of the emotion axis
     * @param  negativePole     the negative pole of the emotion axis
     * @param  event            the event for which to configure the influence
     * @param  influenceFactor  the factor that determines the influence of the event on the emotion axis
     */
    public void configureEventInfluence(String positivePole, String negativePole, String event, float influenceFactor) {
        EmotionAxis emotionAxis = this.getEmotionAxis(positivePole, negativePole);
        if (emotionAxis != null) {
            emotionAxis.setEventInfluence(event, influenceFactor);
        }
    }

    /**
     * Configure the desire for a specific event.
     *
     * @param  event     the event to configure the desire for
     * @param  valuation the valuation of the event desire
     */
    public void configureEventDesire(String event, String valuation) {
        this.setEventDesirability(event, valuation);
    }

    /**
     * Configures the object relationship.
     *
     * @param  object     the object to configure the relationship for
     * @param  valuation  the valuation of the relationship
     */
    public void configureObjectRelationship(String object, String valuation) {
        this.setObjectRelationship(object, valuation);
    }

    /**
     * Configure the person's relationship with a peasant family.
     *
     * @param  peasantFamily  the name of the peasant family
     * @param  valuation      the valuation of the relationship
     */
    public void configurePersonRelationship(String peasantFamily, String valuation) {
        this.setPersonRelationship(peasantFamily, valuation);
    }
}

