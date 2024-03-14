package org.wpsim.AgroEcosystem.Automata.cell;

import org.wpsim.AgroEcosystem.Helper.WorldConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is a generic abstract implementation of a cell that holds a history of states
 * whenever they change
 *
 * @param <S> type of state of the cell
 */
public abstract class GenericWorldLayerCell<S extends LayerCellState> implements LayerCell {
    /**
     * Instance of the state of the cell
     */
    protected S cellState;

    /**
     * String date of the current cell state
     */
    protected String date;
    /**
     * List that holds the possible actions that can receive the cell
     */
    protected List<LayerCellAction> cellActions = new ArrayList<>();

    private Comparator<String> stringDateComparator = new Comparator<String>() {
        private String dateFormat = WorldConfiguration.getPropsInstance().getProperty("date.format");

        @Override
        public int compare(String o1, String o2) {
            int compare = 0;
            try {
                Date dateObj1 = new SimpleDateFormat(dateFormat).parse(o1);
                Date dateObj2 = new SimpleDateFormat(dateFormat).parse(o2);
                if (dateObj1.getTime() < dateObj2.getTime()) {
                    compare = -1;
                } else if (dateObj1.getTime() > dateObj2.getTime()) {
                    compare = 1;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return compare;
        }
    };

    /**
     * This structure holds all the states of the current cell when a new state is set for the current cell
     * is stored, in this case with the date in string format.
     */
    protected SortedMap<String, S> historicalStates = new TreeMap<>(stringDateComparator);

    /**
     * Retrieves the historical states for the cell
     *
     * @return Hashmap with all the historic states of the cell
     */
    public SortedMap<String, S> getHistoricalData() {
        return historicalStates;
    }

    /**
     * Retrieves a state for a given date, null if no state for a given date
     *
     * @param date String date
     * @return State
     */
    public S getCellStateByDate(String date) {
        return this.historicalStates.get(date);
    }

    /**
     * Retrieves the previous cell state
     *
     * @return previous cell state
     */
    public S getPreviousCellState() {
        return this.historicalStates.get(this.historicalStates.lastKey());
    }

    /**
     * Retrieves the previous cell state date added
     *
     * @return previous cell state date
     */
    public String getPreviousCellStateDate() {
        return this.historicalStates.lastKey();
    }

    /**
     * Sets a new state in the cell also stores it in the hasmap structure
     *
     * @param date      date in string format
     * @param cellState new cell state
     */
    public void setCellState(String date, S cellState) {
        this.historicalStates.put(date, cellState);
        this.cellState = cellState;
        this.date = date;
    }

    /**
     * Get current cell state date
     *
     * @return current cell state date
     */
    public String getDate() {
        return date;
    }

    /**
     * Retrieves the current cell state
     *
     * @return CellState
     */
    @Override
    public LayerCellState getCellState() {
        return this.cellState;
    }

    /**
     * Adds an action to the cell queue to be executed in the future
     *
     * @param layerCellAction action to be added to the queue
     */
    public void addCellAction(LayerCellAction layerCellAction) {
        this.cellActions.add(layerCellAction);
    }

    /**
     * Returns all the actions to be processed
     *
     * @return list of actions
     */
    public List<LayerCellAction> getCellActions() {
        return cellActions;
    }

    /**
     *
     * @param cellActions
     */
    public void setCellActions(List<LayerCellAction> cellActions) {
        this.cellActions = cellActions;
    }
}
