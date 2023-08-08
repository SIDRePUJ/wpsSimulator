package org.wpsim.World.Agent;

import BESA.Kernel.Agent.Event.EventBESA;
import BESA.Kernel.Agent.PeriodicGuardBESA;

/**
 * BESA world's periodic guard, holds the behavior when calling the world agent
 */
public class WorldPeriodicGuard extends PeriodicGuardBESA {

    /**
     *
     * @param eventBESA
     */
    @Override
    public void funcPeriodicExecGuard(EventBESA eventBESA) {
       /* try {
            AgHandlerBESA ah = this.agent.getAdmLocal().getHandlerByAid(this.agent.getAid());
            wpsCurrentDate currentDate = wpsCurrentDate.getInstance();
            WorldMessage worldMessage = new WorldMessage(WorldMessageType.CROP_OBSERVE, null, currentDate.getCurrentDate(), null);
            EventBESA eventBESASend = new EventBESA(WorldGuard.class.getName(), worldMessage);
            ah.sendEvent(eventBESASend);
        } catch (ExceptionBESA exceptionBESA) {
            exceptionBESA.printStackTrace();
        }*/
    }
}
