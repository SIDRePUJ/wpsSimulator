package org.wpsim.PerturbationGenerator.PeriodicGuards;

import BESA.ExceptionBESA;
import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;
import BESA.Kernel.System.AdmBESA;
import org.wpsim.SimulationControl.Util.ControlCurrentDate;
import org.wpsim.MarketPlace.Data.MarketPlaceMessage;
import org.wpsim.MarketPlace.Data.MarketPlaceMessageType;
import org.wpsim.MarketPlace.Guards.MarketPlaceInfoAgentGuard;
import org.wpsim.WellProdSim.Config.wpsConfig;
import org.wpsim.WellProdSim.wpsStart;

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
                                MarketPlaceInfoAgentGuard.class.getName(),
                                new MarketPlaceMessage(
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

    public static MarketPlaceMessageType selectRandomIncreaseOrDecrease() {
        List<MarketPlaceMessageType> increaseDecreaseMessages = Arrays.asList(
                MarketPlaceMessageType.INCREASE_TOOS_PRICE,
                MarketPlaceMessageType.INCREASE_SEEDS_PRICE,
                MarketPlaceMessageType.INCREASE_CROP_PRICE,
                MarketPlaceMessageType.DECREASE_TOOLS_PRICE,
                MarketPlaceMessageType.DECREASE_SEEDS_PRICE,
                MarketPlaceMessageType.DECREASE_CROP_PRICE
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
