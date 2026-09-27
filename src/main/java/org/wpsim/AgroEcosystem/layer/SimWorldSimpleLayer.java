package org.wpsim.AgroEcosystem.layer;

import org.wpsim.AgroEcosystem.Helper.MonthlyDataLoader;
import org.wpsim.AgroEcosystem.Helper.WorldConfiguration;
import org.wpsim.AgroEcosystem.Automata.cell.LayerCell;
import org.wpsim.AgroEcosystem.Automata.layer.GenericWorldLayerUniqueCell;
import org.wpsim.AgroEcosystem.layer.data.MonthData;
import org.wpsim.research.water.ResearchClimateRandom;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Abstract implementation for the layers, used for this specific world simulation
 *
 * @param <C> type of cell
 */
public abstract class SimWorldSimpleLayer<C extends LayerCell> extends GenericWorldLayerUniqueCell<C> {

    /**
     *
     */
    protected List<MonthData> monthlyData;

    /**
     *
     */
    protected Random random;

    /**
     *
     */
    protected WorldConfiguration worldConfig = WorldConfiguration.getPropsInstance();

    /**
     *
     * @param dataFile
     */
    public SimWorldSimpleLayer(String dataFile) {
        this.loadYearDataFromFile(dataFile);
        this.random = org.wpsim.WellProdSim.Util.SimRandom.get();
    }

    /** Isolate research climate draws from concurrent agents and other plots. */
    public final void useResearchRandom(long scenarioSeed, String plotId) {
        this.random = ResearchClimateRandom.stream(scenarioSeed, plotId, getClass().getName());
    }

    /**
     *
     * @param month
     * @return
     */
    protected double calculateGaussianFromMonthData(int month) {
        MonthData monthData = this.monthlyData.get(month);
        return this.random.nextGaussian() * monthData.getStandardDeviation() + monthData.getAverage();
    }

    /**
     *
     * @param dataFile
     */
    protected void loadYearDataFromFile(String dataFile) {
        try {
            this.monthlyData = MonthlyDataLoader.loadMonthlyDataFile(dataFile);
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception.getMessage());
        }
    }

}
