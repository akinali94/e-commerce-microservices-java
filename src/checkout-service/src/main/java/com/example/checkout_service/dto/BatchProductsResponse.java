package com.example.checkout_service.dto;

import com.example.checkout_service.model.Product;
import java.util.List;

/**
 * Data Transfer Object for batch product response from the Product Catalog Service API.
 */
public class BatchProductsResponse {
    private List<Product> products;
    
    // Default constructor needed for JSON deserialization
    public BatchProductsResponse() {
    }
    
    public BatchProductsResponse(List<Product> products) {
        this.products = products;
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    @Override
    public String toString() {
        return "BatchProductsResponse{" +
                "products=" + products +
                '}';
    }
}