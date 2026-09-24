package org.wpsim.AgroEcosystem.layer.rainfall;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.wpsim.SimulationControl.Data.DateHelper;
import org.wpsim.AgroEcosystem.Automata.layer.LayerExecutionParams;
import org.wpsim.AgroEcosystem.layer.LayerFunctionParams;
import org.wpsim.AgroEcosystem.layer.SimWorldSimpleLayer;

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

    private static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private double prop(String key, double defaultValue) {
        try {
            String v = this.worldConfig.getProperty(key);
            return (v == null || v.isBlank()) ? defaultValue : Double.parseDouble(v.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Revisión TCSS: lluvia diaria (mm) que conserva el total mensual esperado y su estacionalidad.
     * Día lluvioso con probabilidad p = clip(total/escala, pMin, pMax); monto exponencial con media
     * total / (días * p). La regla original (umbral de 1.3 veces la media mensual) invertía la estacionalidad.
     */
    private double dailyRainfall(int month, double rainfallThresholdPercentage) {
        if (org.wpsim.WellProdSim.Util.Legacy.CLIMATE) {
            double newRainfallRate = this.calculateGaussianFromMonthData(month);
            double averageRainfallForMonth = monthlyData.get(month).getAverage();
            double adjustedRainfall = averageRainfallForMonth + averageRainfallForMonth * rainfallThresholdPercentage;
            return newRainfallRate <= adjustedRainfall ? 0 : newRainfallRate;
        }
        double monthly = Math.max(0.0, monthlyData.get(month).getAverage());
        double p = Math.max(prop("rainfall.wetDay.pMin", 0.07), Math.min(prop("rainfall.wetDay.pMax", 0.55),
                monthly / prop("rainfall.wetDay.scaleMm", 250.0)));
        if (this.random.nextDouble() >= p) {
            return 0;
        }
        double meanWetDay = monthly / (DAYS_IN_MONTH[month] * p);
        return -meanWetDay * Math.log(1.0 - this.random.nextDouble());
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
            double verifyRainfall = this.dailyRainfall(monthFromDate, rainfallThresholdPercentage);
            this.cell.setCellState(params1.getDate(), new RainfallCellState(verifyRainfall));
        } else {
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern(this.worldConfig.getProperty("date.format"));
            int daysBetweenLastDataAndNewEvent = DateHelper.differenceDaysBetweenTwoDates(this.cell.getDate(), params1.getDate());
            for (int i = 0; i < daysBetweenLastDataAndNewEvent; i++) {
                DateTime previousStateDate = DateHelper.getDateInJoda(this.cell.getDate());
                DateTime previousStateDatePlusOneDay = previousStateDate.plusDays(1);
                int month = previousStateDatePlusOneDay.getMonthOfYear() - 1;
                String newDate = dtfOut.print(previousStateDatePlusOneDay);
                double verifyRainfall = this.dailyRainfall(month, rainfallThresholdPercentage);
                this.cell.setCellState(newDate, new RainfallCellState(verifyRainfall));
            }
        }
    }

}
