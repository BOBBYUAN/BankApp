package model;

import java.util.ArrayList;

public class PocketAccount extends Account{

    private double interestRate = 0.0;

    public PocketAccount(long accountId, ArrayList<Transaction> transactions, String branch, double balance) {
        super(accountId, transactions, branch, balance);
    }
}
