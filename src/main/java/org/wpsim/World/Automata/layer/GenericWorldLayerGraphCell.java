package org.wpsim.World.Automata.layer;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.wpsim.World.Automata.cell.LayerCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract class that holds the structure for a layer that contains multiple cells in form of a graph
 *
 * @param <C> Type of cell
 */
public abstract class GenericWorldLayerGraphCell<C extends LayerCell> extends GenericWorldLayer {

    /**
     * Data structure that holds the graph of linked cells, supported on the library jgrapht
     */
    protected Graph<C, DefaultEdge> simpleCellGraph = new SimpleGraph<>(DefaultEdge.class);
    /**
     * Data structure that holds the cells in form of array full of cells without link
     */
    protected List<C> allCells = new ArrayList<>();
    /**
     * For random purposes if necessary
     */
    protected Random random = new Random();

}
