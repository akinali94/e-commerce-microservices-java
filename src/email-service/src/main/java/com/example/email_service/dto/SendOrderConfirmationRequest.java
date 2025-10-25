package com.example.email_service.dto;

import com.example.email_service.model.OrderResult;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendOrderConfirmationRequest {
    
    @NotBlank(message = "Email address is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotNull(message = "Order details are required")
    private OrderResult order;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OrderResult getOrder() {
        return order;
    }

    public void setOrder(OrderResult order) {
        this.order = order;
    }
    
    @Override
    public String toString() {
        return "SendOrderConfirmationRequest{" +
                "email='" + email + '\'' +
                ", order=" + (order != null ? order.getOrderId() : "null") +
                '}';
    }
}