package org.wpsim.World.layer.shortWaveRadiation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.wpsim.Control.DateHelper;
import org.wpsim.World.Helper.ExtraterrestrialRadiation;
import org.wpsim.World.Helper.Hemisphere;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Automata.layer.LayerExecutionParams;
import org.wpsim.World.layer.LayerFunctionParams;
import org.wpsim.World.layer.SimWorldSimpleLayer;
import org.wpsim.World.layer.data.MonthData;

/**
 * Short wave radiation layer implementation
 */
public class ShortWaveRadiationLayer extends SimWorldSimpleLayer<ShortWaveRadiationCell> {

    //private static final Logger logger = LogManager.getLogger(ShortWaveRadiationLayer.class);
    private final double albedoReflection = 0.23;
    private double a_s = 0.25;
    private double b_s = 0.5;
    private Hemisphere hemisphere;
    private double[] monthlyExtraterrestrialRadiationForLocation;

    private int latitudeDegrees;

    private WorldConfiguration worldConfig = WorldConfiguration.getPropsInstance();

    /**
     *
     * @param datafile
     * @param hemisphere
     * @param latitudeDegrees
     */
    public ShortWaveRadiationLayer(String datafile, Hemisphere hemisphere, int latitudeDegrees) {
        super(datafile);
        this.hemisphere = hemisphere;
        this.latitudeDegrees = latitudeDegrees;
        this.cell = new ShortWaveRadiationCell("radCell");
        this.setupLayer();
    }

    @Override
    public void setupLayer() {
        if (this.hemisphere == Hemisphere.NORTHERN) {
            this.monthlyExtraterrestrialRadiationForLocation = ExtraterrestrialRadiation.getNorthernData().get(
                    this.latitudeDegrees % 2 == 0 ? this.latitudeDegrees : this.latitudeDegrees + 1
            );
        } else {
            this.monthlyExtraterrestrialRadiationForLocation = ExtraterrestrialRadiation.getSouthernData().get(
                    this.latitudeDegrees % 2 == 0 ? this.latitudeDegrees : this.latitudeDegrees + 1
            );
        }
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
            double nextShortWaveRadiationRate = this.calculateNetShortWaveRadiationForMonth(monthFromDate);
            this.cell.setCellState(params1.getDate(),
                    new ShortWaveRadiationCellState(nextShortWaveRadiationRate)
            );
        } else {
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(this.worldConfig.getProperty("date.format"));
            int daysBetweenLastDataAndNewEvent = DateHelper.differenceDaysBetweenTwoDates(this.cell.getDate(), params1.getDate());
            for (int i = 0; i < daysBetweenLastDataAndNewEvent; i++) {
                DateTime previousStateDate = DateHelper.getDateInJoda(this.cell.getDate());
                DateTime previousStateDatePlusOneDay = previousStateDate.plusDays(1);
                int month = previousStateDatePlusOneDay.getMonthOfYear() - 1;
                String newDate = dtfOut.print(previousStateDatePlusOneDay);
                this.cell.setCellState(newDate, new ShortWaveRadiationCellState(this.calculateNetShortWaveRadiationForMonth(month)));
            }
        }
    }

    private double calculateNetShortWaveRadiationForMonth(int month) {
        return (1 - this.albedoReflection) * this.calculateShortWaveRadiation(month);
    }

    private double calculateShortWaveRadiation(int month) {
        MonthData monthData = this.monthlyData.get(month);
        return (this.a_s + this.b_s * (this.calculateGaussianFromMonthData(month) / monthData.getMaxValue())) * this.monthlyExtraterrestrialRadiationForLocation[month];
    }


}
