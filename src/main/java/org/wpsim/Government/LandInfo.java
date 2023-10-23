package org.wpsim.Government;

import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.PeasantFamily.Data.SeasonType;

import java.util.Objects;

public class LandInfo implements Cloneable{
    private String farmName;
    private String kind;
    private String landName;
    private boolean isUsed;
    private SeasonType currentSeason;
    private CropCareType currentCropCareType;

    public LandInfo(String landName, String kind, String farmName) {
        this.setKind(kind);
        this.setUsed(false);
        this.setFarmName(farmName);
        this.setLandName(landName);
        this.setCurrentSeason(SeasonType.NONE);
        this.setCurrentCropCareType(CropCareType.NONE);
    }

    public LandInfo(String landName, String kind) {
        this.setKind(kind);
        this.setUsed(false);
        this.setFarmName(null);
        this.setLandName(landName);
        this.setCurrentSeason(SeasonType.NONE);
        this.setCurrentCropCareType(CropCareType.NONE);
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
}
