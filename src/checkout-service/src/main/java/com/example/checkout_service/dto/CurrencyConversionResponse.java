package com.example.checkout_service.dto;


public class CurrencyConversionResponse {
    private double result;
    private String from;
    private String to;
    private double amount;
    
    public CurrencyConversionResponse() {
    }
    
    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "CurrencyConversionResponse{" +
                "result=" + result +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount=" + amount +
                '}';
    }
}