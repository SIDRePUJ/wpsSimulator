package org.wpsim.World.layer.crop;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.wpsim.Control.Data.DateHelper;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Automata.layer.GenericWorldLayer;
import org.wpsim.World.Automata.layer.LayerExecutionParams;
import org.wpsim.World.layer.LayerFunctionParams;
import org.wpsim.World.layer.crop.cell.CropCell;
import org.wpsim.World.layer.crop.cell.CropCellState;
import org.wpsim.World.layer.crop.cell.action.CropCellAction;
import org.wpsim.World.layer.crop.cell.action.CropCellActionType;
import org.wpsim.World.layer.evapotranspiration.EvapotranspirationLayer;
import org.wpsim.World.layer.rainfall.RainfallLayer;
import org.wpsim.World.layer.shortWaveRadiation.ShortWaveRadiationLayer;
import org.wpsim.World.layer.temperature.TemperatureCellState;
import org.wpsim.World.layer.temperature.TemperatureLayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Crop layer concrete implementation
 */
public class CropLayer extends GenericWorldLayer {

    //private static final Logger logger = LogManager.getLogger(CropLayer.class);
    private HashMap<String, CropCell> cropCellMap = new HashMap<>();
    private WorldConfiguration config = WorldConfiguration.getPropsInstance();

    @Override
    public void setupLayer() {
    }

    @Override
    public void executeLayer() {
        throw new RuntimeException("Method not implemented");
    }

    @Override
    public void executeLayer(LayerExecutionParams params) {
        LayerFunctionParams paramsLayer = (LayerFunctionParams) params;
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern(this.config.getProperty("date.format"));
        TemperatureLayer temperatureLayer = (TemperatureLayer) this.dependantLayers.get("temperature");
        EvapotranspirationLayer evapotranspirationLayer = (EvapotranspirationLayer) this.dependantLayers.get("evapotranspiration");
        ShortWaveRadiationLayer shortWaveRadiationLayer = (ShortWaveRadiationLayer) this.dependantLayers.get("radiation");
        RainfallLayer rainfallLayer = (RainfallLayer) this.dependantLayers.get("rainfall");
        this.cropCellMap.values().parallelStream().filter(CropCell::isActive).forEach(currentCell -> {
            CropCellState currentState = (CropCellState) currentCell.getCellState();
            double diseaseDamageCropFactor = Double.parseDouble(this.config.getProperty("disease.damagesCrop"));
            // First event in the simulation, firs state calculation
            if (currentState == null) {
                CropCellState newCellState = new CropCellState();
                double evapotranspiration = currentCell.getCropFactor_ini() * (evapotranspirationLayer.getCell().getCellStateByDate(paramsLayer.getDate())).getEvapotranspirationReference();
                newCellState.setEvapotranspiration(evapotranspiration);
                newCellState.setAboveGroundBiomass(0);
                newCellState.setGrowingDegreeDays(((TemperatureCellState) temperatureLayer.getCell().getCellStateByDate(paramsLayer.getDate())).getTemperature());
                newCellState.setCumulatedEvapotranspiration(evapotranspiration);
                newCellState.setRootZoneDepletionAtTheEndOfDay(currentCell.getReadilyAvailableWater());
                currentCell.setCellState(paramsLayer.getDate(), newCellState);
            }
            // Rest of the events for the simulation
            else {
                int daysBetweenLastDataAndEvent = DateHelper.differenceDaysBetweenTwoDates(currentCell.getDate(), paramsLayer.getDate());
                for (int i = 0; i < daysBetweenLastDataAndEvent; i++) {
                    CropCellState previousState = (CropCellState) currentCell.getCellState();
                    DateTime previousStateDate = DateHelper.getDateInJoda(currentCell.getDate());
                    DateTime newStateDate = previousStateDate.plusDays(1);
                    String newDate = dtfOut.print(newStateDate);
                    CropCellState newCellState = new CropCellState();
                    newCellState.setGrowingDegreeDays(previousState.getGrowingDegreeDays() + ((TemperatureCellState) temperatureLayer.getCell().getCellStateByDate(newDate)).getTemperature());
                    //------------------------------- growing degree days and k_c logic ----------------------------------------

                    double getShortWaveRadiationForDate = shortWaveRadiationLayer.getCell().getCellStateByDate(newDate).getShortWaveRadiation();
                    double evapotranspirationForDate = evapotranspirationLayer.getCell().getCellStateByDate(newDate).getEvapotranspirationReference();
                    double rainfallForDate = rainfallLayer.getCell().getCellStateByDate(newDate).getRainfall();
                    if (newCellState.getGrowingDegreeDays() < currentCell.getDegreeDays_mid()) {
                        double cropEvapotranspirationStandard = currentCell.getCropFactor_ini() * evapotranspirationForDate;
                        setCropEvapotranspiration(
                                currentCell,
                                previousState,
                                newDate,
                                newCellState,
                                diseaseDamageCropFactor,
                                getShortWaveRadiationForDate,
                                rainfallForDate,
                                cropEvapotranspirationStandard,
                                evapotranspirationForDate
                        );
                    } else if (newCellState.getGrowingDegreeDays() >= currentCell.getDegreeDays_mid() && newCellState.getGrowingDegreeDays() < currentCell.getDegreeDays_end()) {
                        double cropEvapotranspirationStandard = currentCell.getCropFactor_mid() * evapotranspirationForDate;
                        setCropEvapotranspiration(
                                currentCell,
                                previousState,
                                newDate,
                                newCellState,
                                diseaseDamageCropFactor,
                                getShortWaveRadiationForDate,
                                rainfallForDate,
                                cropEvapotranspirationStandard,
                                evapotranspirationForDate
                        );
                    } else {
                        //------------------------------------ alert peasant crop is ready to collect ---------------------------------------------------
                        if (newCellState.getGrowingDegreeDays() > currentCell.getDegreeDays_end()) {
                            currentCell.setCellState(newDate, previousState);
                            currentCell.setHarvestReady(true);
                        } else {
                            double cropEvapotranspirationStandard = currentCell.getCropFactor_end() * evapotranspirationForDate;
                            setCropEvapotranspiration(
                                    currentCell,
                                    previousState,
                                    newDate,
                                    newCellState,
                                    diseaseDamageCropFactor,
                                    getShortWaveRadiationForDate,
                                    rainfallForDate,
                                    cropEvapotranspirationStandard,
                                    evapotranspirationForDate
                            );
                        }
                    }
                }
            }
        });
    }

