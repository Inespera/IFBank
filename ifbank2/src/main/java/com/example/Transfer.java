package com.example;

import java.util.Date;
public class Transfer extends Flow {
    
    private int issuingAccountNumber;

    public Transfer(String comment, int identifier, double amount, int targetAccountNumber, boolean effect, int issuingAccountNumber,Date date) {
        super(comment, identifier, amount, targetAccountNumber, effect,date);
        this.issuingAccountNumber = issuingAccountNumber;
    }

    public int getIssuingAccountNumber() {
        return issuingAccountNumber;
    }

    public void setIssuingAccountNumber(int issuingAccountNumber) {
        this.issuingAccountNumber = issuingAccountNumber;
    }
}