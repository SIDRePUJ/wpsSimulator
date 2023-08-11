/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.Control;

import BESA.Kernel.Agent.Event.DataBESA;

/**
 *
 * @author jairo
 */
public class ControlMessage extends DataBESA {

    private String peasantFamilyAlias;
    private int wait;

    public ControlMessage(String peasantFamilyAlias, int wait) {
        this.setPeasantFamilyAlias(peasantFamilyAlias);
        this.setWait(wait);
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
