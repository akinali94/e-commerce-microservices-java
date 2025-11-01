package com.example.checkout_service.service;

import com.example.checkout_service.model.Money;

public interface ProductCatalogService {
    Product getProduct(String productId);
    
    public class Product {
        private String id;
        private String name;
        private String description;
        private String picture;
        private Money priceUsd;
        
        public Product() {
        }
        
        public Product(String id, String name, String description, String picture, Money priceUsd) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.picture = picture;
            this.priceUsd = priceUsd;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getPicture() {
            return picture;
        }
        
        public void setPicture(String picture) {
            this.picture = picture;
        }
        
        public Money getPriceUsd() {
            return priceUsd;
        }
        
        public void setPriceUsd(Money priceUsd) {
            this.priceUsd = priceUsd;
        }
        
        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", picture='" + picture + '\'' +
                    ", priceUsd=" + priceUsd +
                    '}';
        }
    }
}