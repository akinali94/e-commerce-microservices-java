package com.example.checkout_service.dto;

import com.example.checkout_service.model.Address;
import com.example.checkout_service.model.CreditCardInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {
    
    @NotBlank(message = "User ID is required")
    @JsonProperty("user_id")
    private String userId;
    
    @NotBlank(message = "User currency is required")
    @JsonProperty("user_currency")
    private String userCurrency;
    
    @NotNull(message = "Address is required")
    @Valid
    @JsonProperty("address")
    private Address address;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @JsonProperty("email")
    private String email;
    
    @NotNull(message = "Credit card is required")
    @Valid
    @JsonProperty("credit_card")
    private CreditCardInfo creditCard;
}