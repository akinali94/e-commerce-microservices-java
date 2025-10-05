package com.example.product_catalog_service.dto;

import com.example.product_catalog_service.model.Product;
import java.util.List;

public class SearchProductsResponse {
    private List<Product> results;

    public SearchProductsResponse() {
    }

    public SearchProductsResponse(List<Product> results) {
        this.results = results;
    }

    public List<Product> getResults() {
        return results;
    }

    public void setResults(List<Product> results) {
        this.results = results;
    }
}