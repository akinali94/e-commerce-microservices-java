package com.example.checkout_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an item in a completed order with its cost.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @NotNull(message = "Cart item is required")
    @Valid
    @JsonProperty("item")
    private CartItem item;
    
    @NotNull(message = "Cost is required")
    @Valid
    @JsonProperty("cost")
    private Money cost;
}