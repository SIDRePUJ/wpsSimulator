/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 * \ V  V / | |_) |\__ \ *    @since 2023                                  *
 * \_/\_/  | .__/ |___/ *                                                 *
 * | |          *    @author Jairo Serrano                        *
 * |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.PeasantFamily.Data;

import BESA.Emotional.EmotionAxis;
import BESA.Emotional.EmotionalEvent;
import BESA.ExceptionBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.json.JSONObject;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.Control.Data.DateHelper;
import org.wpsim.Government.LandInfo;
import org.wpsim.PeasantFamily.Emotions.EmotionalComponent;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.wpsReport;
import rational.data.InfoData;
import rational.mapping.Believes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author jairo
 */
public class PeasantFamilyBDIAgentBelieves extends EmotionalComponent implements Believes {

    private PeasantFamilyProfile peasantProfile;
    private SeasonType currentSeason;
    private MoneyOriginType currentMoneyOrigin;
    private PeasantActivityType currentPeasantActivityType;
    private PeasantLeisureType currentPeasantLeisureType;
    private ResourceNeededType currentResourceNeededType;
    private List<LandInfo> assignedLands = new CopyOnWriteArrayList<>();
    private int currentDay;
    private int robberyAccount;
    private double timeLeftOnDay;
    private boolean newDay;
    private Map<String, Boolean> checkedToday;
    private boolean robbedToday;
    private boolean askedForLoanToday;
    private boolean haveLoan;
    private double toPay;
    private boolean loanDenied;
    private boolean leisureDoneToday;
    private boolean spendFamilyTimeDoneToday;
    private boolean friendsTimeDoneToday;
    private String internalCurrentDate;
    private String ptwDate;
    private Map<String, FarmingResource> priceList = new HashMap<>();
    private Map<Long, TaskLog> taskLog = new ConcurrentHashMap<>();
    private LinkedHashMap<Integer, Boolean> unblockDaysList = new LinkedHashMap<>();

    /**
     * @param alias          Peasant Family Alias
     * @param peasantProfile profile of the peasant family
     */
    public PeasantFamilyBDIAgentBelieves(String alias, PeasantFamilyProfile peasantProfile) {
        this.setPeasantProfile(peasantProfile);

        this.internalCurrentDate = ControlCurrentDate.getInstance().getCurrentDate();
        this.peasantProfile.setPeasantFamilyAlias(alias);
        this.taskLog.clear();

        this.currentDay = 1;
        this.timeLeftOnDay = 24;
        this.checkedToday = new HashMap<String, Boolean>();
        this.askedForLoanToday = false;
        this.robbedToday = false;
        this.haveLoan = false;
        this.newDay = true;
        this.priceList.clear();
        this.loanDenied = false;
        this.ptwDate = "";
        this.leisureDoneToday = false;

        this.currentMoneyOrigin = MoneyOriginType.NONE;
        this.currentPeasantActivityType = PeasantActivityType.NONE;
        this.currentPeasantLeisureType = PeasantLeisureType.NONE;

    }

    public List<LandInfo> getAssignedLands() {
        return assignedLands;
    }

    /**
     * Establece los terrenos asignados a partir de un mapa proporcionado.
     * Limpia la lista actual y la llena con objetos LandInfo basados en las entradas del mapa.
     *
     * @param lands Un mapa con nombres de terrenos como claves y tipos de terreno como valores.
     */
    public void setAssignedLands(Map<String, String> lands) {
        if (lands == null) {
            return;
        }

        List<LandInfo> newAssignedLands = new ArrayList<>();

        for (Map.Entry<String, String> entry : lands.entrySet()) {
            newAssignedLands.add(
                    new LandInfo(
                            entry.getKey(),
                            entry.getValue(),
                            getPeasantProfile().getPeasantFamilyLandAlias()
                    )
            );
        }

        this.assignedLands.clear();
        this.assignedLands.addAll(newAssignedLands);
    }


