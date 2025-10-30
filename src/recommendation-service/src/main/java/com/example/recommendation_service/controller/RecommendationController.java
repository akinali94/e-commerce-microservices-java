package com.example.recommendation_service.controller;

import com.example.recommendation_service.dto.ListRecommendationsRequest;
import com.example.recommendation_service.dto.ListRecommendationsResponse;
import com.example.recommendation_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    private final RecommendationService recommendationService;

    /**
     * Constructor with dependency injection.
     *
     * @param recommendationService the recommendation service to use
     */
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * API information endpoint.
     * 
     * @return information about the API and available endpoints
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Recommendation Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /recommendations/", "API information");
        endpoints.put("GET /recommendations/health", "Health check");
        endpoints.put("POST /recommendations", "Get product recommendations (body: { userId: string, productIds: string[] })");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }

    /**
     * Endpoint for getting product recommendations.
     *
     * @param request the recommendation request containing user ID and product IDs
     * @return response containing recommended product IDs
     */
    @PostMapping
    public ResponseEntity<ListRecommendationsResponse> listRecommendations(
            @RequestBody ListRecommendationsRequest request) {
        logger.info("Received recommendation request through REST API: {}", request);
        ListRecommendationsResponse response = recommendationService.listRecommendations(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     *
     * @return 200 OK response if the service is healthy
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Recommendation Service is healthy");
    }
}