package com.example.currency_service.model;

public class ConversionResponse {
    private double result;
    private String from;
    private String to;
    private double amount;

    public ConversionResponse(){

    }

    public ConversionResponse(double result, String from, String to, double amount){
        this.result = result;
        this.from = from;
        this.to = to;
        this.amount = amount;
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
}
