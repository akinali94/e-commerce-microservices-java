package com.example.checkout_service.dto;

import com.example.checkout_service.model.Address;
import com.example.checkout_service.model.CartItem;
import com.example.checkout_service.model.CreditCardInfo;
import com.example.checkout_service.model.Money;
import com.example.checkout_service.model.OrderResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ExternalServiceDtos {
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("picture")
        private String picture;
        
        @JsonProperty("price_usd")
        private Money priceUsd;
        
        @JsonProperty("categories")
        private List<String> categories;
    }
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConvertCurrencyRequest {
        @JsonProperty("from")
        private Money from;
        
        @JsonProperty("to_code")
        private String toCode;
    }
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuoteRequest {
        @JsonProperty("address")
        private Address address;
        
        @JsonProperty("items")
        private List<CartItem> items;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetQuoteResponse {
        @JsonProperty("cost_usd")
        private Money costUsd;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipOrderRequest {
        @JsonProperty("address")
        private Address address;
        
        @JsonProperty("items")
        private List<CartItem> items;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipOrderResponse {
        @JsonProperty("tracking_id")
        private String trackingId;
    }
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeRequest {
        @JsonProperty("amount")
        private Money amount;
        
        @JsonProperty("credit_card")
        private CreditCardInfo creditCard;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeResponse {
        @JsonProperty("transaction_id")
        private String transactionId;
    }
        
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendOrderConfirmationRequest {
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("order")
        private OrderResult order;
    }
}