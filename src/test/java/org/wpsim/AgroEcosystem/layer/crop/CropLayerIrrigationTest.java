package org.wpsim.AgroEcosystem.layer.crop;

import org.wpsim.AgroEcosystem.Helper.Soil;
import org.wpsim.AgroEcosystem.Helper.Hemisphere;
import org.wpsim.AgroEcosystem.layer.LayerFunctionParams;
import org.wpsim.AgroEcosystem.layer.crop.cell.rice.RiceCell;
import org.wpsim.AgroEcosystem.layer.disease.DiseaseCell;
import org.wpsim.AgroEcosystem.layer.disease.DiseaseCellState;
import org.wpsim.AgroEcosystem.layer.evapotranspiration.EvapotranspirationCellState;
import org.wpsim.AgroEcosystem.layer.evapotranspiration.EvapotranspirationLayer;
import org.wpsim.AgroEcosystem.layer.rainfall.RainfallCellState;
import org.wpsim.AgroEcosystem.layer.rainfall.RainfallLayer;
import org.wpsim.AgroEcosystem.layer.shortWaveRadiation.ShortWaveRadiationCellState;
import org.wpsim.AgroEcosystem.layer.shortWaveRadiation.ShortWaveRadiationLayer;
import org.wpsim.AgroEcosystem.layer.temperature.TemperatureCellState;
import org.wpsim.AgroEcosystem.layer.temperature.TemperatureLayer;
import org.wpsim.research.water.PhysicalIrrigationPlan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Focused check that physical depth is targeted while the default legacy API is unchanged. */
public final class CropLayerIrrigationTest {
    public static void main(String[] args) throws IOException {
        CropLayer layer = new CropLayer();
        RiceCell first = crop("first");
        RiceCell second = crop("second");
        layer.addCrop(first);
        layer.addCrop(second);

        layer.addIrrigationEventToCrop("first", 12.5, "02/01/2020");
        if (first.getCellActions().size() != 1 || !second.getCellActions().isEmpty()) {
            throw new AssertionError("physical irrigation reached the wrong crop");
        }
        expectFailure(() -> layer.addIrrigationEventToCrop("missing", 1, "02/01/2020"));
        expectFailure(() -> layer.addIrrigationEventToCrop("first", Double.NaN, "02/01/2020"));

        layer.addIrrigationEvent("33", "02/01/2020");
        if (first.getCellActions().size() != 2 || second.getCellActions().size() != 1) {
            throw new AssertionError("legacy broadcast changed");
        }
        checkScheduledWaterBalance();
        System.out.println("CropLayerIrrigationTest PASS");
    }

    private static void checkScheduledWaterBalance() throws IOException {
        Path requests = Files.createTempFile("crop-irrigation-", ".csv");
        try {
            Files.writeString(requests, "date,plot_id,area_ha,net_demand_mm,delivery_efficiency\n"
                    + "02/01/2020,plot,1,10,1\n");
            System.setProperty("wps.water.requests", requests.toString());
            System.setProperty("wps.water.sourceM3", "100");
            System.setProperty("wps.water.rule", "PROPORTIONAL_DEMAND");

            Path data = Path.of("src/main/resources/data/mariaLaBaja").toAbsolutePath();
            TemperatureLayer temperature = new TemperatureLayer(data.resolve("temperature.json").toString());
            EvapotranspirationLayer et = new EvapotranspirationLayer(data.resolve("evapotranspiration.json").toString());
            ShortWaveRadiationLayer radiation = new ShortWaveRadiationLayer(
                    data.resolve("radiation.json").toString(), Hemisphere.NORTHERN, 10);
            RainfallLayer rainfall = new RainfallLayer(data.resolve("rainfall.json").toString());
            for (String date : new String[]{"01/01/2020", "02/01/2020"}) {
                temperature.getCell().setCellState(date, new TemperatureCellState(20));
                et.getCell().setCellState(date, new EvapotranspirationCellState(5));
                radiation.getCell().setCellState(date, new ShortWaveRadiationCellState(20));
                rainfall.getCell().setCellState(date, new RainfallCellState(0));
            }

            CropLayer control = configuredLayer(crop("rice"), temperature, et, radiation, rainfall);
            CropLayer scheduled = configuredLayer(crop("rice"), temperature, et, radiation, rainfall);
            scheduled.enablePhysicalIrrigation("plot");
            for (String date : new String[]{"01/01/2020", "02/01/2020"}) {
                control.executeLayer(new LayerFunctionParams(date));
                scheduled.executeLayer(new LayerFunctionParams(date));
            }
            double controlDepletion = control.getCropState().getRootZoneDepletionAtTheEndOfDay();
            double scheduledDepletion = scheduled.getCropState().getRootZoneDepletionAtTheEndOfDay();
            if (Math.abs(controlDepletion - scheduledDepletion - 10) > 1e-8) {
                throw new AssertionError("scheduled net depth did not enter the crop water balance: "
                        + controlDepletion + " versus " + scheduledDepletion);
            }
            if (Math.abs(scheduled.getCropState().getCumulatedPotentialEvapotranspiration() - 10.5) > 1e-8) {
                throw new AssertionError("seasonal potential ET was not accumulated from standard crop ET");
            }
            Path audit = requests.resolveSibling(requests.getFileName() + ".audit.csv");
            try {
                PhysicalIrrigationPlan.AuditSummary summary = PhysicalIrrigationPlan.active().writeAudit(audit);
                if (!summary.valid() || summary.appliedDeliveries() != 1) {
                    throw new AssertionError("scheduled delivery did not reconcile: " + summary);
                }
            } finally {
                Files.deleteIfExists(audit);
            }
        } finally {
            System.clearProperty("wps.water.requests");
            System.clearProperty("wps.water.sourceM3");
            System.clearProperty("wps.water.rule");
            Files.deleteIfExists(requests);
        }
    }

    private static CropLayer configuredLayer(RiceCell rice, TemperatureLayer temperature,
                                             EvapotranspirationLayer et, ShortWaveRadiationLayer radiation,
                                             RainfallLayer rainfall) {
        DiseaseCellState healthy = new DiseaseCellState();
        healthy.setInfected(false);
        rice.getDiseaseCell().setCellState("02/01/2020", healthy);
        CropLayer layer = new CropLayer();
        layer.addCrop(rice);
        layer.bindLayer("temperature", temperature);
        layer.bindLayer("evapotranspiration", et);
        layer.bindLayer("radiation", radiation);
        layer.bindLayer("rainfall", rainfall);
        return layer;
    }

    private static RiceCell crop(String id) {
        return new RiceCell(1.05, 1.2, 0.7, 1512, 3330, 1, 0.9, 0.2,
                Soil.SAND, true, new DiseaseCell(id + "-disease"), id, "family");
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("expected IllegalArgumentException");
    }
}
