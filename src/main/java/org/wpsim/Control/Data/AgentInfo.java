package org.wpsim.Control.Data;

public class AgentInfo {
    private boolean state;
    private int currentDay;

    public AgentInfo(boolean state, int currentDay) {
        this.state = state;
        this.currentDay = currentDay;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    @Override
    public String toString() {
        return "AgentInfo{" +
                "state=" + state +
                ", currentDay='" + currentDay + '\'' +
                '}';
    }
}
