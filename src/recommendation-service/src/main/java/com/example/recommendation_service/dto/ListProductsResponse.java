package com.example.recommendation_service.dto;

import java.util.List;

/**
 * DTO for the response from the product catalog service containing a list of products.
 */
public class ListProductsResponse {
    private List<ProductDto> products;

    public ListProductsResponse() {
    }

    public ListProductsResponse(List<ProductDto> products) {
        this.products = products;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ListProductsResponse{" +
                "products=" + products +
                '}';
    }
}