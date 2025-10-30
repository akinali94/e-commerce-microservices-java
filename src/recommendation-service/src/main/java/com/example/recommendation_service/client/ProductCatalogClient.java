package com.example.recommendation_service.client;

import com.example.recommendation_service.dto.ListProductsResponse;

/**
 * Client interface for interacting with the Product Catalog Service.
 */
public interface ProductCatalogClient {
    
    /**
     * Fetches all products from the product catalog service.
     *
     * @return ListProductsResponse containing all available products
     */
    ListProductsResponse listProducts();
}