package com.example.checkout_service.service;

import com.example.checkout_service.model.Product;
import java.util.List;

public interface ProductCatalogService {
    Product getProduct(String productId);
    List<Product> getMultipleProducts(List<String> productIds);


}