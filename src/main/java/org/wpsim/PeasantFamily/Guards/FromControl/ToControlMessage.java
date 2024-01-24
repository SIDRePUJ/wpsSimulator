/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.PeasantFamily.Guards.FromControl;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class ToControlMessage extends DataBESA {

    private String peasantFamilyAlias;
    private int currentDay;
    private String currentDate;
    private boolean peasantAlive;

    public ToControlMessage(String peasantFamilyAlias) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
    }
    public ToControlMessage(String peasantFamilyAlias, int currentDay) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setCurrentDay(currentDay);
    }
    public ToControlMessage(String peasantFamilyAlias, String currentDate) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setCurrentDate(currentDate);
    }

    public ToControlMessage(String peasantFamilyAlias, String currentDate, int currentDay) {
        this.setCurrentDate(currentDate);
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setCurrentDay(currentDay);
    }

    public void setPeasantAlive(boolean peasantAlive) {
        this.peasantAlive = peasantAlive;
    }

    public String getPeasantFamilyAlias() {
        return peasantFamilyAlias;
    }

    public void setPeasantFamilyAlias(String peasantFamilyAlias) {
        this.peasantFamilyAlias = peasantFamilyAlias;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
