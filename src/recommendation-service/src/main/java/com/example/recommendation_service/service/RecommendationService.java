package com.example.recommendation_service.service;

import com.example.recommendation_service.dto.ListRecommendationsRequest;
import com.example.recommendation_service.dto.ListRecommendationsResponse;

/**
 * Service interface for product recommendations.
 */
public interface RecommendationService {
    
    /**
     * Gets a list of product recommendations based on user ID and their current product selection.
     *
     * @param request the recommendation request containing user ID and product IDs
     * @return ListRecommendationsResponse containing recommended product IDs
     */
    ListRecommendationsResponse listRecommendations(ListRecommendationsRequest request);
}