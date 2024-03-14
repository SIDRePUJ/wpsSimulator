package org.wpsim.AgroEcosystem.Automata.cell;

/**
 * Interface that provides the basic contract for a cell
 */
public interface LayerCell {

    /**
     * Returns the current cell state
     *
     * @return Cell state
     */
    LayerCellState getCellState();

    /**
     * Returns the current id of the cell
     *
     * @return Cell id
     */
    String getId();

}
