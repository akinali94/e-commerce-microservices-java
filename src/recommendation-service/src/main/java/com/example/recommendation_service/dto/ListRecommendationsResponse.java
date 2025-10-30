package com.example.recommendation_service.dto;

import java.util.List;

/**
 * DTO for recommendation responses containing a list of recommended product IDs.
 */
public class ListRecommendationsResponse {
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

    @Override
    public String toString() {
        return "ListRecommendationsResponse{" +
                "productIds=" + productIds +
                '}';
    }
}