    private void setCropEvapotranspiration(CropCell currentCell,
                                           CropCellState previousState,
                                           String newDate,
                                           CropCellState newCellState,
                                           double diseaseDamageCropFactor,
                                           double getShortWaveRadiationForDate,
                                           double rainfallForDate,
                                           double cropEvapotranspirationStandard,
                                           double evapotranspirationReference) {
        double maximumRadiationEfficiency = Double.parseDouble(this.config.getProperty("agb.maximumRadiationEfficiency"));
        double agbConversionFactor = Double.parseDouble(this.config.getProperty("agb.conversionFactor"));
        boolean isEnabledWaterStress = Boolean.parseBoolean(this.config.getProperty("waterStress.enabled"));
        double depletionFractionAdjusted = this.calculateDepletionFractionAdjusted(currentCell.getDepletionFraction(), cropEvapotranspirationStandard);
        newCellState.setDepletionFractionAdjusted(depletionFractionAdjusted);

        double cropEvapotranspirationAndWaterStress = isEnabledWaterStress ? this.calculateWaterStressEvapotranspiration(previousState, newCellState, currentCell, depletionFractionAdjusted, rainfallForDate, cropEvapotranspirationStandard) : cropEvapotranspirationStandard;
        if (currentCell.getDiseaseCell().getCellStateByDate(newDate).isInfected()) {
            cropEvapotranspirationAndWaterStress = cropEvapotranspirationAndWaterStress - cropEvapotranspirationAndWaterStress * diseaseDamageCropFactor;
        }
        newCellState.setEvapotranspiration(
                cropEvapotranspirationAndWaterStress
        );
        newCellState.setCumulatedEvapotranspiration(previousState.getCumulatedEvapotranspiration() + cropEvapotranspirationAndWaterStress);

        newCellState.setAboveGroundBiomass(
                previousState.getAboveGroundBiomass() +
                        maximumRadiationEfficiency * (newCellState.getEvapotranspiration() / evapotranspirationReference) * getShortWaveRadiationForDate * agbConversionFactor);
        currentCell.setCellState(newDate, newCellState);
    }

    // Equation extracted from https://www.fao.org/3/x0490e/x0490e0e.htm#total%20available%20water%20(taw) A numerical approximation for adjusting p for ETc rate is p = pTable 22 + 0.04 (5 - ETc) where the adjusted p is limited to 0.1 £ p £ 0.8 and, ETc is in mm/day.
    private double calculateDepletionFractionAdjusted(double depletionFraction, double cropEvapotranspiration) {
        return depletionFraction + 0.04 * (5 - cropEvapotranspiration);
    }

