package org.wpsim.World.Automata.layer;

import org.wpsim.World.Automata.cell.LayerCell;

/**
 * Abstract class that holds the structure for a layer that contains only one cell
 *
 * @param <T> type of cell that holds the layer
 */
public abstract class GenericWorldLayerUniqueCell<T extends LayerCell> extends GenericWorldLayer {

    /**
     * Referenced cell
     */
    protected T cell;

    /**
     *
     */
    public GenericWorldLayerUniqueCell() {
    }

    /**
     *
     * @param cell
     */
    public GenericWorldLayerUniqueCell(T cell) {
        this.cell = cell;
    }

    /**
     * Retrieve the linked cell
     *
     * @return The cell of the layer
     */
    public T getCell() {
        return cell;
    }

    /**
     * Sets the cell to the layer
     *
     * @param cell cell to set in the layer
     */
    public void setCell(T cell) {
        this.cell = cell;
    }
}
