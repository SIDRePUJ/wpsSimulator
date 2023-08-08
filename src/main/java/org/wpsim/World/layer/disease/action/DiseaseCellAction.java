package org.wpsim.World.layer.disease.action;

import org.wpsim.World.Automata.cell.LayerCellAction;

/**
 * Implementation of an action that can be taken into account in the layer execution ej: insecticide application to crop
 */
public class DiseaseCellAction implements LayerCellAction {
    String payload;
    String date;

    /**
     *
     * @param payload
     * @param date
     */
    public DiseaseCellAction(String payload, String date) {
        this.payload = payload;
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
