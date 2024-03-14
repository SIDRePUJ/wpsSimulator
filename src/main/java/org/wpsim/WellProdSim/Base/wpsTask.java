package org.wpsim.WellProdSim.Base;

import org.wpsim.PeasantFamily.Data.PeasantFamilyBelieves;
import rational.mapping.Believes;
import rational.mapping.Task;

public class wpsTask extends Task {
    protected boolean isExecuted = false;

    @Override
    public boolean checkFinish(Believes parameters) {
        PeasantFamilyBelieves believes = (PeasantFamilyBelieves) parameters;
        isExecuted = believes.isTaskExecutedOnDate(believes.getInternalCurrentDate(), this.getClass().getSimpleName());
        if (isExecuted && (this.taskState == STATE.WAITING_FOR_EXECUTION || this.taskState == STATE.IN_EXECUTION)) {
            isExecuted = false;
        } else if (isExecuted && this.taskState == STATE.FINALIZED) {
            isExecuted = true;
        }
        //System.out.println("State " + this.taskState + " isWaitingForExecution " + this.isWaitingForExecution() + " isFinalized " + this.isFinalized() + " isInExecution " + this.isInExecution());
        return isExecuted;
    }

    @Override
    public void executeTask(Believes believes) {
    }

    @Override
    public void interruptTask(Believes believes) {
        //System.out.println("interruptTask " + this.getClass().getSimpleName());
    }

    @Override
    public void cancelTask(Believes believes) {
        //System.out.println("cancelTask " + this.getClass().getSimpleName());
    }

    protected void setExecuted(boolean isExecuted) {
        this.isExecuted = isExecuted;
    }
}
