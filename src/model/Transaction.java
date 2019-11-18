package model;

import java.util.Date;

public class Transaction {

    private Date date;
    private double amount;

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public double getAmount() { return amount; }

    public void setAmount(double amount) { this.amount = amount; }

    public void dedposit(double money, long accountId) {
    }

    public void top_up(double money, long accountId) {
    }

    public void withdraw(double money, long accountId) {
    }

    public void purchase(double money, long accountId) {
    }

    public void transfer(double money, long fromAccountId, String toAccountId) {
    }

    public void collect(double money, long accountId) {
    }

    public void pay_friend(double money, long fromAccountId, long toAccountId) {
    }

    public void wire(double money, long fromAccountId, long toAccountId) {
    }

    public void wire_check(double money, long accountId) {
    }
    public void accure_interest() {

    }
}
