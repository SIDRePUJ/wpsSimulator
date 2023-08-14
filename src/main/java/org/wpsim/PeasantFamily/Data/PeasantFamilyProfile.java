/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Data;

import org.json.JSONObject;

import java.io.Serializable;

/**
 *
 * @author jairo
 */
public class PeasantFamilyProfile implements Serializable, Cloneable {

    private String purpose;
    private String peasantKind;
    private String peasantFamilyAlias;
    private double peasantFamilyMinimalVital;
    private int health;
    private double variance;
    private double peasantLeisureAffinity;
    private double peasantFriendsAffinity;
    private double peasantFamilyAffinity;
    private boolean worker;
    private double peasantQualityFactor;
    private double liveStockAffinity;
    private boolean land;
    private int cropSize;
    private double housing;
    private double servicesPresence;
    private double housingSize;
    private double housingCondition;
    private double housingLocation;
    private double farmDistance;
    private double money;
    private double totalIncome;
    private int loanAmountToPay;
    private double housingQuailty;
    private double timeSpentOnMaintenance;
    private double cropHealth;
    private double farmReady;
    private double harvestedWeightExpected;
    private boolean processedCrop;
    private double cropEficiency;
    private double processedWeight;
    private double processingTime;
    private double trainingLevel;
    private double trainingAvailability;
    private double trainingRelevance;
    private double trainingCost;
    private double irrigation;
    private double irrigationTime;
    private double pestControl;
    private double diseasedCrop;
    private double weedControl;
    private double infestedCrop;
    private double suppliesAvailability;
    private double toolsAvailability;
    private boolean associated;
    private int neighbors;
    private double collaborationValue;
    private double healthProgramsAvailability;
    private boolean livestockFarming;
    private double livestockHealth;
    private int livestockNumber;
    private double familyTime;

    private double familyTimeAvailability;
    private double communications;
    private double socialCompatibility;
    private double restingTimeAvailibility;
    private double peasantRestAffinity;
    private double leisureOptions;
    private boolean sellDone;

    // Resources
    private int waterAvailable;
    private int pesticidesAvailable;
    private int tools;
    private int supplies;
    private int riceSeedsByHectare;
    private int seeds;
    private int harvestedWeight;
    private double totalHarvestedWeight;
    private String startRiceSeason;
    private String endRiceSeason;
    private String currentCropName;
    private String rainfallConditions;

    /**
     *
     */
    public PeasantFamilyProfile() {
    }
    public double getVariance() {
        return variance;
    }
    public void setVariance(double variance) {
        this.variance = variance;
    }

    public String getRainfallConditions() {
        return rainfallConditions;
    }

    public void setRainfallConditions(String rainfallConditions) {
        this.rainfallConditions = rainfallConditions;
    }

    public String getPeasantKind() {
        return peasantKind;
    }

    public void setPeasantKind(String peasantKind) {
        this.peasantKind = peasantKind;
    }

    /**
     *
     * @return
     */
    public String getStartRiceSeason() {
        return startRiceSeason;
    }

    /**
     *
     * @param startRiceSeason
     */
    public void setStartRiceSeason(String startRiceSeason) {
        this.startRiceSeason = startRiceSeason;
    }

    /**
     *
     * @return
     */
    public String getEndRiceSeason() {
        return endRiceSeason;
    }

    /**
     *
     * @param endRiceSeason
     */
    public void setEndRiceSeason(String endRiceSeason) {
        this.endRiceSeason = endRiceSeason;
    }

    /**
     *
     * @return
     */
    public String getCurrentCropName() {
        return this.currentCropName;
    }

    /**
     *
     * @param currentCropName
     */
    public void setCurrentCropName(String currentCropName) {
        this.currentCropName = currentCropName;
    }

    /**
     *
     * @return
     */
    public int getRiceSeedsByHectare() {
        return riceSeedsByHectare;
    }

    /**
     *
     * @param riceSeedsByHectare
     */
    public void setRiceSeedsByHectare(int riceSeedsByHectare) {
        this.riceSeedsByHectare = riceSeedsByHectare;
    }

