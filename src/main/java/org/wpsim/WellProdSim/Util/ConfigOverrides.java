package org.wpsim.WellProdSim.Util;

import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Revisión TCSS: sobrescribe propiedades de configuración con propiedades del sistema
 * -Dwps.cfg.&lt;clave&gt;=&lt;valor&gt;, para los análisis de sensibilidad sin editar archivos.
 */
public final class ConfigOverrides {
    public static final String PREFIX = "wps.cfg.";

    private ConfigOverrides() {
    }

    public static Map<String, String> active() {
        Map<String, String> m = new TreeMap<>();
        for (String name : System.getProperties().stringPropertyNames()) {
            if (name.startsWith(PREFIX)) {
                m.put(name.substring(PREFIX.length()), System.getProperty(name));
            }
        }
        return m;
    }

    public static void apply(Properties p) {
        for (Map.Entry<String, String> e : active().entrySet()) {
            p.setProperty(e.getKey(), e.getValue());
        }
    }
}
