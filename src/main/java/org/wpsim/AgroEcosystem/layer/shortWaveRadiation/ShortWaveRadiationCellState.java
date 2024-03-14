package org.wpsim.AgroEcosystem.layer.shortWaveRadiation;

import org.wpsim.AgroEcosystem.Automata.cell.LayerCellState;

/**
 * Concrete implementation of the short wave radiation cell state
 */
public class ShortWaveRadiationCellState implements LayerCellState {

    private double shortWaveRadiation;

    /**
     *
     * @param shortWaveRadiation
     */
    public ShortWaveRadiationCellState(double shortWaveRadiation) {
        //wpsReport.info("New short wave radiation state: " + shortWaveRadiation);
        this.shortWaveRadiation = shortWaveRadiation;
    }

    /**
     *
     */
    public ShortWaveRadiationCellState() {
    }

    /**
     *
     * @return
     */
    public double getShortWaveRadiation() {
        return shortWaveRadiation;
    }

    /**
     *
     * @param shortWaveRadiation
     */
    public void setShortWaveRadiation(double shortWaveRadiation) {
        this.shortWaveRadiation = shortWaveRadiation;
    }
}