    /**
     *
     * @return
     */
    public int getPesticidesAvailable() {
        return pesticidesAvailable;
    }

    /**
     *
     * @param pesticidesAvailable
     */
    public void setPesticidesAvailable(int pesticidesAvailable) {
        this.pesticidesAvailable = pesticidesAvailable;
    }

    /**
     *
     * @return
     */
    public double getPeasantFamilyMinimalVital() {
        return peasantFamilyMinimalVital;
    }

    /**
     *
     * @param peasantFamilyMinimalVital
     */
    public void setPeasantFamilyMinimalVital(double peasantFamilyMinimalVital) {
        this.peasantFamilyMinimalVital = peasantFamilyMinimalVital;
    }

    /**
     *
     * @return
     */
    public boolean isSellDone() {
        return sellDone;
    }

    /**
     *
     */
    public void discountDailyMoney() {
        this.money = this.money - this.peasantFamilyMinimalVital;
        if (this.money <= 0){
            this.decreaseHealth();
        }
    }

    /**
     *
     * @param sellDone
     */
    public void setSellDone(boolean sellDone) {
        this.sellDone = sellDone;
    }

    /**
     *
     * @return
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     *
     * @param purpose
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     *
     * @return
     */
    public String getPeasantFamilyAlias() {
        return peasantFamilyAlias;
    }

    /**
     *
     * @return
     */
    public String getPeasantFamilyLandAlias() {
        return peasantFamilyAlias + "_land";
    }

    /**
     *
     * @param peasantFamilyAlias
     */
    public void setPeasantFamilyAlias(String peasantFamilyAlias) {
        this.peasantFamilyAlias = peasantFamilyAlias;
    }

    /**
     *
     * @return
     */
    public int getHealth() {
        return health;
    }

    /**
     *
     * @param health
     */
    public void setHealth(int health) {
        if (health > 100) {
            this.health = 100;
        } else {
            this.health = health;
        }
    }

    /**
     *
     */
    public synchronized void increaseHealth() {
        if (this.health >= 100) {
            // @TODO: Incrementar felicidad
            this.health = 100;
        } else if (this.health <= 0) {
            this.health = 0;
        } else {
            this.health = this.health + 2;
        }
    }

    /**
     *
     * @return
     */
    public double getPeasantLeisureAffinity() {
        return peasantLeisureAffinity;
    }

    /**
     *
     * @param peasantLeisureAffinity
     */
    public synchronized void setPeasantLeisureAffinity(double peasantLeisureAffinity) {
        this.peasantLeisureAffinity = Math.min(Math.max(peasantLeisureAffinity, 0), 1);
    }

    /**
     *
     * @return
     */
    public double getPeasantFriendsAffinity() {
        return peasantFriendsAffinity;
    }

    /**
     *
     * @param peasantFriendsAffinity
     */
    public synchronized void setPeasantFriendsAffinity(double peasantFriendsAffinity) {
        this.peasantFriendsAffinity = Math.min(Math.max(peasantFriendsAffinity, 0), 1);
    }

    /**
     *
     * @return
     */
    public boolean isAWorker() {
        return worker;
    }

    /**
     *
     * @param worker
     */
    public synchronized void setWorker(boolean worker) {
        this.worker = worker;
    }

    /**
     *
     * @return
     */
    public double getPeasantQualityFactor() {
        return peasantQualityFactor;
    }

    /**
     *
     * @param peasantQualityFactor
     */
    public synchronized void setPeasantQualityFactor(double peasantQualityFactor) {
        this.peasantQualityFactor = peasantQualityFactor;
    }

    /**
     *
     * @return
     */
    public int getTools() {
        return tools;
    }

    /**
     *
     * @param tools
     */
    public synchronized void setTools(int tools) {
        this.tools += tools;
    }

    /**
     *
     * @return
     */
    public int getSupplies() {
        return supplies;
    }

    /**
     *
     * @param supplies
     */
    public synchronized void setSupplies(int supplies) {
        this.supplies += supplies;
    }

    /**
     *
     * @return
     */
    public double getLiveStockAffinity() {
        return liveStockAffinity;
    }

