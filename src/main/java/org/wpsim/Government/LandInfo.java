package org.wpsim.Government;

public class LandInfo {
    public String farmName;
    String kind;
    boolean isUsed;
    public LandInfo(String kind) {
        this.kind = kind;
        this.isUsed = false;
        this.farmName = null;
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
}
