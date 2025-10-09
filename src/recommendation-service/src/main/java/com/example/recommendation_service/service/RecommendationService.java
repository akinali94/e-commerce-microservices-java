package com.example.recommendation_service.service;

import com.hipstershop.recommendationservice.model.ListRecommendationsRequest;
import com.hipstershop.recommendationservice.model.ListRecommendationsResponse;
import com.hipstershop.recommendationservice.model.Product;
import com.hipstershop.recommendationservice.model.ProductCatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    
    private final ProductCatalogClient productCatalogClient;
    private final int maxRecommendations;
    private final Random random;
    
    public RecommendationService(
ProductCatalogClient productCatalogClient,
            @Value("${recommendation.max:5}") int maxRecommendations) {
        this.productCatalogClient = productCatalogClient;
        this.maxRecommendations = maxRecommendations;
        this.random = new Random();
    }
    
    /**
     * Lists product recommendations for a user
     */
    public ListRecommendationsResponse listRecommendations(ListRecommendationsRequest request) {
        logger.info("[Recv ListRecommendations] userId={}, productIdsCount={}", 
                request.getUserId(), 
                request.getProductIds().size());
        
        List<Product> allProducts = productCatalogClient.listProducts();
        
        List<Product> unseenProducts = filterSeenProducts(allProducts, request.getProductIds());
        
        List<Product> recommendedProducts = sampleProducts(unseenProducts, maxRecommendations);
        
        List<String> productIds = extractProductIds(recommendedProducts);
        
        logger.info("[Send ListRecommendations] userId={}, recommendedProducts={}", 
                request.getUserId(), 
                productIds);
        
        return new ListRecommendationsResponse(productIds);
    }
    

    private List<Product> filterSeenProducts(List<Product> allProducts, List<String> seenProductIds) {
        Set<String> seenSet = new HashSet<>(seenProductIds);
        
        return allProducts.stream()
                .filter(product -> !seenSet.contains(product.getId()))
                .collect(Collectors.toList());
    }
    

    private List<Product> sampleProducts(List<Product> products, int count) {
        int numProducts = products.size();
        int numToReturn = Math.min(count, numProducts);
        
        if (numToReturn == 0) {
            return new ArrayList<>();
        }
        
        List<Product> shuffled = new ArrayList<>(products);
        
        // Fisher-Yates shuffle
        for (int i = numProducts - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Product temp = shuffled.get(i);
            shuffled.set(i, shuffled.get(j));
            shuffled.set(j, temp);
        }
        
        return shuffled.subList(0, numToReturn);
    }
    

    private List<String> extractProductIds(List<Product> products) {
        return products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }
}