    /**
     *
     * @param liveStockAffinity
     */
    public synchronized void setLiveStockAffinity(double liveStockAffinity) {
        this.liveStockAffinity = liveStockAffinity;
    }

    /**
     *
     * @return
     */
    public boolean getLand() {
        return land;
    }

    /**
     *
     */
    public synchronized void setLand(boolean land) {
        this.land = land;
    }

    /**
     *
     * @return
     */
    public int getCropSize() {
        return cropSize;
    }
    public double getCropSizeHA() {
        return cropSize / 1000;
    }

    /**
     *
     * @param cropSize
     */
    public synchronized void setCropSize(int cropSize) {
        this.cropSize = cropSize;
    }

    /**
     *
     * @return
     */
    public double getHousing() {
        return housing;
    }

    /**
     *
     * @param housing
     */
    public void setHousing(double housing) {
        this.housing = housing;
    }

    /**
     *
     * @return
     */
    public double getServicesPresence() {
        return servicesPresence;
    }

    /**
     *
     * @param servicesPresence
     */
    public synchronized void setServicesPresence(double servicesPresence) {
        this.servicesPresence = servicesPresence;
    }

    /**
     *
     * @return
     */
    public double getHousingSize() {
        return housingSize;
    }

    /**
     *
     * @param housingSize
     */
    public synchronized void setHousingSize(double housingSize) {
        this.housingSize = housingSize;
    }

    /**
     *
     * @return
     */
    public double getHousingCondition() {
        return housingCondition;
    }

    /**
     *
     * @param housingCondition
     */
    public synchronized void setHousingCondition(double housingCondition) {
        this.housingCondition = housingCondition;
    }

    /**
     *
     * @return
     */
    public double getHousingLocation() {
        return housingLocation;
    }

    /**
     *
     * @param housingLocation
     */
    public synchronized void setHousingLocation(double housingLocation) {
        this.housingLocation = housingLocation;
    }

    /**
     *
     * @return
     */
    public double getFarmDistance() {
        return farmDistance;
    }

    /**
     *
     * @param farmDistance
     */
    public synchronized void setFarmDistance(double farmDistance) {
        this.farmDistance = farmDistance;
    }

    /**
     *
     * @return
     */
    public double getMoney() {
        return money;
    }

    /**
     *
     * @param money
     */
    public synchronized void setMoney(Integer money) {
        this.money = money;
    }

    /**
     *
     * @param money
     */
    public synchronized void increaseMoney(Integer money) {
        this.money += money;
    }

    /**
     *
     * @return
     */
    public double getTotalIncome() {
        return totalIncome;
    }

    /**
     *
     * @param totalIncome
     */
    public synchronized void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    /**
     *
     * @return
     */
    public double getLoanAmountToPay() {
        return loanAmountToPay;
    }

    /**
     *
     * @param loanAmountToPay
     */
    public synchronized void setLoanAmountToPay(int loanAmountToPay) {
        this.loanAmountToPay = loanAmountToPay;
    }

    /**
     *
     * @return
     */
    public int getHarvestedWeight() {
        return harvestedWeight;
    }

    /**
     *
     * @param harvestedWeight
     */
    public synchronized void setHarvestedWeight(int harvestedWeight) {
        this.harvestedWeight = harvestedWeight;
    }

    /**
     *
     * @return
     */
    public double getHousingQuailty() {
        return housingQuailty;
    }

    /**
     *
     * @param housingQuailty
     */
    public synchronized void setHousingQuailty(double housingQuailty) {
        this.housingQuailty = housingQuailty;
    }

    /**
     *
     * @return
     */
    public double getTimeSpentOnMaintenance() {
        return timeSpentOnMaintenance;
    }

    /**
     *
     * @param timeSpentOnMaintenance
     */
    public synchronized void setTimeSpentOnMaintenance(double timeSpentOnMaintenance) {
        this.timeSpentOnMaintenance = timeSpentOnMaintenance;
    }

    /**
     *
     * @return
     */
    public int getSeeds() {
        return seeds;
    }

