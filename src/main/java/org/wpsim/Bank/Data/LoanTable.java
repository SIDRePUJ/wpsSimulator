/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.wpsim.Bank.Data;

import java.io.Serializable;

/**
 * Simple Loan Table without extras
 */
public class LoanTable implements Serializable {
    
    private String peasantFamily;
    private double loanGranted;
    private Integer maxTerm;
    private Integer paidTerm;
    private BankMessageType loanType;
    
    public LoanTable(String peasantFamily, double loanGranted, Integer maxTerm, Integer paidTerm, BankMessageType loanType){
        this.peasantFamily = peasantFamily;
        this.loanGranted = loanGranted;
        this.maxTerm = maxTerm;
        this.paidTerm = paidTerm;
        this.loanType = loanType;
    }

    public BankMessageType getLoanType() {
        return loanType;
    }

    public void setLoanType(BankMessageType loanType) {
        this.loanType = loanType;
    }
    
    public double MoneyToPay(){
        if (maxTerm > 0){
            return loanGranted/maxTerm;
        }
        return 0;
    }

    public String getPeasantFamilyAlias() {
        return peasantFamily;
    }

    public void setPeasantFamilyAlias(String peasantFamilyAlias) {
        this.peasantFamily = peasantFamilyAlias;
    }

    public double getLoanGranted() {
        return loanGranted;
    }

    public void setLoanGranted(Integer loanGranted) {
        this.loanGranted = loanGranted;
    }

    public Integer getMaxTerm() {
        return maxTerm;
    }

    public void setMaxTerm(Integer maxTerm) {
        this.maxTerm = maxTerm;
    }

    public Integer getPaidTerm() {
        return paidTerm;
    }

    public void setPaidTerm(Integer paidTerm) {
        this.paidTerm = paidTerm;
    }
    public void increasePaidTerm() {
        this.paidTerm++;
    }

    @Override
    public String toString() {
        return String.format(
                "LoanTable{peasantFamily='%s', loanGranted=%.2f, maxTerm=%d, paidTerm=%d, loanType='%s', remainingPayments=%.2f}",
                peasantFamily,
                loanGranted,
                maxTerm,
                paidTerm,
                loanType,
                MoneyToPay() * (maxTerm - paidTerm)
        );
    }
}
