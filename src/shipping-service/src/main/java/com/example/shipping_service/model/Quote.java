package com.example.shipping_service.model;

public class Quote {
    private Integer dollars;
    private Integer cents;
    
    public Quote() {}
    
    public Quote(Integer dollars, Integer cents) {
        this.dollars = dollars;
        this.cents = cents;
    }
    
    public Integer getDollars() { return dollars; }
    public void setDollars(Integer dollars) { this.dollars = dollars; }
    
    public Integer getCents() { return cents; }
    public void setCents(Integer cents) { this.cents = cents; }
    
    @Override
    public String toString() {
        return String.format("$%d.%02d", dollars, cents);
    }
}