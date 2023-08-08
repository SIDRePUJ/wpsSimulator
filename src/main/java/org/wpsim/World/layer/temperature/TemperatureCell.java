package org.wpsim.World.layer.temperature;

import org.wpsim.World.Automata.cell.GenericWorldLayerCell;
import org.wpsim.World.Automata.cell.LayerCellState;

/**
 * Temperature cell concrete implementation
 */
public class TemperatureCell extends GenericWorldLayerCell<LayerCellState> {


    private String id;

    /**
     *
     * @param id
     */
    public TemperatureCell(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }
}
