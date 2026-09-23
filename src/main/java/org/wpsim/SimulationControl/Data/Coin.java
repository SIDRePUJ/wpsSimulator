package org.wpsim.SimulationControl.Data;

import java.util.Random;

public class Coin {
    public static boolean flipCoin() {
        Random random = org.wpsim.WellProdSim.Util.SimRandom.get();
        return random.nextBoolean();
    }
}
