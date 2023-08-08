package org.wpsim.PeasantFamily.Guards.FromWorld;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class FromWorldMessage extends DataBESA {
    private String peasantAlias;
    private String payload;
    private FromWorldMessageType peasantMessageType;
    private String date;
    private String simpleMessage;

    /**
     *
     * @param peasantMessageType
     * @param peasantAlias
     * @param payload
     */
    public FromWorldMessage(FromWorldMessageType peasantMessageType, String peasantAlias, String payload) {
        this.peasantAlias = peasantAlias;
        this.payload = payload;
        this.peasantMessageType = peasantMessageType;
    }

    /**
     *
     * @param simpleMessage
     */
    public FromWorldMessage(String simpleMessage) {
        this.simpleMessage = simpleMessage;
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
    public FromWorldMessageType getMessageType() {
        return peasantMessageType;
    }

    /**
     *
     * @param peasantMessageType
     */
    public void setMessageType(FromWorldMessageType peasantMessageType) {
        this.peasantMessageType = peasantMessageType;
    }
}
