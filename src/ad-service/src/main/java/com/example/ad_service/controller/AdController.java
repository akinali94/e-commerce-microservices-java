package com.example.ad_service.controller;


import com.example.ad_service.dto.AdRequestDto;
import com.example.ad_service.dto.AdResponseDto;
import com.example.ad_service.service.AdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping
public class AdController {

    private static final Logger logger = LoggerFactory.getLogger(AdController.class);
    
    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    /**
     * GET /
     * API information endpoint
     * 
     * @return API information
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Ad Service API");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /", "API information");
        endpoints.put("GET /health", "Health check");
        endpoints.put("GET /ads", "Get ads by context keys (query: contextKeys)");
        endpoints.put("POST /ads", "Get ads by context keys (body: { contextKeys: [] })");
        endpoints.put("GET /ads/random", "Get random ads (query: count)");
        endpoints.put("GET /ads/categories", "Get all available categories");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }

    /**
     * GET /health
     * Health check endpoint
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "ad-service");
        health.put("timestamp", LocalDateTime.now());
        health.put("adCount", adService.getAdCount());
        
        return ResponseEntity.ok(health);
    }

    /**
     * GET /ads
     * Get ads based on optional context keys from query parameters
     * 
     * Query params:
     *   - contextKeys: List of categories (e.g., ?contextKeys=clothing&contextKeys=kitchen)
     * 
     * Example requests:
     *   - GET /ads
     *   - GET /ads?contextKeys=clothing&contextKeys=kitchen
     * 
     * @param contextKeys Optional list of context keys
     * @return AdResponseDto containing relevant ads
     */
    @GetMapping("/ads")
    public ResponseEntity<AdResponseDto> getAds(
            @RequestParam(required = false) List<String> contextKeys) {
        
        logger.info("GET /ads called with contextKeys: {}", contextKeys);
        
        AdRequestDto request = new AdRequestDto();
        request.setContextKeys(contextKeys);
        
        AdResponseDto response = adService.getAds(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /ads
     * Get ads based on context keys from request body
     * 
     * Request body:
     *   { "contextKeys": ["clothing", "kitchen"] }
     * 
     * @param request AdRequestDto containing context keys
     * @return AdResponseDto containing relevant ads
     */
    @PostMapping("/ads")
    public ResponseEntity<AdResponseDto> postAds(@RequestBody AdRequestDto request) {
        logger.info("POST /ads called with request: {}", request);
        
        AdResponseDto response = adService.getAds(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /ads/random
     * Get random ads (fallback endpoint)
     * 
     * Query params:
     *   - count: number of ads to return (optional, default: 2)
     * 
     * Example requests:
     *   - GET /ads/random
     *   - GET /ads/random?count=5
     * 
     * @param count Optional number of ads to return
     * @return AdResponseDto containing random ads
     */
    @GetMapping("/ads/random")
    public ResponseEntity<AdResponseDto> getRandomAds(
            @RequestParam(required = false) Integer count) {
        
        logger.info("GET /ads/random called with count: {}", count);
        
        // Validate count if provided
        if (count != null && count < 1) {
            throw new IllegalArgumentException("Count must be a positive integer");
        }
        
        AdResponseDto response = adService.getRandomAds(count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /ads/categories
     * Get all available ad categories
     * 
     * @return List of all category names
     */
    @GetMapping("/ads/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        logger.info("GET /ads/categories called");
        
        List<String> categories = adService.getAllCategories();
        
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("count", categories.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /ads/count
     * Get total ad count
     * 
     * @return Total number of ads
     */
    @GetMapping("/ads/count")
    public ResponseEntity<Map<String, Object>> getAdCount() {
        logger.info("GET /ads/count called");
        
        int count = adService.getAdCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalAds", count);
        
        return ResponseEntity.ok(response);
    }
}
