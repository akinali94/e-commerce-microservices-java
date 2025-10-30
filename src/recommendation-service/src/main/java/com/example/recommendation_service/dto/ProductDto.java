package com.example.recommendation_service.dto;

import java.util.List;

/**
 * DTO for Product information from the product catalog service.
 */
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private String picture;
    private MoneyDto priceUsd;
    private List<String> categories;

    public ProductDto() {
    }

    public ProductDto(String id, String name, String description, String picture, MoneyDto priceUsd, List<String> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.priceUsd = priceUsd;
        this.categories = categories;
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

    public MoneyDto getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(MoneyDto priceUsd) {
        this.priceUsd = priceUsd;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                ", priceUsd=" + priceUsd +
                ", categories=" + categories +
                '}';
    }
}