package org.wpsim.PeasantFamily.Guards.FromAgroEcosystem;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class FromAgroEcosystemMessage extends DataBESA {
    private String peasantAlias;
    private String payload;
    private FromAgroEcosystemMessageType peasantMessageType;
    private String date;
    private String simpleMessage;
    private String landName;

    /**
     *
     * @param peasantMessageType
     * @param peasantAlias
     * @param payload
     */
    public FromAgroEcosystemMessage(FromAgroEcosystemMessageType peasantMessageType, String peasantAlias, String payload, String landName) {
        setPeasantAlias(peasantAlias);
        setPayload(payload);
        this.peasantMessageType = peasantMessageType;
        setLandName(landName);
    }

    /**
     *
     * @param simpleMessage
     */
    public FromAgroEcosystemMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
    }

    public String getLandName() {
        return landName;
    }

    public void setLandName(String landName) {
        this.landName = landName;
    }

    /**
     *
     * @return
     */
    public String getSimpleMessage() {
        return simpleMessage;
    }

    /**
     *
     * @return
     */
    public String getPeasantAlias() {
        return peasantAlias;
    }

    /**
     *
     * @param peasantAlias
     */
    public void setPeasantAlias(String peasantAlias) {
        this.peasantAlias = peasantAlias;
    }

    /**
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     */
    public String getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     *
     * @return
     */
    public FromAgroEcosystemMessageType getMessageType() {
        return peasantMessageType;
    }

    /**
     *
     * @param peasantMessageType
     */
    public void setMessageType(FromAgroEcosystemMessageType peasantMessageType) {
        this.peasantMessageType = peasantMessageType;
    }
}
