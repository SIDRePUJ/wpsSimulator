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
package org.wpsim.PeasantFamily.Guards.FromWorld;

import BESA.BDI.AgentStructuralModel.StateBDI;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.json.JSONObject;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.CropCareType;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.Viewer.wpsReport;

/**
 *
 * @author jairo
 */
public class FromWorldGuard extends GuardBESA {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        FromWorldMessage peasantCommMessage = (FromWorldMessage) event.getData();
        //wpsReport.warn("🤖🤖🤖 Recibido: " + peasantCommMessage.getPeasantAlias() + " getType=" + peasantCommMessage.getPayload());
        StateBDI state = (StateBDI) this.agent.getState();
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) state.getBelieves();
        FromWorldMessageType messageType = peasantCommMessage.getMessageType();
        //wpsReport.info("🍙🍙🍙: " + peasantCommMessage.getPayload() + ":🍙🍙🍙");
        try {

            switch (messageType) {
                case NOTIFY_CROP_DISEASE:
                    believes.getPeasantProfile().setCropHealth(0.5);
                    //wpsReport.info("🍙🍙🍙: NOTIFY_CROP_DISEASE");
                    break;
                case CROP_PESTICIDE:
                    believes.setCurrentCropCare(CropCareType.PESTICIDE);
                    //wpsReport.info("🍙🍙🍙: CROP_PESTICIDE");
                    break;
                case NOTIFY_CROP_WATER_STRESS:
                    believes.setCurrentCropCare(CropCareType.IRRIGATION);
                    wpsReport.info("🍙🍙🍙: NOTIFY_CROP_WATER_STRESS", this.getAgent().getAlias());
                    break;
                case CROP_INFORMATION_NOTIFICATION:
                    //believes.getProfile().setPesticideSeason(true);
                    //wpsReport.info("🍙🍙🍙: CROP_INFORMATION_NOTIFICATION");
                    break;
                case NOTIFY_CROP_READY_HARVEST:
                    believes.setCurrentSeason(SeasonType.HARVEST);
                    //wpsReport.info("🍙🍙🍙: NOTIFY_CROP_READY_HARVEST");
                    break;
                case REQUEST_CROP_INFORMATION:
                    //wpsReport.info("🍙🍙🍙: " + peasantCommMessage.getPayload());
                    break;
                case CROP_INIT:
                    //believes.getProfile().setPlantingSeason(true);
                    break;
                case CROP_HARVEST:
                    wpsReport.info("🍙🍙🍙: CROP_HARVEST OK", this.getAgent().getAlias());
                    JSONObject cropData = new JSONObject(
                            peasantCommMessage.getPayload()
                    );
                    wpsReport.warn(cropData, this.getAgent().getAlias());
                    believes.getPeasantProfile().setHarvestedWeight(
                            (int) Math.round(
                                    Double.parseDouble(
                                            cropData.get("aboveGroundBiomass").toString()
                                    )
                            )
                    );
                    believes.getPeasantProfile().increaseTotalHarvestedWeight(
                            Double.parseDouble(
                                    cropData.get("aboveGroundBiomass").toString()
                            )
                    );
                    break;
                default:
                    // Código a ejecutar si messageType no coincide con ninguno de los casos anteriores
                    break;
            }
        } catch (IllegalArgumentException e) {
            wpsReport.warn("error?" + e, this.getAgent().getAlias());
        }

    }

}
