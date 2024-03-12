package org.wpsim.SimulationControl.Data;

import java.util.Random;

public class Coin {
    public static boolean flipCoin() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
