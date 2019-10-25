package model;

import java.util.ArrayList;


/* Not quite Sure if we should use interface instead of class for Account
* Since four types of account don't touch each other
* To Design*/

public class Account {
    private long accountId;
    private ArrayList<Transaction> transactions;
    private String branch;
    private double balance;

    public Account(long accountId, ArrayList<Transaction> transactions, String branch, double balance) {
        this.accountId = accountId;
        this.transactions = transactions;
        this.branch = branch;
        this.balance = balance;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
