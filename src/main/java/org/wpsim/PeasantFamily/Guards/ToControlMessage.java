/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.PeasantFamily.Guards;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class ToControlMessage extends DataBESA {

    private String peasantFamilyAlias;
    private int days;
    private String currentDate;
    private boolean peasantAlive;

    public ToControlMessage(String peasantFamilyAlias, int days) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setDays(days);
    }
    public ToControlMessage(String peasantFamilyAlias, String currentDate) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setCurrentDate(currentDate);
    }

    public ToControlMessage(String peasantFamilyAlias, int days, boolean alive) {
        this.setPeasantAlive(alive);
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setDays(days);
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

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
