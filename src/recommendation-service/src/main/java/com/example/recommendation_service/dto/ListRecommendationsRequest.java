package com.example.recommendation_service.dto;

import java.util.List;

/**
 * DTO for recommendation requests containing user ID and a list of product IDs.
 */
public class ListRecommendationsRequest {
    private String userId;
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

    @Override
    public String toString() {
        return "ListRecommendationsRequest{" +
                "userId='" + userId + '\'' +
                ", productIds=" + productIds +
                '}';
    }
}