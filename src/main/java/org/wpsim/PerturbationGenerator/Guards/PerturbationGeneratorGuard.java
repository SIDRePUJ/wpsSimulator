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
package org.wpsim.PerturbationGenerator.Guards;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import org.wpsim.ViewerLens.Util.wpsReport;
import org.wpsim.WellProdSim.Util.wpsCSV;

/**
 *
 * @author jairo
 */
public class PerturbationGeneratorGuard extends GuardBESA  {
    public PerturbationGeneratorGuard() {
        super();
        wpsCSV.log("PerturbationGeneratorGuard", "Agent,CurrentDate,Action,Response");
    }

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        wpsReport.debug("Contacto con perturbación desde  " + event.getSource(), this.getAgent().getAlias());
    }
    
}
