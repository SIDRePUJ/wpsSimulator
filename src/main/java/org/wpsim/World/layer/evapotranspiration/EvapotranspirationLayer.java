package org.wpsim.World.layer.evapotranspiration;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.wpsim.SimulationControl.Data.DateHelper;
import org.wpsim.World.Automata.layer.LayerExecutionParams;
import org.wpsim.World.layer.LayerFunctionParams;
import org.wpsim.World.layer.SimWorldSimpleLayer;

/**
 * Concrete implementation of the evapotranspiration layer
 */
public class EvapotranspirationLayer extends SimWorldSimpleLayer<EvapotranspirationCell> {

    //private static final Logger logger = LogManager.getLogger(EvapotranspirationLayer.class);

    /**
     *
     * @param dataFile
     */
    public EvapotranspirationLayer(String dataFile) {
        super(dataFile);
        this.cell = new EvapotranspirationCell("evapoCell");
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
        if (this.cell.getCellState() == null) {
            int monthFromDate = DateHelper.getMonthFromStringDate(params1.getDate());
            double nextEvapotranspirationRate = this.calculateGaussianFromMonthData(monthFromDate);
            this.cell.setCellState(params1.getDate(), new EvapotranspirationCellState(nextEvapotranspirationRate));
        } else {
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(this.worldConfig.getProperty("date.format"));
            int daysBetweenLastDataAndNewEvent = DateHelper.differenceDaysBetweenTwoDates(this.cell.getDate(), params1.getDate());
            for (int i = 0; i < daysBetweenLastDataAndNewEvent; i++) {
                DateTime previousStateDate = DateHelper.getDateInJoda(this.cell.getDate());
                DateTime previousStateDatePlusOneDay = previousStateDate.plusDays(1);
                int month = previousStateDatePlusOneDay.getMonthOfYear() - 1;
                String newDate = dtfOut.print(previousStateDatePlusOneDay);
                this.cell.setCellState(newDate, new EvapotranspirationCellState(this.calculateGaussianFromMonthData(month)));
            }
        }
    }
}
