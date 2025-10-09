package com.example.recommendation_service.controller;

import com.hipstershop.recommendationservice.model.ErrorResponse;
import com.hipstershop.recommendationservice.model.HealthCheckResponse;
import com.hipstershop.recommendationservice.model.ListRecommendationsRequest;
import com.hipstershop.recommendationservice.model.ListRecommendationsResponse;
import com.hipstershop.recommendationservice.service.RecommendationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class RecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    
    private final RecommendationService recommendationService;
    private final String serviceName;
    private final String serviceVersion;
    
    public RecommendationController(
            RecommendationService recommendationService,
            @Value("${service.name:recommendation-service}") String serviceName,
            @Value("${service.version:1.0.0}") String serviceVersion) {
        this.recommendationService = recommendationService;
        this.serviceName = serviceName;
        this.serviceVersion = serviceVersion;
    }
    
    /**
     * Health check endpoint
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        HealthCheckResponse response = new HealthCheckResponse(
                "SERVING",
                serviceName,
                serviceVersion
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * List recommendations endpoint
     * POST /recommendations
     */
    @PostMapping("/recommendations")
    public ResponseEntity<ListRecommendationsResponse> listRecommendations(
            @Valid @RequestBody ListRecommendationsRequest request) {
        
        logger.info("Received recommendation request for userId: {}", request.getUserId());
        
        ListRecommendationsResponse response = recommendationService.listRecommendations(request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("Validation error: {}", errors);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_REQUEST",
                "Validation failed"
        );
        errorResponse.getError().setDetails(errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Exception handler for general errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Request failed", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}