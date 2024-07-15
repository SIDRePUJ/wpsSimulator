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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.WellProdSim.Base.wpsTask;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.AgroEcosystem.Guards.AgroEcosystemGuard;
import org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessage;
import rational.mapping.Believes;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;

import static org.wpsim.AgroEcosystem.Messages.AgroEcosystemMessageType.CROP_PESTICIDE;

/**
 *
 * @author jairo
 */
public class ManagePestsTask extends wpsTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        this.setExecuted(false);
        //wpsReport.info("⚙️⚙️⚙️");
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.ManagePestsTask.getTime());
        //believes.setCurrentCropCare(CropCareType.NONE);

        try {
            AdmBESA.getInstance().getHandlerByAlias(
                    believes.getAlias()
            ).sendEvent(
                    new EventBESA(
                            AgroEcosystemGuard.class.getName(),
                            new AgroEcosystemMessage(
                                    CROP_PESTICIDE,
                                    "rice", // @TODO: CAMBIAR NOMBRE AL REAL
                                    believes.getInternalCurrentDate(),
                                    believes.getPeasantProfile().getPeasantFamilyAlias()
                            )
                    )
            );
            ControlCurrentDate.getInstance().setCurrentDate(
                    believes.getInternalCurrentDate()
            );
            //this.setTaskWaitingForExecution();

        } catch (ExceptionBESA ex) {
            wpsReport.error(ex, believes.getPeasantProfile().getPeasantFamilyAlias());
        }
    }

}
