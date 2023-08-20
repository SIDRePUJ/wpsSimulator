/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.Control.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class ControlMessage extends DataBESA {

    private String peasantFamilyAlias;
    private int wait;
    private int currentDay;

    public ControlMessage(String peasantFamilyAlias, int wait, int currentDay) {
        this.setCurrentDay(currentDay);
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setWait(wait);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public String getPeasantFamilyAlias() {
        return peasantFamilyAlias;
    }

    public void setPeasantFamilyAlias(String peasantFamilyAlias) {
        this.peasantFamilyAlias = peasantFamilyAlias;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

}
