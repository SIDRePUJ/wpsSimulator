package org.wpsim.AgroEcosystem.Automata.layer;

import org.wpsim.AgroEcosystem.Automata.cell.LayerCell;

/**
 * Abstract class that holds the structure for a layer that contains a matrix of cells
 *
 * @param <T> Type of the cell
 */
public abstract class GenericWorldLayerMatrixCell<T extends LayerCell> extends GenericWorldLayer {

    /**
     * Data structure that holds the matrix of cells
     */
    protected T[][] cellMatrix;

    /**
     *
     * @param cellMatrix
     */
    public GenericWorldLayerMatrixCell(T[][] cellMatrix) {
        this.cellMatrix = cellMatrix;
    }

    /**
     *
     */
    public GenericWorldLayerMatrixCell() {
    }

    /**
     * Gets the current cell matrix
     *
     * @return Cell matrix
     */
    public T[][] getCellMatrix() {
        return cellMatrix;
    }

    /**
     * Sets the cell matrix
     *
     * @param cellMatrix Matrix of cells
     */
    public void setCellMatrix(T[][] cellMatrix) {
        this.cellMatrix = cellMatrix;
    }
}
