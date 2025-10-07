package com.example.ad_service.dto;

import java.util.List;
import java.util.Objects;


public class AdRequestDto {
    private List<String> contextKeys;

    public AdRequestDto() {
    }

    public AdRequestDto(List<String> contextKeys) {
        this.contextKeys = contextKeys;
    }

    public List<String> getContextKeys() {
        return contextKeys;
    }

    public void setContextKeys(List<String> contextKeys) {
        this.contextKeys = contextKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdRequestDto that = (AdRequestDto) o;
        return Objects.equals(contextKeys, that.contextKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextKeys);
    }

    @Override
    public String toString() {
        return "AdRequestDto{" +
               "contextKeys=" + contextKeys +
               '}';
    }
}