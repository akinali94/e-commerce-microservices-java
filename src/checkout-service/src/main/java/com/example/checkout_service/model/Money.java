package com.example.checkout_service.model;

public class Money {
    private long units;
    private int nanos;
    private String currencyCode;

    // Constructors
    public Money() {
    }

    public Money(long units, int nanos, String currencyCode) {
        this.units = units;
        this.nanos = nanos;
        this.currencyCode = currencyCode;
    }

    public Money(String currencyCode) {
        this(0, 0, currencyCode);
    }

    // Getters and setters
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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "Money{" +
                "units=" + units +
                ", nanos=" + nanos +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return units == money.units && nanos == money.nanos && 
               (currencyCode != null ? currencyCode.equals(money.currencyCode) : money.currencyCode == null);
    }

    @Override
    public int hashCode() {
        int result = (int) (units ^ (units >>> 32));
        result = 31 * result + nanos;
        result = 31 * result + (currencyCode != null ? currencyCode.hashCode() : 0);
        return result;
    }
}