package org.wpsim.WellProdSim.Util;

import java.util.Random;

/**
 * Generador aleatorio central de la simulación (revisión TCSS, R2.8).
 * La semilla se fija con la opción -seed y se registra en stdout. Como cada agente
 * corre en su propio hilo, dos corridas con la misma semilla no son idénticas bit a
 * bit; lo que se garantiza es que cada réplica usa una semilla propia y registrada.
 */
public final class SimRandom {
    private static volatile long seed = System.nanoTime();
    private static volatile Random rng = new Random(seed);

    private SimRandom() {
    }

    public static synchronized void setSeed(long s) {
        seed = s;
        rng = new Random(s);
    }

    public static long getSeed() {
        return seed;
    }

    public static Random get() {
        return rng;
    }

    public static double nextDouble() {
        return rng.nextDouble();
    }
}
