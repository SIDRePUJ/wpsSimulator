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
package org.wpsim.PeasantFamily.Guards.FromCivicAuthority;

import BESA.Kernel.Agent.Event.DataBESA;

import java.util.Map;

/**
 *
 * @author jairo
 */
public class FromCivicAuthorityMessage extends DataBESA {

    Map<String, String> assignedLands;
    private String landName;

    /**
     * Constructor.
     * @param landName
     */
    public FromCivicAuthorityMessage(String landName, Map<String, String> assignedLands) {
        this.setLandName(landName);
        this.setAssignedLands(assignedLands);
    }

    public Map<String, String> getAssignedLands() {
        return assignedLands;
    }

    public void setAssignedLands(Map<String, String> assignedLands) {
        this.assignedLands = assignedLands;
    }

    public String getLandName() {
        return landName;
    }
    public void setLandName(String landName) {
        this.landName = landName;
    }

}
