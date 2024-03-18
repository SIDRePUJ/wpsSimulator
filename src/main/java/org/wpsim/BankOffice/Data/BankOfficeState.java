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
package org.wpsim.BankOffice.Data;

import BESA.Kernel.Agent.StateBESA;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class BankOfficeState extends StateBESA implements Serializable {

    // Money in "COP"
    private double availableMoney;
    private Map<String, LoanTable> loans = new HashMap<>();

    private String loansToString() {
        if (loans.isEmpty()) {
            return "No active loans";
        }
        return loans.entrySet()
                .stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue().toString()))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public String toString() {
        return "BankAgentState{" +
                "availableMoney=" + availableMoney +
                ", loans=" + loansToString() + // Usamos el método personalizado aquí
                '}';
    }

    /**
     *
     */
    public BankOfficeState() {
        super();
        availableMoney = 10000000;
    }

    public boolean giveLoanToPeasantFamily(BankOfficeMessageType loanType, String peasantFamily, double money) {

        //@TODO: incluir prestamos informales, cobro cada semana y más interés.
        if (availableMoney > money && !loans.containsKey(peasantFamily)) {
            addLoanPeasantFamily(
                    peasantFamily,
                    new LoanTable(
                            peasantFamily,
                            money,
                            12,
                            0,
                            loanType
                    )
            );
            return true;
        } else {
            return false;
        }
    }

    public double getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(double availableMoney) {
        this.availableMoney = availableMoney;
    }

    public Map<String, LoanTable> getLoans() {
        return loans;
    }

    public void addLoanPeasantFamily(String PeasantFamily, LoanTable loanTable) {
        this.loans.put(PeasantFamily, loanTable);
    }

    public double currentMoneyToPay(String PeasantFamily) {
        if (this.loans.containsKey(PeasantFamily)) {
            return this.loans.get(PeasantFamily).MoneyToPay();
        } else {
            return 0;
        }
    }

    public double payLoan(String PeasantFamily, double money) {
        try {
            if (money >= loans.get(PeasantFamily).MoneyToPay()) {
                // If money is enought discount one paid term.
                loans.get(PeasantFamily).increasePaidTerm();
                // If the term is end... remove from the loans table
                if (loans.get(PeasantFamily).getPaidTerm() == 12) {
                    loans.remove(PeasantFamily);
                }
                // return money to paid
                return (loans.get(PeasantFamily).MoneyToPay()
                        * (loans.get(PeasantFamily).getMaxTerm()
                        - loans.get(PeasantFamily).getPaidTerm()));
            } else {
                // else... return -1 error
                return -1;
            }
        } catch (Exception e){
            return 0;
        }
    }
}
