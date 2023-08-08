package org.wpsim.World.layer.crop.cell;

import org.wpsim.World.Automata.cell.LayerCellState;

/**
 * Implementation for a generic crop cell state, holds the attributes that change over time
 */
public class CropCellState implements LayerCellState {

    /**
     *
     */
    protected double evapotranspiration;

    /**
     *
     */
    protected double growingDegreeDays;

    /**
     *
     */
    protected double aboveGroundBiomass;

    /**
     *
     */
    protected double cumulatedEvapotranspiration;

    /**
     *
     */
    protected double depletionFractionAdjusted;

    /**
     *
     */
    protected double rootZoneDepletionAtTheEndOfDay;

    /**
     *
     */
    protected boolean waterStress;

    /**
     *
     * @param riceEvapotranspiration
     * @param currentGrowingDegreeDays
     * @param currentAboveGroundBiomass
     * @param cumulatedEvapotranspiration
     * @param depletionFractionAdjusted
     * @param rootZoneDepletionAtTheEndOfDay
     * @param waterStress
     */
    public CropCellState(
            double riceEvapotranspiration,
            double currentGrowingDegreeDays,
            double currentAboveGroundBiomass,
            double cumulatedEvapotranspiration,
            double depletionFractionAdjusted,
            double rootZoneDepletionAtTheEndOfDay,
            boolean waterStress
    ) {
        this.evapotranspiration = riceEvapotranspiration;
        this.growingDegreeDays = currentGrowingDegreeDays;
        this.aboveGroundBiomass = currentAboveGroundBiomass;
        this.cumulatedEvapotranspiration = cumulatedEvapotranspiration;
        this.depletionFractionAdjusted = depletionFractionAdjusted;
        this.rootZoneDepletionAtTheEndOfDay = rootZoneDepletionAtTheEndOfDay;
        this.waterStress = waterStress;
    }

    /**
     *
     */
    public CropCellState() {
    }

    /**
     *
     * @return
     */
    public double getEvapotranspiration() {
        return evapotranspiration;
    }

    /**
     *
     * @param evapotranspiration
     */
    public void setEvapotranspiration(double evapotranspiration) {
        this.evapotranspiration = evapotranspiration;
    }

    /**
     *
     * @return
     */
    public double getGrowingDegreeDays() {
        return growingDegreeDays;
    }

    /**
     *
     * @param growingDegreeDays
     */
    public void setGrowingDegreeDays(double growingDegreeDays) {
        this.growingDegreeDays = growingDegreeDays;
    }

    /**
     *
     * @return
     */
    public double getAboveGroundBiomass() {
        return aboveGroundBiomass;
    }

    /**
     *
     * @param aboveGroundBiomass
     */
    public void setAboveGroundBiomass(double aboveGroundBiomass) {
        this.aboveGroundBiomass = aboveGroundBiomass;
    }

    /**
     *
     * @return
     */
    public double getCumulatedEvapotranspiration() {
        return cumulatedEvapotranspiration;
    }

    /**
     *
     * @param cumulatedEvapotranspiration
     */
    public void setCumulatedEvapotranspiration(double cumulatedEvapotranspiration) {
        this.cumulatedEvapotranspiration = cumulatedEvapotranspiration;
    }

    /**
     *
     * @return
     */
    public double getDepletionFractionAdjusted() {
        return depletionFractionAdjusted;
    }

    /**
     *
     * @param depletionFractionAdjusted
     */
    public void setDepletionFractionAdjusted(double depletionFractionAdjusted) {
        this.depletionFractionAdjusted = depletionFractionAdjusted;
    }

    /**
     *
     * @return
     */
    public double getRootZoneDepletionAtTheEndOfDay() {
        return rootZoneDepletionAtTheEndOfDay;
    }

    /**
     *
     * @param rootZoneDepletionAtTheEndOfDay
     */
    public void setRootZoneDepletionAtTheEndOfDay(double rootZoneDepletionAtTheEndOfDay) {
        this.rootZoneDepletionAtTheEndOfDay = rootZoneDepletionAtTheEndOfDay;
    }

    /**
     *
     * @return
     */
    public boolean isWaterStress() {
        return waterStress;
    }

    /**
     *
     * @param waterStress
     */
    public void setWaterStress(boolean waterStress) {
        this.waterStress = waterStress;
    }
}
