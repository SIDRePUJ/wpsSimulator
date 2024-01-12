package org.wpsim.PeasantFamily.Tasks.Base;

import org.wpsim.PeasantFamily.Data.PeasantFamilyBDIAgentBelieves;
import rational.mapping.Believes;
import rational.mapping.Task;

public class wpsTask extends Task {
    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        return believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName());
    }

    @Override
    public void executeTask(Believes believes) {

    }

    @Override
    public void interruptTask(Believes believes) {

    }

    @Override
    public void cancelTask(Believes parameters) {
        PeasantFamilyBDIAgentBelieves believes = (PeasantFamilyBDIAgentBelieves) parameters;
        System.out.println(believes.getPeasantProfile().getPeasantFamilyAlias() + " Canceling " + this.getClass().getSimpleName());
    }
}
