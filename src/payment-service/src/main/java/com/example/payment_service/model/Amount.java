package com.example.payment_service.model;

public class Amount {
    private String currencyCode;
    private int units;
    private int nanos;

    public String getCurrencyCode(){
        return currencyCode;
    }

    public void set(String currencyCode){
        this.currencyCode = currencyCode;
    }

    public int getUnits(){
        return units;
    }

    public void setUnits(int units){
        this.units = units;
    }
    
    public int getNanos(){
        return nanos;
    }

    public void setNanos(int nanos){
        this.nanos = nanos;
    }
}