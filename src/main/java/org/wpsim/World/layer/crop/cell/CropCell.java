package org.wpsim.World.layer.crop.cell;

import org.wpsim.World.Helper.Soil;
import org.wpsim.World.Automata.cell.GenericWorldLayerCell;
import org.wpsim.World.Automata.cell.LayerCellState;
import org.wpsim.World.layer.disease.DiseaseCell;

/**
 * Crop cell abstract implementation, holds the necessary attributes (according to FAO) to model the crop cell behavior
 *
 * @param <S> Cell
 */
public abstract class CropCell<S extends LayerCellState> extends GenericWorldLayerCell<S> {

    /**
     *
     */
    protected double cropFactor_ini;

    /**
     *
     */
    protected double cropFactor_mid;

    /**
     *
     */
    protected double cropFactor_end;

    /**
     *
     */
    protected double degreeDays_mid;

    /**
     *
     */
    protected double degreeDays_end;

    /**
     *
     */
    protected int cropArea;

    /**
     *
     */
    protected boolean isActive;

    /**
     *
     */
    protected DiseaseCell diseaseCell;

    /**
     *
     */
    protected double maximumRootDepth;

    /**
     *
     */
    protected double depletionFraction;

    /**
     *
     */
    protected Soil soilType;

    // TAW

    /**
     *
     */
    protected double totalAvailableWater;

    //RAW

    /**
     *
     */

    protected double readilyAvailableWater;

    /**
     *
     */
    protected String agentPeasantId;

    /**
     *
     */
    protected boolean harvestReady = false;

    /**
     *
     * @param cropFactor_ini
     * @param cropFactor_mid
     * @param cropFactor_end
     * @param degreeDays_mid
     * @param degreeDays_end
     * @param cropArea
     * @param maximumRootDepth
     * @param depletionFraction
     * @param soilType
     * @param isActive
     * @param diseaseCell
     * @param agentPeasantId
     */
    public CropCell(double cropFactor_ini,
                    double cropFactor_mid,
                    double cropFactor_end,
                    double degreeDays_mid,
                    double degreeDays_end,
                    int cropArea,
                    double maximumRootDepth,
                    double depletionFraction,
                    Soil soilType,
                    boolean isActive,
                    DiseaseCell diseaseCell,
                    String agentPeasantId) {
        this.cropFactor_ini = cropFactor_ini;
        this.cropFactor_mid = cropFactor_mid;
        this.cropFactor_end = cropFactor_end;
        this.degreeDays_mid = degreeDays_mid;
        this.degreeDays_end = degreeDays_end;
        this.cropArea = cropArea;
        this.isActive = isActive;
        this.diseaseCell = diseaseCell;
        this.maximumRootDepth = maximumRootDepth;
        this.depletionFraction = depletionFraction;
        this.soilType = soilType;
        this.agentPeasantId = agentPeasantId;
        this.calculateTAWRAW();
    }

    /**
     *
     */
    public CropCell() {
    }

    //Equations from https://www.fao.org/3/x0490e/x0490e0e.htm#total%20available%20water%20(taw)  equations 82, 83
    private void calculateTAWRAW() {
        this.totalAvailableWater = 1000 * (this.soilType.getWaterContentAtFieldCapacity() - this.soilType.getWaterContentAtWiltingPoint()) * this.maximumRootDepth;
        this.readilyAvailableWater = this.depletionFraction * this.totalAvailableWater;
    }

    /**
     *
     * @return
     */
    public double getCropFactor_ini() {
        return cropFactor_ini;
    }

    /**
     *
     * @param cropFactor_ini
     */
    public void setCropFactor_ini(double cropFactor_ini) {
        this.cropFactor_ini = cropFactor_ini;
    }

    /**
     *
     * @return
     */
    public double getCropFactor_mid() {
        return cropFactor_mid;
    }

    /**
     *
     * @param cropFactor_mid
     */
    public void setCropFactor_mid(double cropFactor_mid) {
        this.cropFactor_mid = cropFactor_mid;
    }

    /**
     *
     * @return
     */
    public double getCropFactor_end() {
        return cropFactor_end;
    }

    /**
     *
     * @param cropFactor_end
     */
    public void setCropFactor_end(double cropFactor_end) {
        this.cropFactor_end = cropFactor_end;
    }

    /**
     *
     * @return
     */
    public int getCropArea() {
        return cropArea;
    }

    /**
     *
     * @param cropArea
     */
    public void setCropArea(int cropArea) {
        this.cropArea = cropArea;
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     *
     * @param active
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     *
     * @return
     */
    public double getDegreeDays_mid() {
        return degreeDays_mid;
    }

    /**
     *
     * @param degreeDays_mid
     */
    public void setDegreeDays_mid(double degreeDays_mid) {
        this.degreeDays_mid = degreeDays_mid;
    }

    /**
     *
     * @return
     */
    public double getDegreeDays_end() {
        return degreeDays_end;
    }

    /**
     *
     * @param degreeDays_end
     */
    public void setDegreeDays_end(double degreeDays_end) {
        this.degreeDays_end = degreeDays_end;
    }

    /**
     *
     * @return
     */
    public DiseaseCell getDiseaseCell() {
        return diseaseCell;
    }

    /**
     *
     * @param diseaseCell
     */
    public void setDiseaseCell(DiseaseCell diseaseCell) {
        this.diseaseCell = diseaseCell;
    }

    /**
     *
     * @return
     */
    public double getMaximumRootDepth() {
        return maximumRootDepth;
    }

    /**
     *
     * @param maximumRootDepth
     */
    public void setMaximumRootDepth(double maximumRootDepth) {
        this.maximumRootDepth = maximumRootDepth;
    }

    /**
     *
     * @return
     */
    public double getDepletionFraction() {
        return depletionFraction;
    }

    /**
     *
     * @param depletionFraction
     */
    public void setDepletionFraction(double depletionFraction) {
        this.depletionFraction = depletionFraction;
    }

    /**
     *
     * @return
     */
    public Soil getSoilType() {
        return soilType;
    }

    /**
     *
     * @param soilType
     */
    public void setSoilType(Soil soilType) {
        this.soilType = soilType;
    }

    /**
     *
     * @return
     */
    public double getTotalAvailableWater() {
        return totalAvailableWater;
    }

    /**
     *
     * @return
     */
    public double getReadilyAvailableWater() {
        return readilyAvailableWater;
    }

    /**
     *
     * @return
     */
    public String getAgentPeasantId() {
        return agentPeasantId;
    }

    /**
     *
     * @param agentPeasantId
     */
    public void setAgentPeasantId(String agentPeasantId) {
        this.agentPeasantId = agentPeasantId;
    }

    /**
     *
     * @return
     */
    public boolean isHarvestReady() {
        return harvestReady;
    }

    /**
     *
     * @param harvestReady
     */
    public void setHarvestReady(boolean harvestReady) {
        this.harvestReady = harvestReady;
    }
}