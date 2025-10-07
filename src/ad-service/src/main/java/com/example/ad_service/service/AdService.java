
package com.example.ad_service.service;

import com.example.ad_service.dto.AdRequestDto;
import com.example.ad_service.dto.AdResponseDto;

import java.util.List;

/**
 * Service interface for ad operations
 */
public interface AdService {
    
    /**
     * Get ads based on context keys
     * @param request Request containing optional context keys
     * @return Response containing relevant ads
     */
    AdResponseDto getAds(AdRequestDto request);

    /**
     * Get random ads
     * @param count Number of ads to return
     * @return Response containing random ads
     */
    AdResponseDto getRandomAds(Integer count);

    /**
     * Get all available categories
     * @return List of all category names
     */
    List<String> getAllCategories();

    /**
     * Get total ad count
     * @return Total number of ads in the database
     */
    int getAdCount();
}