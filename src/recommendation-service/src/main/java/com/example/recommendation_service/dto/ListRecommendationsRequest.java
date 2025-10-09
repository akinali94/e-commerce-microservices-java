package com.example.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class ListRecommendationsRequest {
    
    @NotBlank(message = "userId is required")
    @JsonProperty("userId")
    private String userId;
    
    @NotNull(message = "productIds is required")
    @JsonProperty("productIds")
    private List<String> productIds;
    
    public ListRecommendationsRequest() {
    }
    
    public ListRecommendationsRequest(String userId, List<String> productIds) {
        this.userId = userId;
        this.productIds = productIds;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<String> getProductIds() {
        return productIds;
    }
    
    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}