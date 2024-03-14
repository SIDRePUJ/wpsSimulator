package org.wpsim.AgroEcosystem.Messages;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class AgroEcosystemMessage extends DataBESA {
    private AgroEcosystemMessageType agroEcosystemMessageType;
    private String cropId;
    private String peasantAgentAlias;
    private String payload;
    private String date;

    /**
     *
     * @param agroEcosystemMessageType
     * @param cropId
     * @param date
     * @param peasantAgentId
     */
    public AgroEcosystemMessage(AgroEcosystemMessageType agroEcosystemMessageType, String cropId, String date, String peasantAgentId) {
        this.agroEcosystemMessageType = agroEcosystemMessageType;
        this.cropId = cropId;
        this.date = date;
        this.peasantAgentAlias = peasantAgentId;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "---> WorldMessage{" + "worldMessageType=" + agroEcosystemMessageType + ", cropId=" + cropId + ", peasantAgentId=" + peasantAgentAlias + ", payload=" + payload + ", date=" + date + "}";
    }

    /**
     *
     * @return
     */
    public String getPeasantAgentAlias() {
        return peasantAgentAlias;
    }

    /**
     *
     * @param peasantAgentAlias
     */
    public void setPeasantAgentAlias(String peasantAgentAlias) {
        this.peasantAgentAlias = peasantAgentAlias;
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
    public AgroEcosystemMessageType getWorldMessageType() {
        return agroEcosystemMessageType;
    }

    /**
     *
     * @param agroEcosystemMessageType
     */
    public void setWorldMessageType(AgroEcosystemMessageType agroEcosystemMessageType) {
        this.agroEcosystemMessageType = agroEcosystemMessageType;
    }

    /**
     *
     * @return
     */
    public String getCropId() {
        return cropId;
    }

    /**
     *
     * @param cropId
     */
    public void setCropId(String cropId) {
        this.cropId = cropId;
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
}
