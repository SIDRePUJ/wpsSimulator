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
package org.wpsim.CivicAuthority.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.GuardBESA;
import BESA.Kernel.System.AdmBESA;
import BESA.Kernel.System.Directory.AgHandlerBESA;
import org.wpsim.CivicAuthority.Data.CivicAuthorityState;
import org.wpsim.CivicAuthority.Data.CivicAuthorityLandData;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentGuard;
import org.wpsim.PeasantFamily.Guards.FromGovernment.FromGovernmentMessage;

import java.util.Map;

/**
 *
 * @author jairo
 */
public class CivicAuthorityReleaseLandGuard extends GuardBESA  {

    /**
     *
     * @param event
     */
    @Override
    public void funcExecGuard(EventBESA event) {
        CivicAuthorityLandData data = (CivicAuthorityLandData) event.getData();
        CivicAuthorityState state = (CivicAuthorityState) this.getAgent().getState();

        Map.Entry<String, Map<String, String>> assignedFarmData = state.assignLandToFamily(data.getFamilyName());
        String assignedFarmName = assignedFarmData != null ? assignedFarmData.getKey() : null;
        Map<String, String> assignedLands = assignedFarmData != null ? assignedFarmData.getValue() : null;

        try {
            //System.out.println("peaasant family: " + data.getFamilyName() + " Assigned farm: " + assignedFarmName + " Assigned lands: " + assignedLands);
            AgHandlerBESA ah = AdmBESA.getInstance().getHandlerByAlias(data.getFamilyName());
            FromGovernmentMessage fromGovernmentMessage = new FromGovernmentMessage(assignedFarmName, assignedLands);
            EventBESA ev = new EventBESA(FromGovernmentGuard.class.getName(), fromGovernmentMessage);
            ah.sendEvent(ev);
        } catch (ExceptionBESA ex) {
            System.out.println(ex);
        }
    }

}
