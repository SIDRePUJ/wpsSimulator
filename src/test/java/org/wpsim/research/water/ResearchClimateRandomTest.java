package org.wpsim.research.water;

import java.util.Random;

public final class ResearchClimateRandomTest {
    private ResearchClimateRandomTest() {
    }

    public static void main(String[] args) {
        Random reference = ResearchClimateRandom.stream(12345L, "land_5_2", "rainfall");
        Random same = ResearchClimateRandom.stream(12345L, "land_5_2", "rainfall");
        Random otherPlot = ResearchClimateRandom.stream(12345L, "land_6_2", "rainfall");
        Random otherLayer = ResearchClimateRandom.stream(12345L, "land_5_2", "temperature");
        Random otherSeed = ResearchClimateRandom.stream(54321L, "land_5_2", "rainfall");
        for (int i = 0; i < 100; i++) {
            assert reference.nextLong() == same.nextLong();
        }
        long first = ResearchClimateRandom.stream(12345L, "land_5_2", "rainfall").nextLong();
        assert first != otherPlot.nextLong();
        assert first != otherLayer.nextLong();
        assert first != otherSeed.nextLong();
        Random isolated = ResearchClimateRandom.stream(12345L, "land_5_2", "rainfall");
        Random noisy = ResearchClimateRandom.stream(12345L, "land_6_2", "rainfall");
        for (int i = 0; i < 100; i++) {
            noisy.nextLong();
        }
        assert isolated.nextLong() == first;
        expectInvalid("", "rainfall");
        expectInvalid("land_5_2", "");
        System.out.println("ResearchClimateRandomTest PASS");
    }

    private static void expectInvalid(String plotId, String layerName) {
        try {
            ResearchClimateRandom.stream(1L, plotId, layerName);
            throw new AssertionError("Expected invalid stream key");
        } catch (IllegalArgumentException expected) {
            // Expected: an empty key could accidentally couple different streams.
        }
    }
}
