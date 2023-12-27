/**
 * ==========================================================================
 * __      __ _ __   ___  *    WellProdSim                                  *
 * \ \ /\ / /| '_ \ / __| *    @version 1.0                                 *
 *  \ V  V / | |_) |\__ \ *    @since 2023                                  *
 *   \_/\_/  | .__/ |___/ *                                                 *
 *           | |          *    @author Jairo Serrano                        *
 *           |_|          *    @author Enrique Gonzalez                     *
 * ==========================================================================
 * Social Simulator used to estimate productivity and well-being of peasant *
 * families. It is event oriented, high concurrency, heterogeneous time     *
 * management and emotional reasoning BDI.                                  *
 * ==========================================================================
 */
package org.wpsim.Society.Data;

import BESA.Kernel.Agent.StateBESA;
import org.wpsim.Viewer.Data.wpsReport;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author jairo
 */
public class SocietyAgentState extends StateBESA implements Serializable {

    // @TODO: Incluir medio de pago y tiempo de contrato. el tiempo según el cultivo.
    // @TODO: revisar los jornales por precio.
    ConcurrentLinkedDeque<String> peasantFamilyHelperStack = new ConcurrentLinkedDeque<>();
    private ReentrantLock lock = new ReentrantLock();
    /**
     *
     */
    public SocietyAgentState() {
        super();
    }

    public void addPeasantFamilyToStack(String peasantFamilyHelper) {
        if (!peasantFamilyHelper.isEmpty() && !peasantFamilyHelperStack.contains(peasantFamilyHelper)) {
            peasantFamilyHelperStack.push(peasantFamilyHelper);
            wpsReport.info("Ofertando ayuda " +  peasantFamilyHelper, "SocietyAgent");
        }
        //System.out.println("Campesinos disponibles: " + peasantFamilyHelperStack);
    }
    public String getPeasantFamilyFromStack() {
        lock.lock();
        try {
            if (!peasantFamilyHelperStack.isEmpty()) {
                return peasantFamilyHelperStack.pop();
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }
}