    /**
     * Actualiza la información del terreno en la lista.
     * Si el terreno con el mismo nombre ya existe en la lista, se actualiza con la nueva información.
     *
     * @param newLandInfo La nueva información del terreno.
     * @return true si el terreno fue actualizado exitosamente, false en caso contrario.
     */
    public void updateAssignedLands(LandInfo newLandInfo) {
        assignedLands.remove(newLandInfo);
        assignedLands.add(newLandInfo);
    }

    /**
     * Verifica si hay algún terreno disponible en la lista que no sea de tipo "water" y no esté en uso.
     * Imprime información sobre cada terreno durante la verificación.
     *
     * @return true si hay un terreno disponible, false en caso contrario.
     */
    public boolean isLandAvailable() {
        for (LandInfo landInfo : assignedLands) {
            if (!landInfo.getKind().equals("water") && !landInfo.isUsed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca y devuelve un terreno disponible en la lista que no sea de tipo "water" y no esté en uso.
     * El terreno devuelto se marca como usado.
     *
     * @return El terreno disponible o null si no hay ninguno.
     */
    public boolean getLandAvailable() {
        for (LandInfo landInfo : assignedLands) {
            if (!landInfo.getKind().equals("water") && landInfo.getCurrentSeason().equals(SeasonType.NONE)) {
                return true;
            }
        }
        return false;
    }


    public synchronized LandInfo getLandInfo(String landName) {
        for (LandInfo landInfo : assignedLands) {
            if (landInfo.getLandName().equals(landName)) {
                return landInfo;
            }
        }
        return null;
    }

    /**
     * Establece la temporada actual para un terreno específico basado en su nombre.
     * Si se encuentra el terreno en la lista, se actualiza su temporada.
     *
     * @param landName      El nombre del terreno cuya temporada se desea actualizar.
     * @param currentSeason La nueva temporada que se desea establecer para el terreno.
     */
    public void setCurrentSeason(String landName, SeasonType currentSeason) {
        LandInfo landInfo = getLandInfo(landName);
        if (landInfo != null) {
            landInfo.setCurrentSeason(currentSeason);
        }
    }

    public void setCurrentLandKind(String landName, String currentKind) {
        LandInfo landInfo = getLandInfo(landName);
        if (landInfo != null) {
            landInfo.setKind(currentKind);
        }
    }


    /**
     * Sets the current crop care type for the specified land.
     *
     * @param landName            the name of the land
     * @param currentCropCareType the new crop care type
     */
    public void setCurrentCropCareType(String landName, CropCareType currentCropCareType) {
        LandInfo landInfo = getLandInfo(landName);
        landInfo.setCurrentCropCareType(currentCropCareType);
    }

    /**
     * Adds a task to the log.
     *
     * @param date the date of the task
     */
    public void addTaskToLog(String date) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String fullClassName = stackTraceElements[2].getClassName();
        String[] parts = fullClassName.split("\\.");
        String taskName = parts[parts.length - 1];
        long time = System.currentTimeMillis() - wpsStart.startTime;
        TaskLog taskLog = new TaskLog(date, taskName);
        this.taskLog.put(time, taskLog);
    }

    public boolean isFamilyTimeDoneToday() {
        return spendFamilyTimeDoneToday;
    }

    public void setSpendFamilyTimeDoneToday(boolean spendFamilyTimeDoneToday) {
        this.spendFamilyTimeDoneToday = spendFamilyTimeDoneToday;
    }

    public boolean isFriendsTimeDoneToday() {
        return friendsTimeDoneToday;
    }

    public void setFriendsTimeDoneToday(boolean friendsTimeDoneToday) {
        this.friendsTimeDoneToday = friendsTimeDoneToday;
    }

    public boolean isHaveLoan() {
        return haveLoan;
    }

    public void setHaveLoan(boolean haveLoan) {
        this.haveLoan = haveLoan;
    }

    public boolean isAskedForLoanToday() {
        return askedForLoanToday;
    }

    public void setAskedForLoanToday() {
        this.askedForLoanToday = true;
    }

    public boolean isRobbedToday() {
        return robbedToday;
    }

    public void setRobbedToday() {
        this.robbedToday = false;
    }

    public boolean isCheckedToday(String landName) {
        return checkedToday.getOrDefault(landName, false);
    }

    public void setCheckedToday(String landName) {
        checkedToday.put(landName, true);
    }

    public int getRobberyAccount() {
        return robberyAccount;
    }

    public void increaseRobberyAccount() {
        this.robberyAccount++;
    }

    public String getPtwDate() {
        return ptwDate;
    }

    public void setPtwDate(String ptwDate) {
        this.ptwDate = ptwDate;
    }

    /**
     * @return
     */
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * @param currentDay
     */
    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    /**
     * @return
     */
    public double getTimeLeftOnDay() {
        return timeLeftOnDay;
    }

    /**
     * @param timeLeftOnDay
     */
    public void setTimeLeftOnDay(double timeLeftOnDay) {
        this.timeLeftOnDay = timeLeftOnDay;
    }

    /**
     * @return
     */
    public String getInternalCurrentDate() {
        return internalCurrentDate;
    }

    /**
     * @param internalCurrentDate
     */
    public void setInternalCurrentDate(String internalCurrentDate) {
        this.internalCurrentDate = internalCurrentDate;
    }

    /**
     * Time unit defined by hours spent on activities.
     *
     * @param time
     */
    public void useTime(TimeConsumedBy time) {
        decreaseTime(time.getTime());
    }

    /**
     * Time unit defined by hours spent on activities.
     *
     * @param time
     */
    public void useTime(double time) {
        decreaseTime(time);
    }

    /**
     * Make variable reset Every Day
     */
    public void makeNewDay() {
        this.currentDay++;
        this.timeLeftOnDay = 24;
        this.newDay = true;
        this.robbedToday = false;
        this.askedForLoanToday = false;
        this.internalCurrentDate = ControlCurrentDate.getInstance().getDatePlusOneDay(internalCurrentDate);
        if (this.currentSeason == SeasonType.GROWING) {
            checkedToday.replaceAll((k, v) -> false);
        }
    }

    public void makeNewDayWOD() {
        this.currentDay++;
        this.timeLeftOnDay = 24;
        this.newDay = true;
        this.robbedToday = false;
        this.askedForLoanToday = false;
        if (this.currentSeason == SeasonType.GROWING) {
            checkedToday.replaceAll((k, v) -> false);
        }
    }

    /**
     * Time unit defined by hours spent on activities.
     *
     * @param time
     */
    public synchronized void decreaseTime(double time) {
        wpsReport.debug("decreaseTime: " + time, getPeasantProfile().getPeasantFamilyAlias());
        timeLeftOnDay = timeLeftOnDay - time;
        if (timeLeftOnDay <= 0) {
            this.makeNewDay();
        }
    }

    /**
     * @param time
     * @return
     */
    public boolean haveTimeAvailable(TimeConsumedBy time) {
        return this.timeLeftOnDay - time.getTime() >= 0;
    }

    /**
     * Check if is a new Day
     *
     * @return true if is a new day
     */
    public boolean isNewDay() {
        return this.newDay;
    }

    /**
     * Set a new Day false
     *
     * @param newDay
     */
    public void setNewDay(boolean newDay) {
        this.newDay = newDay;
    }


    /**
     * @return
     */
    public ResourceNeededType getCurrentResourceNeededType() {
        return currentResourceNeededType;
    }

    /**
     *
     */
    public void setCurrentResourceNeededType(ResourceNeededType currentResourceNeededType) {
        this.currentResourceNeededType = currentResourceNeededType;
    }

    /**
     * @return
     */
    public PeasantLeisureType getCurrentPeasantLeisureType() {
        return currentPeasantLeisureType;
    }

    /**
     * @param currentPeasantLeisureType
     */
    public void setCurrentPeasantLeisureType(PeasantLeisureType currentPeasantLeisureType) {
        this.currentPeasantLeisureType = currentPeasantLeisureType;
    }

    /**
     *
     */
    public void setRandomCurrentPeasantLeisureType() {
        Random rand = new Random();

        switch (rand.nextInt(1)) {
            case 0 -> this.currentPeasantLeisureType = PeasantLeisureType.LEISURE;
            case 1 -> this.currentPeasantLeisureType = PeasantLeisureType.WASTERESOURCE;
            //case 2 -> this.currentPeasantLeisureType = PeasantLeisureType.WASTERESOURCE;
        }
    }

    /**
     * @return
     */
    public SeasonType getCurrentSeason() {
        return currentSeason;
    }

    /**
     * @return
     */
    public MoneyOriginType getCurrentMoneyOrigin() {
        return currentMoneyOrigin;
    }

    /**
     * @param currentMoneyOrigin the currentMoneyOrigin to set
     */
    public void setCurrentMoneyOrigin(MoneyOriginType currentMoneyOrigin) {
        this.currentMoneyOrigin = currentMoneyOrigin;
    }

    public PeasantActivityType getCurrentActivity() {
        return this.currentPeasantActivityType;
    }

    public void setCurrentActivity(PeasantActivityType peasantActivityType) {
        this.currentPeasantActivityType = peasantActivityType;
    }

    /**
     * @return the currentPeasantActivityType
     */
    public PeasantFamilyProfile getPeasantProfile() {
        return peasantProfile;
    }

    /**
     * @param peasantProfile the peasantProfile to set
     */
    private void setPeasantProfile(PeasantFamilyProfile peasantProfile) {
        this.peasantProfile = peasantProfile;
    }

    /**
     * @param infoData
     * @return
     */
    @Override
    public boolean update(InfoData infoData) {
        return true;
    }

    /**
     * @return the priceList
     */
    public Map<String, FarmingResource> getPriceList() {
        return priceList;
    }

    /**
     * @param priceList the priceList to set
     */
    public void setPriceList(Map<String, FarmingResource> priceList) {
        this.priceList = priceList;
    }

    /**
     * @return @throws CloneNotSupportedException
     */
    @Override
    public Believes clone() throws CloneNotSupportedException {
        return this;
    }

    /**
     * @return
     */
    public synchronized String toJson() {
        JSONObject dataObject = new JSONObject();
        dataObject.put("money", peasantProfile.getMoney());
        dataObject.put("health", peasantProfile.getHealth());
        dataObject.put("timeLeftOnDay", timeLeftOnDay);
        dataObject.put("newDay", newDay);
        dataObject.put("currentSeason", currentSeason);
        dataObject.put("robberyAccount", robberyAccount);
        dataObject.put("purpose", peasantProfile.getPurpose());
        dataObject.put("peasantFamilyAffinity", peasantProfile.getPeasantFamilyAffinity());
        dataObject.put("peasantLeisureAffinity", peasantProfile.getPeasantLeisureAffinity());
        dataObject.put("peasantFriendsAffinity", peasantProfile.getPeasantFriendsAffinity());
        dataObject.put("currentPeasantLeisureType", currentPeasantLeisureType);
        dataObject.put("currentResourceNeededType", currentResourceNeededType);
        dataObject.put("currentDay", currentDay);
        dataObject.put("askedForLoanToday", askedForLoanToday);
        dataObject.put("robbedToday", robbedToday);
        dataObject.put("checkedToday", checkedToday);
        dataObject.put("internalCurrentDate", internalCurrentDate);
        dataObject.put("toPay", toPay);
        dataObject.put("peasantKind", peasantProfile.getPeasantKind());
        dataObject.put("rainfallConditions", peasantProfile.getRainfallConditions());
        dataObject.put("peasantFamilyMinimalVital", peasantProfile.getPeasantFamilyMinimalVital());
        dataObject.put("peasantFamilyLandAlias", peasantProfile.getPeasantFamilyLandAlias());
        dataObject.put("leisureDoneToday", leisureDoneToday);
        dataObject.put("spendFamilyTimeDoneToday", spendFamilyTimeDoneToday);
        dataObject.put("friendsTimeDoneToday", friendsTimeDoneToday);
        dataObject.put("currentActivity", currentPeasantActivityType);
        dataObject.put("farm", peasantProfile.getFarmName());
        dataObject.put("cropSize", peasantProfile.getCropSize());
        dataObject.put("loanAmountToPay", peasantProfile.getLoanAmountToPay());
        dataObject.put("tools", peasantProfile.getTools());
        dataObject.put("seeds", peasantProfile.getSeeds());
        dataObject.put("waterAvailable", peasantProfile.getWaterAvailable());
        dataObject.put("pesticidesAvailable", peasantProfile.getPesticidesAvailable());
        dataObject.put("riceSeedsByHectare", peasantProfile.getRiceSeedsByHectare());
        dataObject.put("harvestedWeight", peasantProfile.getHarvestedWeight());

        if (!getAssignedLands().isEmpty()) {
            dataObject.put("assignedLands", getAssignedLands());
        } else {
            dataObject.put("assignedLands", Collections.emptyList());
        }

        try {
            List<EmotionAxis> emotions = this.getEmotionsListCopy();
            emotions.forEach(emotion -> {
                dataObject.put(emotion.toString(), emotion.getCurrentValue());
            });
        } catch (CloneNotSupportedException e) {
            dataObject.put("emotions", 0);
        }

        JSONObject finalDataObject = new JSONObject();
        finalDataObject.put("name", peasantProfile.getPeasantFamilyAlias());
        finalDataObject.put("state", dataObject.toString());

        finalDataObject.put("taskLog", new JSONObject(taskLog).toString());
        finalDataObject.put("unblockDateList", new JSONObject(unblockDaysList).toString());

        return finalDataObject.toString();
    }

    public double getToPay() {
        return toPay;
    }

    public void setToPay(double toPay) {
        this.toPay = toPay;
    }

    public void discountToPay(double toPay) {
        this.toPay -= toPay;
    }

    public boolean isLoanDenied() {
        return loanDenied;
    }

    public void setLoanDenied(boolean loanDenied) {
        this.loanDenied = loanDenied;
    }

    public void decreaseHealth() {
        this.peasantProfile.decreaseHealth();
        if (this.getPeasantProfile().getHealth() <= 0) {
            try {
                wpsReport.debug("👻👻 murió agente " + this.peasantProfile.getPeasantFamilyAlias() + " 👻👻", this.peasantProfile.getPeasantFamilyAlias());
                AdmBESA adm = AdmBESA.getInstance();
                AgHandlerBESA agHandler = adm.getHandlerByAlias(this.peasantProfile.getPeasantFamilyAlias());
                adm.killAgent(agHandler.getAgId(), wpsStart.PASSWD);
                this.processEmotionalEvent(
                        new EmotionalEvent("FAMILY", "STARVING", "FOOD")
                );
            } catch (ExceptionBESA ex) {
                wpsReport.error(ex, this.peasantProfile.getPeasantFamilyAlias());
            }
        }
    }

    public boolean isLeisureDoneToday() {
        return leisureDoneToday;
    }

    public void setLeisureDoneToday(boolean leisureDoneToday) {
        this.leisureDoneToday = leisureDoneToday;
    }

    public boolean isPlantingSeason() {
        return DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 0 ||
                DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 3 ||
                DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 6 ||
                DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 8;
    }
}

