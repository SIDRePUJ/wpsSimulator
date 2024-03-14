package org.wpsim.AgroEcosystem.layer;

import org.wpsim.AgroEcosystem.Automata.layer.LayerExecutionParams;

/**
 * Pojo implementation that holds the necessary parameters for the layer executions
 */
public class LayerFunctionParams implements LayerExecutionParams {
    private String date;

    /**
     *
     * @param date
     */
    public LayerFunctionParams(String date) {
        this.date = date;
    }

    /**
     *
     */
    public LayerFunctionParams() {
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
