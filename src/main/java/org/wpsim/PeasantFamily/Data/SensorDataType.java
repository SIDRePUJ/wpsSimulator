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
package org.wpsim.PeasantFamily.Data;

/**
 *
 * @author jairo
 */
public enum SensorDataType {

    /**
     *
     */
    ACTIVITY("activity"),

    /**
     *
     */
    EMOTIONS("emotions"),

    /**
     *
     */
    INTERACTION("interaction"),

    /**
     *
     */
    PEASANT("peasant"),

    /**
     *
     */
    PURPOSE("purpose");
    private final String identif;

    private SensorDataType(String i) {
        identif = i;
    }

    /**
     *
     * @param ident
     * @return
     */
    public static SensorDataType getFromId(String ident) {
        SensorDataType ret = null;
        for (SensorDataType sdt : values()) {
            if (sdt.identif.equals(ident)) {
                ret = sdt;
                break;
            }
        }
        return ret;
    }
}
