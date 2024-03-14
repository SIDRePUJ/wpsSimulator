package org.wpsim.AgroEcosystem.layer.crop.cell.action;

import org.wpsim.AgroEcosystem.Automata.cell.LayerCellAction;

/**
 * Crop cell action implementation, holds the payload, action type and date for an action to be taken into account during the layer execution
 */
public class CropCellAction implements LayerCellAction {
    private CropCellActionType actionType;
    private String payload;

    private String date;

    /**
     *
     * @param actionType
     * @param payload
     * @param date
     */
    public CropCellAction(CropCellActionType actionType, String payload, String date) {
        this.actionType = actionType;
        this.payload = payload;
        this.date = date;
    }

    /**
     *
     * @return
     */
    public CropCellActionType getActionType() {
        return actionType;
    }

    /**
     *
     * @param actionType
     */
    public void setActionType(CropCellActionType actionType) {
        this.actionType = actionType;
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
}
