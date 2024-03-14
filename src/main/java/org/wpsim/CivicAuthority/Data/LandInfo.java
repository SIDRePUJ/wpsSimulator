package org.wpsim.CivicAuthority.Data;

import org.wpsim.PeasantFamily.Data.Utils.CropCareType;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;

import java.util.Objects;

public class LandInfo implements Cloneable{
    private String farmName;
    private String kind;
    private String landName;
    private boolean isUsed;
    private String cropName;
    private SeasonType currentSeason;
    private CropCareType currentCropCareType;
    private int totalRequiredTime;  // Total time required (in hours or days, as you prefer)
    private int elapsedWorkTime;    // Elapsed work time (in hours or days)
    private boolean isProcessing;
    private String yearPlanted;
    public LandInfo(String landName, String kind, String farmName, String yearPlanted) {
        setupLandInfo(landName, kind, farmName, yearPlanted);
    }
    public LandInfo(String landName, String kind) {
        setupLandInfo(landName, kind, null, null);
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    private void setupLandInfo(String landName, String kind, String farmName, String yearPlanted) {
        this.setKind(kind);
        this.setUsed(false);
        this.setFarmName(farmName);
        this.setLandName(landName);
        this.setCurrentSeason(SeasonType.NONE);
        this.setCurrentCropCareType(CropCareType.NONE);
        this.setProcessing(false);
        this.setYearPlanted(yearPlanted);
        this.setCropName("");
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public CropCareType getCurrentCropCareType() {
        return currentCropCareType;
    }

    public void setCurrentCropCareType(CropCareType currentCropCareType) {

        this.currentCropCareType = currentCropCareType;
    }

    public SeasonType getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(SeasonType currentSeason) {
        this.currentSeason = currentSeason;
    }

    @Override
    public String toString() {
        return "LandInfo{" +
                "farmName='" + farmName + '\'' +
                ", kind='" + kind + '\'' +
                ", landName='" + landName + '\'' +
                ", isUsed=" + isUsed +
                '}';
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public String getLandName() {
        return landName;
    }

    public void setLandName(String landName) {
        this.landName = landName;
    }

    public int getTotalRequiredTime() {
        return totalRequiredTime;
    }

    public void setTotalRequiredTime(int totalRequiredTime) {
        this.totalRequiredTime = totalRequiredTime;
    }

    public int getElapsedWorkTime() {
        return elapsedWorkTime;
    }

    public void setElapsedWorkTime(int elapsedWorkTime) {
        this.elapsedWorkTime = elapsedWorkTime;
    }

    public void increaseElapsedWorkTime(int workDone) {
        this.elapsedWorkTime += (int) workDone;
    }

    public boolean elapsedWorkTimeIsDone() {
        return elapsedWorkTime >= totalRequiredTime;
    }

    public void resetElapsedWorkTime() {
        this.elapsedWorkTime = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LandInfo landInfo = (LandInfo) o;
        return Objects.equals(landName, landInfo.landName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(landName);
    }

    public String getYearPlanted() {
        return yearPlanted;
    }

    public void setYearPlanted(String yearPlanted) {
        this.yearPlanted = yearPlanted;
    }
}
