package com.example.ad_service.service;

import com.example.ad_service.dto.AdRequestDto;
import com.example.ad_service.dto.AdResponseDto;
import com.example.ad_service.exception.InvalidRequestException;
import com.example.ad_service.model.Ad;
import com.example.ad_service.repository.AdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of AdService
 * Contains business logic for serving ads
 */
@Service
public class AdServiceImpl implements AdService {

    private static final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    
    private final AdRepository adRepository;

    public AdServiceImpl(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    /**
     * Core business logic for getting ads based on context keys
     * 
     * Logic flow:
     * 1. Validate request
     * 2. If context keys provided -> get ads by those categories
     * 3. If no context keys OR no matching ads -> return random ads
     * 
     * @param request AdRequestDto containing optional context keys
     * @return AdResponseDto containing relevant ads
     */
    @Override
    public AdResponseDto getAds(AdRequestDto request) {
        // Validate request
        if (request == null) {
            logger.error("Ad request is null");
            throw new InvalidRequestException("Ad request cannot be null");
        }

        List<String> contextKeys = request.getContextKeys();
        logger.info("Received ad request with context keys: {}", 
                    contextKeys != null ? contextKeys : "none");

        List<Ad> ads;

        // If context keys are provided, try to get ads by categories
        if (contextKeys != null && !contextKeys.isEmpty()) {
            ads = adRepository.findByCategories(contextKeys);
            logger.debug("Found {} ads for context keys: {}", ads.size(), contextKeys);
        } else {
            ads = List.of(); // Empty list
        }

        // If no ads found or no context keys provided, return random ads
        if (ads.isEmpty()) {
            logger.debug("No ads found or no context keys provided, returning random ads");
            ads = adRepository.findRandomAds();
        }

        logger.info("Returning {} ads", ads.size());
        
        AdResponseDto response = new AdResponseDto();
        response.setAds(ads);
        return response;
    }

    /**
     * Get random ads directly (without context)
     * @param count Optional number of ads to return
     * @return AdResponseDto containing random ads
     */
    @Override
    public AdResponseDto getRandomAds(Integer count) {
        logger.info("Getting random ads with count: {}", count);

        List<Ad> ads;
        if (count != null && count > 0) {
            ads = adRepository.findRandomAds(count);
        } else {
            ads = adRepository.findRandomAds();
        }

        logger.info("Returning {} random ads", ads.size());

        AdResponseDto response = new AdResponseDto();
        response.setAds(ads);
        return response;
    }

    /**
     * Get all available categories
     * @return List of all category names
     */
    @Override
    public List<String> getAllCategories() {
        logger.debug("Getting all categories");
        return adRepository.findAllCategories();
    }

    /**
     * Get total ad count
     * @return Total number of ads in the database
     */
    @Override
    public int getAdCount() {
        logger.debug("Getting total ad count");
        return adRepository.count();
    }
}