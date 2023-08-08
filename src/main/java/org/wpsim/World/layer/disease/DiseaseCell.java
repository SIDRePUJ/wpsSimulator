package org.wpsim.World.layer.disease;

import org.wpsim.World.Automata.cell.GenericWorldLayerCell;

/**
 * Concrete disease cell implementation
 */
public class DiseaseCell extends GenericWorldLayerCell<DiseaseCellState> {

    private String id;
    private double percentageOfCropCoverage = 0;
    private String dateInsecticideApplication;

    /**
     *
     * @param id
     */
    public DiseaseCell(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     *
     * @return
     */
    public double getPercentageOfCropCoverage() {
        return percentageOfCropCoverage;
    }

    /**
     *
     * @param percentageOfCropCoverage
     */
    public void setPercentageOfCropCoverage(double percentageOfCropCoverage) {
        this.percentageOfCropCoverage = percentageOfCropCoverage;
    }

    /**
     *
     * @return
     */
    public String getDateInsecticideApplication() {
        return dateInsecticideApplication;
    }

    /**
     *
     * @param dateInsecticideApplication
     */
    public void setDateInsecticideApplication(String dateInsecticideApplication) {
        this.dateInsecticideApplication = dateInsecticideApplication;
    }
}
