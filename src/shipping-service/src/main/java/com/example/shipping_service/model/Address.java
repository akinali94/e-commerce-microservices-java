package com.example.shipping_service.model;

import jakarta.validation.constraints.NotBlank;

public class Address {
    @NotBlank(message = "Street address is required")
    private String streetAddress;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String state;
    private String country;
    private Integer zipCode;
    
    public Address() {}
    
    public Address(String streetAddress, String city, String state, String country, Integer zipCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }
    
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public Integer getZipCode() { return zipCode; }
    public void setZipCode(Integer zipCode) { this.zipCode = zipCode; }
}