/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.CommunityDynamics.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class CommunityDynamicsDataMessage extends DataBESA  {

    String peasantFamilyContractor;
    String peasantFamilyHelper;
    int availableDays;

    public CommunityDynamicsDataMessage(String peasantFamilyContractor, String peasantFamilyHelper, int days) {
        this.peasantFamilyContractor = peasantFamilyContractor;
        this.peasantFamilyHelper = peasantFamilyHelper;
        this.availableDays = days;
    }

    public int getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(int availableDays) {
        this.availableDays = availableDays;
    }

    public String getPeasantFamilyContractor() {
        return peasantFamilyContractor;
    }

    public void setPeasantFamilyContractor(String peasantFamilyContractor) {
        this.peasantFamilyContractor = peasantFamilyContractor;
    }

    public String getPeasantFamilyHelper() {
        return peasantFamilyHelper;
    }

    public void setPeasantFamilyHelper(String peasantFamilyHelper) {
        this.peasantFamilyHelper = peasantFamilyHelper;
    }
    
}
