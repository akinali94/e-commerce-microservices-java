package com.example.recommendation_service.service;

import com.example.recommendation_service.client.ProductCatalogClient;
import com.example.recommendation_service.dto.ListProductsResponse;
import com.example.recommendation_service.dto.ListRecommendationsRequest;
import com.example.recommendation_service.dto.ListRecommendationsResponse;
import com.example.recommendation_service.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementation of the RecommendationService interface.
 * Provides product recommendations based on user ID and their current product selection.
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private final ProductCatalogClient productCatalogClient;
    private final int maxRecommendations;
    private final Random random;

    /**
     * Constructor with dependency injection.
     *
     * @param productCatalogClient client for the product catalog service
     * @param maxRecommendations maximum number of recommendations to return
     */
    public RecommendationServiceImpl(
            ProductCatalogClient productCatalogClient,
            @Value("${recommendations.max:5}") int maxRecommendations) {
        this.productCatalogClient = productCatalogClient;
        this.maxRecommendations = maxRecommendations;
        this.random = new Random();
    }

    @Override
    public ListRecommendationsResponse listRecommendations(ListRecommendationsRequest request) {
        logger.info("Received recommendation request for user: {} with product IDs: {}", 
                request.getUserId(), request.getProductIds());
        
        // Fetch all products from catalog
        ListProductsResponse catalogResponse = productCatalogClient.listProducts();
        if (catalogResponse == null || catalogResponse.getProducts() == null) {
            logger.warn("Received null or empty product list from catalog");
            return new ListRecommendationsResponse(Collections.emptyList());
        }
        
        // Extract all product IDs
        List<String> allProductIds = catalogResponse.getProducts().stream()
                .map(ProductDto::getId)
                .collect(Collectors.toList());
        
        // Filter out products that are already in the request
        List<String> filteredProducts = new ArrayList<>(allProductIds);
        filteredProducts.removeAll(request.getProductIds());
        
        // Generate recommendations
        List<String> recommendedProducts = selectRandomProducts(filteredProducts);
        
        logger.info("Returning {} recommended products for user {}: {}", 
                recommendedProducts.size(), request.getUserId(), recommendedProducts);
        
        return new ListRecommendationsResponse(recommendedProducts);
    }
    
    /**
     * Selects random products from the filtered product list.
     *
     * @param filteredProducts list of product IDs not in the user's current selection
     * @return list of randomly selected product IDs
     */
    private List<String> selectRandomProducts(List<String> filteredProducts) {
        int numProducts = filteredProducts.size();
        if (numProducts == 0) {
            return Collections.emptyList();
        }
        
        int numToReturn = Math.min(maxRecommendations, numProducts);
        if (numToReturn == numProducts) {
            return new ArrayList<>(filteredProducts);
        }
        
        // Create a copy of the list to shuffle
        List<String> productsCopy = new ArrayList<>(filteredProducts);
        Collections.shuffle(productsCopy, random);
        
        // Return the first n elements
        return productsCopy.subList(0, numToReturn);
    }
}