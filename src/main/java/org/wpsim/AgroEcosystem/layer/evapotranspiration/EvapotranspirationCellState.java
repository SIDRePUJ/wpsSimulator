package org.wpsim.AgroEcosystem.layer.evapotranspiration;

import org.wpsim.AgroEcosystem.Automata.cell.LayerCellState;

/**
 * Concrete implementation of the evapotranspiration cell state
 */
public class EvapotranspirationCellState implements LayerCellState {

    private double evapotranspirationReference;

    /**
     *
     * @param evapotranspirationReference
     */
    public EvapotranspirationCellState(double evapotranspirationReference) {
        //wpsReport.info("Next evapotranspiration state: " + evapotranspirationReference);
        this.evapotranspirationReference = evapotranspirationReference;
    }

    /**
     *
     */
    public EvapotranspirationCellState() {
    }

    /**
     *
     * @return
     */
    public double getEvapotranspirationReference() {
        return evapotranspirationReference;
    }

    /**
     *
     * @param evapotranspirationReference
     */
    public void setEvapotranspirationReference(double evapotranspirationReference) {
        this.evapotranspirationReference = evapotranspirationReference;
    }

}
