package cs174a;

import java.util.ArrayList;

public class SavingAccount extends Account{

    private double interestRate = 0.0;
    public SavingAccount(long accountId, ArrayList<Transaction> transactions, String branch, double balance) {
        super(accountId, transactions, branch, balance);
    }
}
