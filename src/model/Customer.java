package model;

import java.util.HashSet;

public class Customer {
    private String name;
    private int taxIdentificationNumber;
    private String address;
    private HashSet<Account> accounts;
    private String PIN;

    public Customer(String name, int taxIdentificationNumber, String address, HashSet<Account> accounts, String PIN) {
        this.name = name;
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.address = address;
        this.accounts = accounts;
        this.PIN = PIN;
    }

    boolean VerifyPin(String PIN) {
        if (this.PIN.equals(PIN)) {
            return true;
        } else {
            return false;
        }
    }

    void SetPin(String OldPIN, String NewPIN) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    public void setTaxIdentificationNumber(int taxIdentificationNumber) {
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public HashSet<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(HashSet<Account> accounts) {
        this.accounts = accounts;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }
}
