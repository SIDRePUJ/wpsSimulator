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
package org.wpsim.PeasantFamily.Tasks.L3Development;

import org.wpsim.Government.Data.LandInfo;
import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import org.wpsim.PeasantFamily.Data.Utils.SeasonType;
import org.wpsim.PeasantFamily.Data.Utils.TimeConsumedBy;
import org.wpsim.PeasantFamily.Tasks.Base.wpsLandTask;
import org.wpsim.PeasantFamily.Tasks.Base.wpsTask;
import org.wpsim.Viewer.Data.wpsReport;
import rational.mapping.Believes;
import rational.mapping.Task;

import java.util.List;

/**
 *
 * @author jairo
 */
public class DeforestingLandTask extends wpsLandTask {

    /**
     *
     * @param parameters
     */
    @Override
    public void executeTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        updateConfig(believes, 120); // Paso 1: Configurar el tiempo de deforestación
        believes.addTaskToLog(believes.getInternalCurrentDate());
        believes.useTime(TimeConsumedBy.DeforestingLandTask);

        int factor = 1;
        if (!believes.getPeasantFamilyHelper().isBlank())
        {
            factor = 2;
        }

        // Incluir si tiene tendencias a deforestar
        List<LandInfo> landInfos = believes.getAssignedLands();
        for (LandInfo currentLandInfo : landInfos) {
            if ("forest".equals(currentLandInfo.getKind())) {
                this.increaseWorkDone(believes,currentLandInfo.getLandName(), TimeConsumedBy.DeforestingLandTask.getTime()*factor); // Paso 2: Gastar tiempo
                if (this.isWorkDone(believes, currentLandInfo.getLandName())) { // Paso 3: Revisar si se completó el tiempo necesario
                    this.resetLand(believes, currentLandInfo.getLandName()); // Paso 4: reiniciar el tiempo ok
                    currentLandInfo.setKind("land");
                    wpsReport.info(
                            "Finished the deforesting process " + currentLandInfo.getLandName(),
                            believes.getPeasantProfile().getPeasantFamilyAlias()
                    );
                }
                return; // Paso 5: Retornar a la iteración
            }
        }
    }

}