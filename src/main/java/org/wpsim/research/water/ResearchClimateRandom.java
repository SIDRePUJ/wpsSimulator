package org.wpsim.research.water;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/** Independent climate stream for one plot and layer in opt-in physical research runs. */
public final class ResearchClimateRandom {
    private static final long FNV_OFFSET = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    private ResearchClimateRandom() {
    }

    public static Random stream(long scenarioSeed, String plotId, String layerName) {
        if (plotId == null || plotId.isBlank() || layerName == null || layerName.isBlank()) {
            throw new IllegalArgumentException("Plot ID and climate layer name are required");
        }
        long hash = FNV_OFFSET ^ scenarioSeed;
        hash = mix(hash, plotId);
        hash = (hash ^ 0xffL) * FNV_PRIME;
        hash = mix(hash, layerName);
        return new Random(hash);
    }

    private static long mix(long hash, String value) {
        for (byte item : value.getBytes(StandardCharsets.UTF_8)) {
            hash = (hash ^ (item & 0xffL)) * FNV_PRIME;
        }
        return hash;
    }
}
