package com.example.checkout_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditCardInfo {

    @JsonProperty("number")
    private String creditCardNumber;
    @JsonProperty("cvv")
    private int creditCardCvv;
    @JsonProperty("expirationYear")
    private int creditCardExpirationYear;
    @JsonProperty("expirationMonth")
    private int creditCardExpirationMonth;

    
    public CreditCardInfo() {
    }

    public CreditCardInfo(String creditCardNumber, int creditCardCvv, 
                         int creditCardExpirationYear, int creditCardExpirationMonth) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardCvv = creditCardCvv;
        this.creditCardExpirationYear = creditCardExpirationYear;
        this.creditCardExpirationMonth = creditCardExpirationMonth;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public int getCreditCardCvv() {
        return creditCardCvv;
    }

    public void setCreditCardCvv(int creditCardCvv) {
        this.creditCardCvv = creditCardCvv;
    }

    public int getCreditCardExpirationYear() {
        return creditCardExpirationYear;
    }

    public void setCreditCardExpirationYear(int creditCardExpirationYear) {
        this.creditCardExpirationYear = creditCardExpirationYear;
    }

    public int getCreditCardExpirationMonth() {
        return creditCardExpirationMonth;
    }

    public void setCreditCardExpirationMonth(int creditCardExpirationMonth) {
        this.creditCardExpirationMonth = creditCardExpirationMonth;
    }

    @Override
    public String toString() {
        return "CreditCardInfo{" +
                "creditCardNumber='" + creditCardNumber + '\'' +
                ", creditCardCvv=" + creditCardCvv +
                ", creditCardExpirationYear=" + creditCardExpirationYear +
                ", creditCardExpirationMonth=" + creditCardExpirationMonth +
                '}';
    }
}