/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.CivicAuthority.Data;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class CivicAuthorityLandData extends DataBESA  {

    private String landName;
    private String familyName;

    public CivicAuthorityLandData(String landName, String familyName) {
        this.landName = landName;
        this.familyName = familyName;
    }
    public CivicAuthorityLandData(String familyName) {
        this.familyName = familyName;
    }

    public String getLandName() {
        return landName;
    }

    public String getFamilyName() {
        return familyName;
    }
    
}
