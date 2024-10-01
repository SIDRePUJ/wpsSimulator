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
package org.wpsim.PeasantFamily.Guards.FromAgroEcosystem;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Emotional.EmotionalEvent;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.json.JSONObject;
import org.wpsim.CivicAuthority.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.CropCareType;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.ViewerLens.Util.wpsReport;

import java.time.Instant;

/**
 *
 * @author jairo
 */
public class FromAgroEcosystemGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {

        FromAgroEcosystemMessage peasantCommMessage = (FromAgroEcosystemMessage) event.getData();
        StateBDI state = (StateBDI) this.agent.getState();
        String landName = peasantCommMessage.getLandName();

        long initTime = System.currentTimeMillis();
        //wpsReport.info("Llegó a FromWorldGuard: " + initTime + " " + landName, this.getAgent().getAlias());

        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) state.getBelieves();
        LandInfo landInfo = believes.getLandInfo(landName);
        landInfo.setProcessing(false);
        FromAgroEcosystemMessageType messageType = peasantCommMessage.getMessageType();

        try {

            switch (messageType) {
                case NOTIFY_CROP_DISEASE:
                    believes.getPeasantProfile().setCropHealth(0.5);
                    //wpsReport.info("🍙🍙🍙: NOTIFY_CROP_DISEASE", this.getAgent().getAlias());
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "CROPDISEASES", "CROPS"));
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "CROPDISEASES", "FOOD"));
                    break;
                case CROP_PESTICIDE:
                    believes.setCurrentCropCareType(landName, CropCareType.PESTICIDE);
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "CROPDISEASES", "CROPS"));
                    //wpsReport.info("🍙🍙🍙: CROP_PESTICIDE", this.getAgent().getAlias());
                    break;
                case NOTIFY_CROP_WATER_STRESS:
                    believes.setCurrentCropCareType(landName, CropCareType.IRRIGATION);
                    //wpsReport.info("🍙🍙🍙: NOTIFY_CROP_WATER_STRESS", this.getAgent().getAlias());
                    break;
                case CROP_INFORMATION_NOTIFICATION:
                    break;
                case NOTIFY_CROP_READY_HARVEST:
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HARVESTING", "CROPS"));
                    believes.setCurrentSeason(landName, SeasonType.HARVEST);
                    break;
                case REQUEST_CROP_INFORMATION, CROP_INIT:
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HARVESTING", "CROPS"));
                    break;
                case CROP_HARVEST:
                    //wpsReport.info("🍙🍙🍙: CROP_HARVEST OK", this.getAgent().getAlias());
                    JSONObject cropData = new JSONObject(
                            peasantCommMessage.getPayload()
                    );
                    //wpsReport.info(cropData, this.getAgent().getAlias());
                    believes.getPeasantProfile().setHarvestedWeight(
                            (int) Math.round(
                                    Math.ceil(
                                            Double.parseDouble(
                                                    cropData.get("aboveGroundBiomass").toString()
                                            ) * believes.getTrainingLevel() // Solo es aprovechable el 50% de la biomasa + 20% consumo interno
                                    )
                            )
                    );
                    believes.getPeasantProfile().increaseTotalHarvestedWeight(
                            Math.ceil(
                                    Double.parseDouble(
                                            cropData.get("aboveGroundBiomass").toString()
                                    ) * believes.getTrainingLevel() // Solo es aprovechable el 50% de la biomasa + 20% consumo interno
                            )
                    );
                    believes.processEmotionalEvent(new EmotionalEvent("FAMILY", "HARVESTING", "CROPS"));
                    //System.out.println("🍙🍙🍙: CROP_HARVEST OK");
                    wpsReport.mental(Instant.now() + "," + believes.toCSV(), believes.getAlias());
                    break;
                default:
                    // Código a ejecutar si messageType no coincide con ninguno de los casos anteriores
                    break;
            }
        } catch (Exception e) {
            wpsReport.warn("error?" + e, this.getAgent().getAlias());
        }
        //wpsReport.info("Mensaje procesado: " + (System.currentTimeMillis() - initTime) + " " + landName, this.getAgent().getAlias());
    }
}