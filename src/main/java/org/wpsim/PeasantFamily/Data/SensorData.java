/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Data;

import rational.data.InfoData;

import java.util.Map;

/**
 *
 * @author jairo
 */
public class SensorData extends InfoData{
    
    private SensorDataType dataType;
    private String infoReceived=null;
    private Map<String, Object> dataP=null;
    private Map<String, Object> dataPE=null;
    private int ack;
    private boolean hasEmo;

    /**
     *
     */
    public SensorData() {
        super(null);
        hasEmo=false;
    }

    /**
     *
     * @return
     */
    public SensorDataType getDataType() {
        return dataType;
    }

    /**
     *
     * @param dataType
     */
    public void setDataType(SensorDataType dataType) {
        this.dataType = dataType;
    }

    /**
     *
     * @return
     */
    public Map<String, Object> getDataP() {
        return dataP;
    }

    /**
     *
     * @param dataP
     */
    public void setDataP(Map<String, Object> dataP) {
        this.dataP = dataP;
    }

    /**
     *
     * @return
     */
    public Map<String, Object> getDataPE() {
        return dataPE;
    }

    /**
     *
     * @param dataPE
     */
    public void setDataPE(Map<String, Object> dataPE) {
        this.dataPE = dataPE;
    }

    /**
     *
     * @return
     */
    public String getInfoReceived() {
        return infoReceived;
    }

    /**
     *
     * @param infoReceived
     */
    public void setInfoReceived(String infoReceived) {
        this.infoReceived = infoReceived;
    }

    /**
     *
     * @return
     */
    public int getAck() {
        return ack;
    }

    /**
     *
     * @param ack
     */
    public void setAck(int ack) {
        this.ack = ack;
    }

    /**
     *
     * @return
     */
    public boolean isHasEmo() {
        return hasEmo;
    }

    /**
     *
     * @param hasEmo
     */
    public void setHasEmo(boolean hasEmo) {
        this.hasEmo = hasEmo;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "SensorData{" + "dataType=" + dataType + ", infoReceived=" + infoReceived + ", dataP=" + dataP + ", dataPE=" + dataPE + ", ack=" + ack + ", hasEmo=" + hasEmo + '}';
    }
    
}
