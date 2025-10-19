package com.example.checkout_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents the result of a completed order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResult {
    
    @NotBlank(message = "Order ID is required")
    @JsonProperty("order_id")
    private String orderId;
    
    @NotBlank(message = "Shipping tracking ID is required")
    @JsonProperty("shipping_tracking_id")
    private String shippingTrackingId;
    
    @NotNull(message = "Shipping cost is required")
    @Valid
    @JsonProperty("shipping_cost")
    private Money shippingCost;
    
    @NotNull(message = "Shipping address is required")
    @Valid
    @JsonProperty("shipping_address")
    private Address shippingAddress;
    
    @NotNull(message = "Items are required")
    @Valid
    @JsonProperty("items")
    private List<OrderItem> items;
}
