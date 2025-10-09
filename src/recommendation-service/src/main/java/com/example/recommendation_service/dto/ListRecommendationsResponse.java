package com.example.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;


public class ListRecommendationsResponse {
    
    @JsonProperty("productIds")
    private List<String> productIds;
    
    public ListRecommendationsResponse() {
    }
    
    public ListRecommendationsResponse(List<String> productIds) {
        this.productIds = productIds;
    }
    
    public List<String> getProductIds() {
        return productIds;
    }
    
    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}