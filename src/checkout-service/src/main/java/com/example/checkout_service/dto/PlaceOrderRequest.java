package com.example.checkout_service.dto;

import java.util.List;

import com.example.checkout_service.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PlaceOrderRequest {
    @NotBlank(message = "User ID cannot be empty")
    private String userId;
    
    @NotBlank(message = "User currency cannot be empty")
    private String userCurrency;
    
    @NotNull(message = "Address cannot be null")
    private Address address;
    
    @NotBlank(message = "Email cannot be empty")
    private String email;
    
    @NotNull(message = "Credit card information cannot be null")
    private CreditCardInfo creditCard;

    @NotNull(message = "Items list cannot be null")
    @Size(min = 1, message = "At least one item must be specified")
    private List<CartItem> items;

    public PlaceOrderRequest() {
    }

    public PlaceOrderRequest(String userId, String userCurrency, Address address, 
                            String email, CreditCardInfo creditCard, List<CartItem> items) {
        this.userId = userId;
        this.userCurrency = userCurrency;
        this.address = address;
        this.email = email;
        this.creditCard = creditCard;
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCurrency() {
        return userCurrency;
    }

    public void setUserCurrency(String userCurrency) {
        this.userCurrency = userCurrency;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CreditCardInfo getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardInfo creditCard) {
        this.creditCard = creditCard;
    }

    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PlaceOrderRequest{" +
                "userId='" + userId + '\'' +
                ", userCurrency='" + userCurrency + '\'' +
                ", address=" + address +
                ", email='" + email + '\'' +
                ", creditCard=" + creditCard +
                '}';
    }
}