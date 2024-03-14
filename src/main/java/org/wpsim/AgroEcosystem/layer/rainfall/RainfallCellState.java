package org.wpsim.AgroEcosystem.layer.rainfall;

import org.wpsim.AgroEcosystem.Automata.cell.LayerCellState;

/**
 * Concrete implementation of the rainfall cell state
 */
public class RainfallCellState implements LayerCellState {

    private double rainfall;

    /**
     *
     * @param rainfall
     */
    public RainfallCellState(double rainfall) {
        //wpsReport.info("New rainfall state: " + rainfall);
        this.rainfall = rainfall;
    }

    /**
     *
     * @return
     */
    public double getRainfall() {
        return rainfall;
    }

    /**
     *
     * @param rainfall
     */
    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
    }
}
