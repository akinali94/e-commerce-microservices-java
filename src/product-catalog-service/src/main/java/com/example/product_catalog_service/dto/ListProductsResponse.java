package com.example.product_catalog_service.dto;

import com.example.product_catalog_service.model.Product;
import java.util.List;

public class ListProductsResponse {
    private List<Product> products;

    public ListProductsResponse() {
    }

    public ListProductsResponse(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}