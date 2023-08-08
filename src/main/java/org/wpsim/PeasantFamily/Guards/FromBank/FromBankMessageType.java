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
package org.wpsim.PeasantFamily.Guards.FromBank;

/**
 *
 */
public enum FromBankMessageType {
    /**
     *
     */
    APPROBED_LOAN,
    /**
     *
     */
    APPROBED_INFORMAL_LOAN,
    /**
     *
     */
    DENIED_FORMAL_LOAN,
    /**
     *
     */
    DENIED_INFORMAL_LOAN,
    /**
     *
     */
    TERM_TO_PAY,
    /**
     *
     */
    TERM_PAYED,
    /**
     *
     */
    APPROBED_SOCIAL
}
