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
import org.json.JSONObject;
import org.wpsim.Control.Data.Coin;
import org.wpsim.Control.Data.ControlCurrentDate;
import org.wpsim.Control.Data.DateHelper;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.Utils.*;
import org.wpsim.PeasantFamily.Emotions.EmotionalComponent;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsReport;
import rational.data.InfoData;
import rational.mapping.Believes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private boolean workerWithoutLand;
    private boolean newDay;
    private boolean haveLoan;
    private double toPay;
    private boolean loanDenied;
    private String internalCurrentDate;
    private String ptwDate;
    private Map<String, FarmingResource> priceList = new HashMap<>();
    private Map<String, Set<String>> taskLog = new ConcurrentHashMap<>();
    private int daysToWorkForOther;
    private String peasantFamilyHelper;
    private String Contractor;
    private boolean haveEmotions;
    private boolean askedForContractor;
    private boolean wait;

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
        this.haveLoan = false;
        this.newDay = true;
        this.priceList.clear();
        this.loanDenied = false;
        this.ptwDate = "";
        this.peasantFamilyHelper = "";
        this.Contractor = "";

        this.currentMoneyOrigin = MoneyOriginType.NONE;
        this.currentPeasantActivityType = PeasantActivityType.NONE;
        this.currentPeasantLeisureType = PeasantLeisureType.NONE;

        if (wpsStart.RANDOM_EMOTIONS){
            this.setHaveEmotions(Coin.flipCoin());
        }

    }

    public boolean isHaveEmotions() {
        return haveEmotions;
    }

    public void setHaveEmotions(boolean haveEmotions) {
        this.haveEmotions = haveEmotions;
    }

    public boolean isAskedForContractor() {
        return askedForContractor;
    }

    public void setAskedForContractor(boolean askedForContractor) {
        this.askedForContractor = askedForContractor;
    }

    public String getContractor() {
        return Contractor;
    }

    public void setContractor(String peasantFamilyToHelp) {
        this.Contractor = peasantFamilyToHelp;
    }

    public boolean isWorkerWithoutLand() {
        return workerWithoutLand;
    }

    public void setWorkerWithoutLand() {
        this.workerWithoutLand = true;
        peasantProfile.setPurpose("worker");
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
                            getPeasantProfile().getPeasantFamilyLandAlias(),
                            ControlCurrentDate.getInstance().getCurrentYear()
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
            if (!landInfo.getKind().equals("water")
                    && !landInfo.getKind().equals("forest")
                    && landInfo.getCurrentSeason().equals(SeasonType.NONE)) {
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
        this.currentSeason = currentSeason;
        this.currentSeason = currentSeason;
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
     * Adds a task to the log for the specified date.
     *
     * @param date the date of the task
     */
    public void addTaskToLog(String date) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String fullClassName = stackTraceElements[2].getClassName();
        String[] parts = fullClassName.split("\\.");
        String taskName = parts[parts.length - 1];
        taskLog.computeIfAbsent(date, k -> ConcurrentHashMap.newKeySet()).add(taskName);
        //TaskLog taskLog = new TaskLog(date, taskName);
        //this.taskLog.put(time, taskLog);
    }

    /**
     * Checks if the specified task was executed on the specified date.
     *
     * @param date     Date to check
     * @param taskName Name of the task
     * @return true if the task was executed on the specified date, false otherwise
     */
    public boolean isTaskExecutedOnDate(String date, String taskName) {
        Set<String> tasks = taskLog.getOrDefault(date, new HashSet<>());
        //System.out.println(tasks + " " + taskName + " on " + date + " r " + tasks.contains(taskName));
        return tasks.contains(taskName);
    }

    public boolean isHaveLoan() {
        return haveLoan;
    }

    public void setHaveLoan(boolean haveLoan) {
        this.haveLoan = haveLoan;
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
     * Make variable reset Every Day and increment date
     */
    public void makeNewDay() {
        this.currentDay++;
        this.timeLeftOnDay = 24;
        this.newDay = true;
        this.internalCurrentDate = ControlCurrentDate.getInstance().getDatePlusOneDay(internalCurrentDate);
        wpsReport.mental(this.toCSV(), this.getAlias());
    }

    /**
     * Make variable reset Every Day without change date
     */
    public void makeNewDayWOD() {
        this.currentDay++;
        this.timeLeftOnDay = 24;
        this.newDay = true;
        wpsReport.mental(this.toCSV(), this.getAlias());
    }

    /**
     * Time unit defined by hours spent on activities.
     *
     * @param time
     */
    public synchronized void decreaseTime(double time) {
        //wpsReport.debug("decreaseTime: " + time, getPeasantProfile().getPeasantFamilyAlias());
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
        dataObject.put("initialMoney", peasantProfile.getInitialMoney());
        dataObject.put("health", peasantProfile.getHealth());
        dataObject.put("initialHealth", peasantProfile.getInitialHealth());
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
        dataObject.put("internalCurrentDate", internalCurrentDate);
        dataObject.put("toPay", toPay);
        dataObject.put("peasantKind", peasantProfile.getPeasantKind());
        dataObject.put("rainfallConditions", peasantProfile.getRainfallConditions());
        dataObject.put("peasantFamilyMinimalVital", peasantProfile.getPeasantFamilyMinimalVital());
        dataObject.put("peasantFamilyLandAlias", peasantProfile.getPeasantFamilyLandAlias());
        dataObject.put("currentActivity", currentPeasantActivityType);
        dataObject.put("farm", peasantProfile.getFarmName());
        dataObject.put("cropSize", peasantProfile.getCropSize());
        dataObject.put("loanAmountToPay", peasantProfile.getLoanAmountToPay());
        dataObject.put("tools", peasantProfile.getTools());
        dataObject.put("seeds", peasantProfile.getSeeds());
        dataObject.put("waterAvailable", peasantProfile.getWaterAvailable());
        dataObject.put("pesticidesAvailable", peasantProfile.getPesticidesAvailable());
        dataObject.put("totalHarvestedWeight", peasantProfile.getTotalHarvestedWeight());
        dataObject.put("contractor", getContractor());
        dataObject.put("daysToWorkForOther", getDaysToWorkForOther());
        dataObject.put("peasantFamilyHelper", getPeasantFamilyHelper());

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
        } catch (Exception e) {
            dataObject.put("emotions", 0);
        }

        JSONObject finalDataObject = new JSONObject();
        finalDataObject.put("name", peasantProfile.getPeasantFamilyAlias());
        finalDataObject.put("state", dataObject.toString());

        finalDataObject.put("taskLog", getOrderedTasksByDateJson());

        return finalDataObject.toString();
    }

    public Map<String, Set<String>> getOrderedTasksByDateJson() {
        Map<LocalDate, Set<String>> sortedTasks = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertir y ordenar
        taskLog.forEach((dateString, tasks) -> {
            LocalDate date = LocalDate.parse(dateString, formatter);
            sortedTasks.put(date, tasks);
        });

        // Convertir de vuelta a String y crear JSON
        Map<String, Set<String>> finalData = new LinkedHashMap<>();
        sortedTasks.forEach((date, tasks) -> {
            finalData.put(date.format(formatter), tasks);
        });

        return finalData;
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
        /*if (this.getPeasantProfile().getHealth() <= 0) {
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
        }*/
    }

    public boolean isPlantingSeason() {
        return DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 0 ||
                DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 3 ||
                DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 6 ||
                DateHelper.getMonthFromStringDate(getInternalCurrentDate()) == 8;
    }

    public int getDaysToWorkForOther() {
        return daysToWorkForOther;
    }

    public void setDaysToWorkForOther(int daysToWorkForOther) {
        this.daysToWorkForOther = daysToWorkForOther;
    }

    public void decreaseDaysToWorkForOther() {
        this.daysToWorkForOther = this.daysToWorkForOther - 1;
    }

    public String getPeasantFamilyHelper() {
        return peasantFamilyHelper;
    }

    public void setPeasantFamilyHelper(String peasantFamilyHelper) {
        this.peasantFamilyHelper = peasantFamilyHelper;
    }

    public String getAlias() {
        return peasantProfile.getPeasantFamilyAlias();
    }

    public synchronized String toCSV() {
        StringBuilder csvData = new StringBuilder();

        try {
            List<EmotionAxis> emotions = this.getEmotionsListCopy();
            emotions.forEach(emotion -> {
                csvData.append(getOrDefault(emotion.getCurrentValue())).append(',');
            });
        } catch (Exception e) {
            //csvData.append("NONE").append(',');
        }

        // Agregar los datos
        csvData.append(getOrDefault(peasantProfile.getMoney())).append(',')
                .append(getOrDefault(peasantProfile.getHealth())).append(',')
                .append(getOrDefault(timeLeftOnDay)).append(',')
                .append(getOrDefault(newDay)).append(',')
                .append(getOrDefault(currentSeason)).append(',')
                .append(getOrDefault(robberyAccount)).append(',')
                .append(getOrDefault(peasantProfile.getPurpose())).append(',')
                .append(getOrDefault(peasantProfile.getPeasantFamilyAffinity())).append(',')
                .append(getOrDefault(peasantProfile.getPeasantLeisureAffinity())).append(',')
                .append(getOrDefault(peasantProfile.getPeasantFriendsAffinity())).append(',')
                .append(getOrDefault(currentPeasantLeisureType)).append(',')
                .append(getOrDefault(currentResourceNeededType)).append(',')
                .append(getOrDefault(currentDay)).append(',')
                .append(getOrDefault(internalCurrentDate)).append(',')
                .append(getOrDefault(toPay)).append(',')
                .append(getOrDefault(peasantProfile.getPeasantKind())).append(',')
                .append(getOrDefault(peasantProfile.getRainfallConditions())).append(',')
                .append(getOrDefault(peasantProfile.getPeasantFamilyMinimalVital())).append(',')
                .append(getOrDefault(peasantProfile.getPeasantFamilyLandAlias())).append(',')
                .append(getOrDefault(currentPeasantActivityType)).append(',')
                .append(getOrDefault(peasantProfile.getFarmName())).append(',')
                .append(getOrDefault(peasantProfile.getLoanAmountToPay())).append(',')
                .append(getOrDefault(peasantProfile.getTools())).append(',')
                .append(getOrDefault(peasantProfile.getSeeds())).append(',')
                .append(getOrDefault(peasantProfile.getWaterAvailable())).append(',')
                .append(getOrDefault(peasantProfile.getPesticidesAvailable())).append(',')
                .append(getOrDefault(peasantProfile.getTotalHarvestedWeight())).append(',')
                .append(getOrDefault(getContractor())).append(',')
                .append(getOrDefault(getDaysToWorkForOther())).append(',')
                .append(getOrDefault(getAlias())).append(',')
                .append(getOrDefault(wpsStart.EMOTIONS)).append(',')
                .append(getOrDefault(getPeasantFamilyHelper())).append(',')
                .append(getOrDefault(isHaveEmotions()));

        //csvData.append('\n');
        return csvData.toString();
    }
    private String getOrDefault(Object value) {
        if (value == null) {
            return "NONE";
        }else if (value == "")  {
            return "NONE";
        }else {
            return value.toString();
        }
    }

    public void setWait(boolean waitStatus) {
        this.wait = waitStatus;
    }
    public boolean isWaiting() {
        return this.wait;
    }
}

