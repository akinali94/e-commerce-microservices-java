package com.example.email_service.model;

public class Money {
    private String currencyCode;
    private long units;
    private int nanos;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public long getUnits() {
        return units;
    }

    public void setUnits(long units) {
        this.units = units;
    }

    public int getNanos() {
        return nanos;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }
    
    public String getFormattedAmount() {
        return String.format("%d.%02d", units, nanos / 10000000);
    }
}