    /**
     *
     * @param seeds
     */
    public synchronized void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    /**
     *
     * @return
     */
    public double getCropHealth() {
        return cropHealth;
    }

    /**
     *
     * @param cropHealth
     */
    public synchronized void setCropHealth(double cropHealth) {
        this.cropHealth = cropHealth;
    }

    /**
     *
     * @return
     */
    public double getFarmReady() {
        return farmReady;
    }

    /**
     *
     * @param farmReady
     */
    public synchronized void setFarmReady(double farmReady) {
        this.farmReady = farmReady;
    }

    /**
     *
     * @return
     */
    public double getHarvestedWeightExpected() {
        return harvestedWeightExpected;
    }

    /**
     *
     * @param harvestedWeightExpected
     */
    public synchronized void setHarvestedWeightExpected(double harvestedWeightExpected) {
        this.harvestedWeightExpected = harvestedWeightExpected;
    }

    /**
     *
     * @return
     */
    public synchronized boolean isProcessedCrop() {
        return processedCrop;
    }

    /**
     *
     * @param processedCrop
     */
    public synchronized void setProcessedCrop(boolean processedCrop) {
        this.processedCrop = processedCrop;
    }

    /**
     *
     * @return
     */
    public double getCropEficiency() {
        return cropEficiency;
    }

    /**
     *
     * @param cropEficiency
     */
    public synchronized void setCropEficiency(double cropEficiency) {
        this.cropEficiency = cropEficiency;
    }

    /**
     *
     * @return
     */
    public double getProcessedWeight() {
        return processedWeight;
    }

    /**
     *
     * @param processedWeight
     */
    public synchronized void setProcessedWeight(double processedWeight) {
        this.processedWeight = processedWeight;
    }

    /**
     *
     * @return
     */
    public double getProcessingTime() {
        return processingTime;
    }

    /**
     *
     * @param processingTime
     */
    public synchronized void setProcessingTime(double processingTime) {
        this.processingTime = processingTime;
    }

    /**
     *
     * @return
     */
    public synchronized double getTrainingLevel() {
        return trainingLevel;
    }

