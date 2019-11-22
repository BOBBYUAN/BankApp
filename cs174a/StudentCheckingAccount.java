package cs174a;

import java.util.ArrayList;

public class StudentCheckingAccount extends Account{

    private double interestRate;

    public StudentCheckingAccount(long accountId, ArrayList<Transaction> transactions, String branch, double balance) {
        super(accountId, transactions, branch, balance);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
