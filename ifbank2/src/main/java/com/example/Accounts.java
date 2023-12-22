package com.example;

public abstract class Accounts {
  
    protected String label;
    protected double balance;
    protected int accountNumber;
    protected Clients client;

    
    public Accounts(String label, Clients client) {
        this.label = label;
        this.client = client;
        this.accountNumber = client.getClientNumber();
    }

    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double amount, String flowType) {
        switch (flowType) {
            case "Transfer":
                
                break;
            case "Credit":
                balance += amount;
                break;
            case "Debit":
                balance -= amount;
                break;

            default:
                break;
        }
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public Clients getClient() {
        return client;
    }

    
    @Override
    public String toString() {
        return "Account{" +
                "label='" + label + '\'' +
                ", balance=" + balance +
                ", accountNumber=" + accountNumber +
                ", client=" + client.toString() +
                '}';
    }
}

