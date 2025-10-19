package com.example.checkout_service.dto;

import com.example.checkout_service.model.OrderResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Response DTO for order placement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderResponse {
    
    @NotNull(message = "Order is required")
    @Valid
    @JsonProperty("order")
    private OrderResult order;
}
