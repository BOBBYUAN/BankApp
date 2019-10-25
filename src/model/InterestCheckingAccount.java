package model;

import java.util.ArrayList;

public class InterestCheckingAccount extends  Account {

    private double interestRate;

    public InterestCheckingAccount(long accountId, ArrayList<Transaction> transactions, String branch, double balance) {
        super(accountId, transactions, branch, balance);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
