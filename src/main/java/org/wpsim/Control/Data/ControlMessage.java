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
    private boolean wait;

    public ControlMessage(String peasantFamilyAlias, boolean wait) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setWait(wait);
    }
    public String getPeasantFamilyAlias() {
        return peasantFamilyAlias;
    }

    public void setPeasantFamilyAlias(String peasantFamilyAlias) {
        this.peasantFamilyAlias = peasantFamilyAlias;
    }

    public boolean isWaiting() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

}