    private double calculateWaterStressEvapotranspiration(CropCellState previousState, CropCellState newState, CropCell currentCell, double depletionFraction, double rainfallForDate, double cropEvapotranspiration) {
        //-------------------------------- water stress k_s -------------------------------------------------
        double depletionRootZoneStart = previousState.getRootZoneDepletionAtTheEndOfDay();
        double k_s = 1;
        if (depletionRootZoneStart > currentCell.getReadilyAvailableWater()) {
            k_s = ((currentCell.getTotalAvailableWater() - depletionRootZoneStart) / ((1 - depletionFraction) * currentCell.getTotalAvailableWater()));
            newState.setWaterStress(true);
        } else {
            newState.setWaterStress(false);
        }
        double newCropEvapotranspiration = cropEvapotranspiration * k_s;
        // Calculate new root zone depletion for the end of the day based from the soil water balance - from: https://www.fao.org/3/x0490e/x0490e0e.htm#total%20available%20water%20(taw) equation 85
        double rootZoneDepletionEndOfDay = (rainfallForDate > depletionRootZoneStart) ? currentCell.getReadilyAvailableWater() : depletionRootZoneStart - rainfallForDate - this.sumCurrentIrrigationEvents(currentCell) + newCropEvapotranspiration;
        newState.setRootZoneDepletionAtTheEndOfDay(rootZoneDepletionEndOfDay);
        return newCropEvapotranspiration;
    }

    private double sumCurrentIrrigationEvents(CropCell currentCell) {
        List<CropCellAction> nonIrrigationEvents = new ArrayList<>();
        double allIrrigation = 0;
        for (Object cropCellActionObject : currentCell.getCellActions()) {
            CropCellAction cropCellAction = (CropCellAction) cropCellActionObject;
            if (cropCellAction.getActionType() == CropCellActionType.IRRIGATION) {
                allIrrigation += Double.parseDouble(cropCellAction.getPayload());
            } else {
                nonIrrigationEvents.add(cropCellAction);
            }
        }
        currentCell.setCellActions(nonIrrigationEvents);
        return allIrrigation;
    }

    /**
     * @param crop
     */
    public void addCrop(CropCell crop) {
        this.cropCellMap.put(crop.getId(), crop);
    }

    /**
     * @param id
     * @return
     */
    public CropCellState getCropStateById(String id) {
        return (CropCellState) this.cropCellMap.get(id).getCellState();
    }

    public CropCellState getCropState() {
        return this.cropCellMap.values().stream().findFirst().map(
                cropCell -> (CropCellState) cropCell.getCellState()
        ).orElse(null);
    }

    /**
     * @param id
     * @return
     */
    public CropCell getCropCell() {
        return this.cropCellMap.values().stream().findFirst().orElse(null);
    }
    public CropCell getCropCellById(String id) {
        return this.cropCellMap.get(id);
    }

    /**
     * @param waterQuantity
     * @param date
     */
    public void addIrrigationEvent(String waterQuantity, String date) {
        CropCellAction cropCellAction = new CropCellAction(CropCellActionType.IRRIGATION, waterQuantity, date);
        cropCellMap.forEach((key, value) -> {
            //System.out.println(key + " " + value);
            value.addCellAction(cropCellAction);
        });
        //CropCell cropCell = this.cropCellMap.get(cropId);
        //cropCell.addCellAction(cropCellAction);
    }

    /**
     *
     */
    public void writeCropData() {
        String fileDirection = this.config.getProperty("crop.dataFiles");
        for (CropCell cropCell : this.cropCellMap.values()) {
            String diseaseEnabled = this.config.isDiseasePerturbation() ? "_disease_" : "";
            String waterStressEnabled = Boolean.parseBoolean(this.config.getProperty("waterStress.enabled")) ? "_water_stress_" : "";
            String filename = fileDirection + cropCell.getId() + diseaseEnabled + waterStressEnabled + ".csv";
            List<String[]> dataLines = new ArrayList<>();
            dataLines.add(new String[]{"date", "disease", "evapotranspiration", "agb", "cumulatedTemperature", "cumulatedEvapotranspiration"});
            cropCell.getHistoricalData().keySet().stream().forEach(dateKey -> {
                CropCellState stateCell = (CropCellState) cropCell.getHistoricalData().get(dateKey);
                dataLines.add(new String[]{
                        dateKey.toString(),
                        cropCell.getDiseaseCell().getHistoricalData().get(dateKey).isInfected() ? "true" : "false",
                        stateCell.getEvapotranspiration() + "",
                        stateCell.getAboveGroundBiomass() + "",
                        stateCell.getGrowingDegreeDays() + "",
                        stateCell.getCumulatedEvapotranspiration() + ""
                });
            });
            File csvOutputFile = new File(filename);
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                dataLines.stream()
                        .map(this::convertToCSV)
                        .forEach(pw::println);
            } catch (FileNotFoundException fileNotFoundException) {
                wpsReport.error("File not Found!!!", "CropLayer");
            }
        }
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * @return
     */
    public List<CropCell> getAllCrops() {
        //wpsReport.debug(this.cropCellMap.values());
        return (List<CropCell>) new ArrayList<CropCell>(this.cropCellMap.values());
    }
}
