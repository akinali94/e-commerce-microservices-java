package com.example.ad_service.dto;

import com.example.ad_service.model.Ad;
import java.util.List;
import java.util.Objects;


public class AdResponseDto {
    private List<Ad> ads;

    public AdResponseDto() {
    }

    public AdResponseDto(List<Ad> ads) {
        this.ads = ads;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdResponseDto that = (AdResponseDto) o;
        return Objects.equals(ads, that.ads);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ads);
    }

    @Override
    public String toString() {
        return "AdResponseDto{" +
               "ads=" + ads +
               '}';
    }
}