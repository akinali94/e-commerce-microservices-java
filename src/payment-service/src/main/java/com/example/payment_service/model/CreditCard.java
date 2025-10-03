package com.example.payment_service.model;

public class CreditCard {
    private String cardNo;
    private String cvv;
    private String expirationDate; //MM/YY or MM/YYYY format

    public String getCardNo(){
        return cardNo;
    }

    public void setCardNo(String cardNo){
        this.cardNo = cardNo;
    }

    public String getCvv(){
        return cvv;
    }

    public void setCvv(String cvv){
        this.cvv = cvv;
    }

    public String getExpirationDate(){
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate){
        this.expirationDate = expirationDate;
    }
}
