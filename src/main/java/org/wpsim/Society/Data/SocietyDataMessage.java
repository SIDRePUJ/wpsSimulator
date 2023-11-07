/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.Society.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class SocietyDataMessage extends DataBESA  {

    String peasantFamilyAgent;
    String peasantFamilyHelper;
    int availableDays;

    public SocietyDataMessage(String peasantFamilyAgent, String peasantFamilyHelper, int days) {
        this.peasantFamilyAgent = peasantFamilyAgent;
        this.peasantFamilyHelper = peasantFamilyHelper;
        this.availableDays = days;
    }

    public int getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(int availableDays) {
        this.availableDays = availableDays;
    }

    public String getPeasantFamilyAgent() {
        return peasantFamilyAgent;
    }

    public void setPeasantFamilyAgent(String peasantFamilyAgent) {
        this.peasantFamilyAgent = peasantFamilyAgent;
    }

    public String getPeasantFamilyHelper() {
        return peasantFamilyHelper;
    }

    public void setPeasantFamilyHelper(String peasantFamilyHelper) {
        this.peasantFamilyHelper = peasantFamilyHelper;
    }
    
}
