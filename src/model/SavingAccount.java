package model;

import java.util.ArrayList;

public class SavingAccount extends Account{
    public SavingAccount(long accountId, ArrayList<Transaction> transactions, String branch, double balance) {
        super(accountId, transactions, branch, balance);
    }
}
