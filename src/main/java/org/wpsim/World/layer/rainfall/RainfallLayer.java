package org.wpsim.World.layer.rainfall;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.wpsim.SimulationControl.Data.DateHelper;
import org.wpsim.World.Automata.layer.LayerExecutionParams;
import org.wpsim.World.layer.LayerFunctionParams;
import org.wpsim.World.layer.SimWorldSimpleLayer;

/**
 * Rainfall layer concrete implementation
 */
public class RainfallLayer extends SimWorldSimpleLayer<RainfallCell> {

    //private static final Logger logger = LogManager.getLogger(RainfallLayer.class);

    /**
     *
     * @param dataFile
     */
    public RainfallLayer(String dataFile) {
        super(dataFile);
        this.cell = new RainfallCell("rainCell");
    }


    @Override
    public void setupLayer() {
    }

    @Override
    public void executeLayer() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public void executeLayer(LayerExecutionParams params) {
        LayerFunctionParams params1 = (LayerFunctionParams) params;
        double rainfallThresholdPercentage = Double.parseDouble(this.worldConfig.getProperty("rainfall.thresholdPercentage"));
        if (this.cell.getCellState() == null) {
            int monthFromDate = DateHelper.getMonthFromStringDate(params1.getDate());
            double newRainfallRate = this.calculateGaussianFromMonthData(monthFromDate);
            double averageRainfallForMonth = monthlyData.get(monthFromDate).getAverage();
            double adjustedRainfall = averageRainfallForMonth + averageRainfallForMonth * rainfallThresholdPercentage;
            double verifyRainfall = newRainfallRate <= adjustedRainfall ? 0 : newRainfallRate;
            this.cell.setCellState(params1.getDate(), new RainfallCellState(verifyRainfall));
        } else {
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(this.worldConfig.getProperty("date.format"));
            int daysBetweenLastDataAndNewEvent = DateHelper.differenceDaysBetweenTwoDates(this.cell.getDate(), params1.getDate());
            for (int i = 0; i < daysBetweenLastDataAndNewEvent; i++) {
                DateTime previousStateDate = DateHelper.getDateInJoda(this.cell.getDate());
                DateTime previousStateDatePlusOneDay = previousStateDate.plusDays(1);
                int month = previousStateDatePlusOneDay.getMonthOfYear() - 1;
                String newDate = dtfOut.print(previousStateDatePlusOneDay);
                double newRainfallRate = this.calculateGaussianFromMonthData(month);
                double averageRainfallForMonth = monthlyData.get(month).getAverage();
                double adjustedRainfall = averageRainfallForMonth + averageRainfallForMonth * rainfallThresholdPercentage;
                double verifyRainfall = newRainfallRate <= adjustedRainfall ? 0 : newRainfallRate;
                this.cell.setCellState(newDate, new RainfallCellState(verifyRainfall));
            }
        }
    }

}