    /**
     *
     * @param trainingLevel
     */
    public synchronized void setTrainingLevel(double trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    /**
     *
     * @return
     */
    public synchronized double getTrainingAvailability() {
        return trainingAvailability;
    }

    /**
     *
     * @param trainingAvailability
     */
    public synchronized void setTrainingAvailability(double trainingAvailability) {
        this.trainingAvailability = trainingAvailability;
    }

    /**
     *
     * @return
     */
    public synchronized double getTrainingRelevance() {
        return trainingRelevance;
    }

    /**
     *
     * @param trainingRelevance
     */
    public synchronized void setTrainingRelevance(double trainingRelevance) {
        this.trainingRelevance = trainingRelevance;
    }

    /**
     *
     * @return
     */
    public synchronized double getTrainingCost() {
        return trainingCost;
    }

    /**
     *
     * @param trainingCost
     */
    public synchronized void setTrainingCost(double trainingCost) {
        this.trainingCost = trainingCost;
    }

    /**
     *
     * @return
     */
    public synchronized double getIrrigation() {
        return irrigation;
    }

    /**
     *
     * @param irrigation
     */
    public synchronized void setIrrigation(double irrigation) {
        this.irrigation = irrigation;
    }

    /**
     *
     * @return
     */
    public synchronized double getWaterAvailable() {
        return waterAvailable;
    }

    /**
     *
     * @param waterAvailable
     */
    public synchronized void setWaterAvailable(double waterAvailable) {
        this.waterAvailable += waterAvailable;
    }

    /**
     *
     * @return
     */
    public synchronized double getIrrigationTime() {
        return irrigationTime;
    }

    /**
     *
     * @param irrigationTime
     */
    public synchronized void setIrrigationTime(double irrigationTime) {
        this.irrigationTime = irrigationTime;
    }

    /**
     *
     * @return
     */
    public synchronized double getPestControl() {
        return pestControl;
    }

    /**
     *
     * @param pestControl
     */
    public synchronized void setPestControl(double pestControl) {
        this.pestControl = pestControl;
    }

    /**
     *
     * @return
     */
    public synchronized double getDiseasedCrop() {
        return diseasedCrop;
    }

    /**
     *
     * @param diseasedCrop
     */
    public synchronized void setDiseasedCrop(double diseasedCrop) {
        this.diseasedCrop = diseasedCrop;
    }

    /**
     *
     * @return
     */
    public synchronized double getWeedControl() {
        return weedControl;
    }

    /**
     *
     * @param weedControl
     */
    public synchronized void setWeedControl(double weedControl) {
        this.weedControl = weedControl;
    }

    /**
     *
     * @return
     */
    public synchronized double getInfestedCrop() {
        return infestedCrop;
    }

    /**
     *
     * @param infestedCrop
     */
    public synchronized void setInfestedCrop(double infestedCrop) {
        this.infestedCrop = infestedCrop;
    }

    /**
     *
     * @return
     */
    public synchronized double getSuppliesAvailability() {
        return suppliesAvailability;
    }

    /**
     *
     * @param suppliesAvailability
     */
    public synchronized void setSuppliesAvailability(double suppliesAvailability) {
        this.suppliesAvailability = suppliesAvailability;
    }

    /**
     *
     * @return
     */
    public synchronized double getToolsAvailability() {
        return toolsAvailability;
    }

    /**
     *
     * @param toolsAvailability
     */
    public synchronized void setToolsAvailability(double toolsAvailability) {
        this.toolsAvailability = toolsAvailability;
    }

    /**
     *
     * @return
     */
    public synchronized boolean isAssociated() {
        return associated;
    }

    /**
     *
     * @param associated
     */
    public synchronized void setAssociated(boolean associated) {
        this.associated = associated;
    }

    /**
     *
     * @return
     */
    public synchronized int getNeighbors() {
        return neighbors;
    }

    /**
     *
     * @param neighbors
     */
    public synchronized void setNeighbors(int neighbors) {
        this.neighbors = neighbors;
    }

    /**
     *
     * @return
     */
    public synchronized double getCollaborationValue() {
        return collaborationValue;
    }

    /**
     *
     * @param collaborationValue
     */
    public synchronized void setCollaborationValue(double collaborationValue) {
        this.collaborationValue = collaborationValue;
    }

    /**
     *
     * @return
     */
    public synchronized double getHealthProgramsAvailability() {
        return healthProgramsAvailability;
    }

    /**
     *
     * @param healthProgramsAvailability
     */
    public synchronized void setHealthProgramsAvailability(double healthProgramsAvailability) {
        this.healthProgramsAvailability = healthProgramsAvailability;
    }

    /**
     *
     * @return
     */
    public synchronized boolean isLivestockFarming() {
        return livestockFarming;
    }

    /**
     *
     * @param livestockFarming
     */
    public synchronized void setLivestockFarming(boolean livestockFarming) {
        this.livestockFarming = livestockFarming;
    }

    /**
     *
     * @return
     */
    public synchronized double getLivestockHealth() {
        return livestockHealth;
    }

    /**
     *
     * @param livestockHealth
     */
    public synchronized void setLivestockHealth(double livestockHealth) {
        this.livestockHealth = livestockHealth;
    }

    /**
     *
     * @return
     */
    public synchronized int getLivestockNumber() {
        return livestockNumber;
    }

    /**
     *
     * @param livestockNumber
     */
    public synchronized void setLivestockNumber(int livestockNumber) {
        this.livestockNumber += livestockNumber;
    }

    /**
     *
     * @return
     */
    public synchronized double getFamilyTime() {
        return familyTime;
    }

    /**
     *
     * @param familyTime
     */
    public synchronized void setFamilyTime(double familyTime) {
        this.familyTime = familyTime;
    }

    /**
     *
     * @return
     */
    public synchronized double getPeasantFamilyAffinity() {
        return peasantFamilyAffinity;
    }

    /**
     *
     * @param peasantFamilyAffinity
     */
    public synchronized void setPeasantFamilyAffinity(double peasantFamilyAffinity) {
        this.peasantFamilyAffinity = Math.min(Math.max(peasantFamilyAffinity, 0), 1);
    }

    /**
     *
     * @return
     */
    public synchronized double getFamilyTimeAvailability() {
        return familyTimeAvailability;
    }

    /**
     *
     * @param familyTimeAvailability
     */
    public synchronized void setFamilyTimeAvailability(double familyTimeAvailability) {
        this.familyTimeAvailability = Math.min(Math.max(familyTimeAvailability, 0), 1);
    }

    /**
     *
     */
    public synchronized void increaseFamilyTimeAvailability() {
        //wpsReport.info("");
        /*if (this.familyTimeAvailability == 1) {
            this.familyTimeAvailability = 1;
        } else {
            this.familyTimeAvailability = this.familyTimeAvailability + 0.1;
        }*/
        this.familyTimeAvailability = 1;
    }

    /**
     *
     */
    public synchronized void useFamilyTimeAvailability() {
        //wpsReport.info("");
        /*if (this.familyTimeAvailability > 0) {
            this.familyTimeAvailability = this.familyTimeAvailability - 0.1;
        } else {
            this.familyTimeAvailability = 0;
        }*/
        this.familyTimeAvailability = 0;
    }

    /**
     *
     * @return
     */
    public synchronized double getCommunications() {
        return communications;
    }

    /**
     *
     * @param communications
     */
    public synchronized void setCommunications(double communications) {
        this.communications = communications;
    }

    /**
     *
     * @return
     */
    public synchronized double getSocialCompatibility() {
        return socialCompatibility;
    }

    /**
     *
     * @param socialCompatibility
     */
    public synchronized void setSocialCompatibility(double socialCompatibility) {
        this.socialCompatibility = socialCompatibility;
    }

    /**
     *
     * @return
     */
    public synchronized double getRestingTimeAvailibility() {
        return restingTimeAvailibility;
    }

    /**
     *
     * @param restingTimeAvailibility
     */
    public synchronized void setRestingTimeAvailibility(double restingTimeAvailibility) {
        this.restingTimeAvailibility = restingTimeAvailibility;
    }

    /**
     *
     * @return
     */
    public synchronized double getPeasantRestAffinity() {
        return peasantRestAffinity;
    }

    /**
     *
     * @param peasantRestAffinity
     */
    public synchronized void setPeasantRestAffinity(double peasantRestAffinity) {
        this.peasantRestAffinity = peasantRestAffinity;
    }

    /**
     *
     * @return
     */
    public synchronized double getLeisureOptions() {
        return leisureOptions;
    }

    /**
     *
     * @param leisureOptions
     */
    public synchronized void setLeisureOptions(double leisureOptions) {
        this.leisureOptions = leisureOptions;
    }

    /**
     *
     */
    public synchronized void useLeisureOptions() {
        /*wpsReport.info("");
        if (this.leisureOptions > 0) {
            this.leisureOptions = this.leisureOptions - 0.1;
        }*/
        this.leisureOptions = 0;
    }

    /**
     *
     */
    public synchronized void increaseLeisureOptions() {
        //wpsReport.info("");
        /*if (this.leisureOptions >= 1) {
            this.leisureOptions = 1;
        } else {
            this.leisureOptions = this.leisureOptions + 0.1;
        }*/
        this.leisureOptions = 1;
    }

    /**
     *
     */
    public synchronized void reduceHouseCondition() {
        //wpsReport.info("");
        /*if (this.housingCondition > 0) {
            this.housingCondition = this.housingCondition - 0.1;
        }*/
        this.housingCondition = 0;
    }

    /**
     *
     */
    public synchronized void increaseHouseCondition() {
        //wpsReport.info("");
        /*if (this.housingCondition >= 1) {
            this.housingCondition = 1;
        } else {
            this.housingCondition = this.housingCondition + 0.1;
        }*/
        this.housingCondition = 1;
    }

    /**
     *
     */
    public synchronized void increaseTools() {
        //wpsReport.info("");
        /*if (this.tools >= 1) {
            this.tools = 1;
        } else {
            this.tools = this.housingCondition + 0.1;
        }*/
        this.tools = 1;
    }

    /**
     *
     */
    public synchronized void useTools() {
        /*if (this.tools > 0) {
            this.tools = this.tools - 0.1;
        }*/
        this.tools = 0;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (purpose != null ? purpose.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public synchronized boolean equals(Object object) {
        if (!(object instanceof PeasantFamilyProfile)) {
            return false;
        }
        PeasantFamilyProfile other = (PeasantFamilyProfile) object;
        if ((this.purpose == null && other.purpose != null) || (this.purpose != null && !this.purpose.equals(other.purpose))) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public synchronized void increaseFarmReady() {
        /*
        if (this.farmReady == 1) {
            this.farmReady = 1;
        } else {
            this.farmReady = this.farmReady + 0.1;
        }*/
        this.farmReady = 1;
    }

    /**
     *
     */
    public synchronized void increaseTrainingLevel() {
        this.trainingLevel = 1;
    }

    /**
     *
     * @param pesticidesAvailable
     */
    public synchronized void setPesticidesAvailable(Integer pesticidesAvailable) {
        this.pesticidesAvailable += pesticidesAvailable;
    }

    /**
     *
     * @param discount
     */
    public synchronized void useMoney(int discount) {
        this.money -= discount;
    }

    public synchronized void useMoney(double discount) {
        this.money -= (int) discount;
    }

    /**
     *
     * @param seeds
     */
    public synchronized void useSeeds(int seeds) {
        this.seeds -= seeds;
    }

    /**
     *
     * @param water
     */
    public synchronized void useWater(int water) {
        this.waterAvailable -= water;
    }

    /**
     *
     */
    public void decreaseHealth() {
        this.health -= 10;
    }

    /**
     *
     * @return
     */
    public JSONObject toJson() {

        JSONObject dataObject1 = new JSONObject();
        dataObject1.put("peasantKind", peasantKind);
        dataObject1.put("rainfallConditions", rainfallConditions);
        dataObject1.put("peasantFamilyMinimalVital", peasantFamilyMinimalVital);
        dataObject1.put("health", health);
        dataObject1.put("productivity", peasantLeisureAffinity);
        dataObject1.put("wellBeging", peasantFriendsAffinity);
        dataObject1.put("worker", worker);
        dataObject1.put("peasantQualityFactor", peasantQualityFactor);
        dataObject1.put("liveStockAffinity", liveStockAffinity);
        dataObject1.put("farm", land);
        dataObject1.put("cropSize", cropSize);
        dataObject1.put("housing", housing);
        dataObject1.put("servicesPresence", servicesPresence);
        dataObject1.put("housingSize", housingSize);
        dataObject1.put("housingCondition", housingCondition);
        dataObject1.put("housingLocation", housingLocation);
        dataObject1.put("farmDistance", farmDistance);
        dataObject1.put("money", money);
        dataObject1.put("totalIncome", totalIncome);
        dataObject1.put("loanAmountToPay", loanAmountToPay);
        dataObject1.put("housingQuailty", housingQuailty);
        dataObject1.put("timeSpentOnMaintenance", timeSpentOnMaintenance);
        dataObject1.put("cropHealth", cropHealth);
        dataObject1.put("farmReady", farmReady);
        dataObject1.put("tools", tools);
        dataObject1.put("seeds", seeds);
        dataObject1.put("waterAvailable", waterAvailable);
        dataObject1.put("pesticidesAvailable", pesticidesAvailable);
        dataObject1.put("trainingLevel", trainingLevel);
        dataObject1.put("riceSeedsByHectare", riceSeedsByHectare);
        dataObject1.put("harvestedWeight", harvestedWeight);
        dataObject1.put("totalHarvestedWeight", totalHarvestedWeight);
        dataObject1.put("processedWeight", processedWeight);
        dataObject1.put("diseasedCrop", diseasedCrop);
        dataObject1.put("weedControl", weedControl);
        return dataObject1;
    }

    @Override
    public PeasantFamilyProfile clone() {
        try {
            return (PeasantFamilyProfile) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void increaseTotalHarvestedWeight(double harvested) {
        this.totalHarvestedWeight += harvested;
    }

}
