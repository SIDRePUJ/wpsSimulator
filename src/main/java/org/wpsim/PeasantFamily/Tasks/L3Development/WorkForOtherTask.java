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
import BESA.Kernel.Agent.StructBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.PeasantFamilyProfile;
import org.wpsim.PeasantFamily.Data.SeasonType;
import org.wpsim.PeasantFamily.Data.TimeConsumedBy;
import org.wpsim.Simulator.wpsStart;
import org.wpsim.Viewer.Data.wpsReport;
import org.wpsim.World.Agent.WorldAgent;
import org.wpsim.World.Agent.WorldGuard;
import org.wpsim.World.Agent.WorldState;
import org.wpsim.World.Helper.Hemisphere;
import org.wpsim.World.Helper.Soil;
import org.wpsim.World.Helper.WorldConfiguration;
import org.wpsim.World.Messages.WorldMessage;
import org.wpsim.World.Messages.WorldMessageType;
import org.wpsim.World.layer.crop.CropLayer;
import org.wpsim.World.layer.crop.cell.rice.RiceCell;
import org.wpsim.World.layer.disease.DiseaseCell;
import org.wpsim.World.layer.disease.DiseaseLayer;
import org.wpsim.World.layer.evapotranspiration.EvapotranspirationLayer;
import org.wpsim.World.layer.rainfall.RainfallLayer;
import org.wpsim.World.layer.shortWaveRadiation.ShortWaveRadiationLayer;
import org.wpsim.World.layer.temperature.TemperatureLayer;
import rational.mapping.Believes;
import rational.mapping.Task;

import static org.wpsim.World.Messages.WorldMessageType.CROP_INIT;

/**
 *
 * @author jairo
 */
public class WorkForOtherTask extends Task {

    private boolean finished;

    /**
     *
     */
    public WorkForOtherTask() {
        finished = false;
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        finished = false;
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        believes.discountDaysToWorkForOther();
        believes.addTaskToLog(believes.getInternalCurrentDate());
        finished = true;
    }


    /**
     *
     * @param parameters
     */
    @Override
    public void interruptTask(Believes parameters) {
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void cancelTask(Believes parameters) {
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public boolean checkFinish(Believes parameters) {
        return finished;
    }

}
