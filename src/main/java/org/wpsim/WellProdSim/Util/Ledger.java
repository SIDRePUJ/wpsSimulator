package org.wpsim.WellProdSim.Util;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * Diagnóstico de flujos de dinero (revisión TCSS). Con -Dwps.ledger=1 registra cada
 * ingreso y gasto de las familias según la clase que lo origina y, al terminar la
 * simulación, escribe ledger.csv (kind,source,total,count) en el directorio de trabajo.
 */
public final class Ledger {
    public static final boolean ON = flag("wps.ledger");
    private static final Map<String, DoubleAdder> SUM = new ConcurrentHashMap<>();
    private static final Map<String, LongAdder> COUNT = new ConcurrentHashMap<>();
    private static final StackWalker WALKER = StackWalker.getInstance();

    static {
        if (ON) {
            Runtime.getRuntime().addShutdownHook(new Thread(Ledger::dump));
        }
    }

    private Ledger() {
    }

    private static boolean flag(String key) {
        String v = System.getProperty(key);
        return v != null && !v.equals("0") && !v.equalsIgnoreCase("false");
    }

    public static void record(String kind, double amount) {
        if (!ON || amount == 0) {
            return;
        }
        String caller = WALKER.walk(s -> s.map(StackWalker.StackFrame::getClassName)
                .filter(c -> !c.endsWith(".PeasantFamilyProfile") && !c.endsWith(".Ledger"))
                .findFirst().orElse("?"));
        String key = kind + "," + caller.substring(caller.lastIndexOf('.') + 1);
        SUM.computeIfAbsent(key, k -> new DoubleAdder()).add(amount);
        COUNT.computeIfAbsent(key, k -> new LongAdder()).increment();
    }

    private static void dump() {
        try (PrintWriter w = new PrintWriter("ledger.csv")) {
            w.println("kind,source,total,count");
            for (Map.Entry<String, DoubleAdder> e : new TreeMap<>(SUM).entrySet()) {
                w.println(e.getKey() + "," + Math.round(e.getValue().sum()) + "," + COUNT.get(e.getKey()).sum());
            }
        } catch (Exception ex) {
            System.err.println("Ledger: " + ex.getMessage());
        }
    }
}
