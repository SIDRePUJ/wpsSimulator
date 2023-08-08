package org.wpsim.World.layer.data;

/**
 *
 * @author jairo
 */
public class MonthData {
    private double average;
    private double standardDeviation;
    private double maxValue;
    private double minValue;

    /**
     *
     */
    public MonthData() {
    }

    /**
     *
     * @return
     */
    public double getAverage() {
        return average;
    }

    /**
     *
     * @param average
     */
    public void setAverage(double average) {
        this.average = average;
    }

    /**
     *
     * @return
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     *
     * @param standardDeviation
     */
    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    /**
     *
     * @return
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     *
     * @param maxValue
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    /**
     *
     * @return
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     *
     * @param minValue
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
}
