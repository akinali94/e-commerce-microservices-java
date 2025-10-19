package com.example.checkout_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents credit card information for payment processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardInfo {
    
    @NotBlank(message = "Credit card number is required")
    @JsonProperty("credit_card_number")
    private String creditCardNumber;
    
    @NotNull(message = "Credit card CVV is required")
    @Min(value = 100, message = "CVV must be a 3 or 4 digit number")
    @Max(value = 9999, message = "CVV must be a 3 or 4 digit number")
    @JsonProperty("credit_card_cvv")
    private Integer creditCardCvv;
    
    @NotNull(message = "Credit card expiration year is required")
    @Min(value = 2024, message = "Expiration year must be valid")
    @JsonProperty("credit_card_expiration_year")
    private Integer creditCardExpirationYear;
    
    @NotNull(message = "Credit card expiration month is required")
    @Min(value = 1, message = "Expiration month must be between 1 and 12")
    @Max(value = 12, message = "Expiration month must be between 1 and 12")
    @JsonProperty("credit_card_expiration_month")
    private Integer creditCardExpirationMonth;
}