package com.example.recommendation_service.model;

/**
 * Money entity representing a monetary value with currency code, units, and nanos.
 */
public class Money {
    private String currencyCode;
    private long units;
    private int nanos;

    public Money() {
    }

    public Money(String currencyCode, long units, int nanos) {
        this.currencyCode = currencyCode;
        this.units = units;
        this.nanos = nanos;
    }

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

    @Override
    public String toString() {
        return "Money{" +
                "currencyCode='" + currencyCode + '\'' +
                ", units=" + units +
                ", nanos=" + nanos +
                '}';
    }
}