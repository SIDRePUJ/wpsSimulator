package org.wpsim.AgroEcosystem.layer.shortWaveRadiation;

import org.wpsim.AgroEcosystem.Automata.cell.GenericWorldLayerCell;

/**
 * Concrete cell implementation
 */
public class ShortWaveRadiationCell extends GenericWorldLayerCell<ShortWaveRadiationCellState> {

    private String id;

    /**
     *
     * @param id
     */
    public ShortWaveRadiationCell(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

}
