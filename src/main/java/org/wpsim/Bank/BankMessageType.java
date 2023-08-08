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
package org.wpsim.Bank;

/**
 *
 */
public enum BankMessageType {

    /**
     * Formal bank loan
     */
    ASK_FOR_FORMAL_LOAN,
    
    /**
     * Informal social loan
     * Pagadiario (semanal)
     */
    ASK_FOR_INFORMAL_LOAN,

    /**
     * Ask for payment
     */
    ASK_CURRENT_TERM,
    
    /**
     *
     */
    PAY_LOAN_TERM
    
}
