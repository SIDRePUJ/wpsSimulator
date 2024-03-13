package org.wpsim.PerturbationGenerator.Guards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.MarketPlace.Data.MarketMessage;
import org.wpsim.MarketPlace.Data.MarketMessageType;
import org.wpsim.MarketPlace.Guards.MarketInfoAgentGuard;
import org.wpsim.Simulator.Config.wpsConfig;
import org.wpsim.Simulator.wpsStart;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NaturalPhenomena extends PeriodicGuardBESA {
    @Override
    public void funcPeriodicExecGuard(EventBESA eventBESA) {
        if (Math.random() < wpsConfig.getInstance().getDoubleProperty("perturbation.probability.value")) {
            try {
                AdmBESA.getInstance().getHandlerByAlias(
                        wpsStart.config.getMarketAgentName()
                ).sendEvent(
                        new EventBESA(
                                MarketInfoAgentGuard.class.getName(),
                                new MarketMessage(
                                        selectRandomIncreaseOrDecrease(),
                                        selectRandomNumber(),
                                        ControlCurrentDate.getInstance().getCurrentDate()
                                )
                        )
                );
            } catch (ExceptionBESA e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static MarketMessageType selectRandomIncreaseOrDecrease() {
        List<MarketMessageType> increaseDecreaseMessages = Arrays.asList(
                MarketMessageType.INCREASE_TOOS_PRICE,
                MarketMessageType.INCREASE_SEEDS_PRICE,
                MarketMessageType.INCREASE_CROP_PRICE,
                MarketMessageType.DECREASE_TOOLS_PRICE,
                MarketMessageType.DECREASE_SEEDS_PRICE,
                MarketMessageType.DECREASE_CROP_PRICE
        );
        Random random = new Random();
        int index = random.nextInt(increaseDecreaseMessages.size());
        return increaseDecreaseMessages.get(index);
    }
    public static int selectRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(16);
        return 5 + (randomNumber * 5);
    }
}
