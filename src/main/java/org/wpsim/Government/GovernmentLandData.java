/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.Government;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class GovernmentLandData extends DataBESA  {

    private String landName;
    private String familyName;

    public GovernmentLandData(String landName, String familyName) {
        this.landName = landName;
        this.familyName = familyName;
    }
    public GovernmentLandData(String familyName) {
        this.familyName = familyName;
    }

    public String getLandName() {
        return landName;
    }

    public String getFamilyName() {
        return familyName;
    }
    
}
