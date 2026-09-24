package org.wpsim.WellProdSim.Util;

/**
 * Interruptores para reproducir el comportamiento de la versión enviada (8364c4f)
 * en cada corrección de la revisión TCSS. Se activan con -Dwps.legacyX=1.
 * Sirven para la escalera de conciliación original -> corregido; no se usan en producción.
 */
public final class Legacy {
    public static final boolean WATER = flag("wps.legacyWater");
    public static final boolean FACTOR = flag("wps.legacyFactor");
    public static final boolean FORGET = flag("wps.legacyForget");
    public static final boolean CLAMP = flag("wps.legacyClamp");
    public static final boolean FAO = flag("wps.legacyFao");
    public static final boolean LEISURE = flag("wps.legacyLeisure");

    private Legacy() {
    }

    private static boolean flag(String key) {
        String v = System.getProperty(key);
        return v != null && !v.equals("0") && !v.equalsIgnoreCase("false");
    }

    public static String describe() {
        return "water=" + WATER + " factor=" + FACTOR + " forget=" + FORGET + " clamp=" + CLAMP + " fao=" + FAO + " leisure=" + LEISURE;
    }
}
