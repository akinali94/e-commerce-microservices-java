package com.example.ad_service.repository;

import com.hipstershop.adservice.model.Ad;
import com.hipstershop.adservice.model.AdCategory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Repository for managing ad data
 * In-memory database organized by category
 */
@Repository
public class AdRepository {

    private static final int MAX_ADS_TO_SERVE = 2;
    private final Map<AdCategory, List<Ad>> adDatabase;
    private final Random random;


    public AdRepository() {
        this.random = new Random();
        this.adDatabase = initializeAdDatabase();
    }


    private Map<AdCategory, List<Ad>> initializeAdDatabase() {
        Map<AdCategory, List<Ad>> database = new EnumMap<>(AdCategory.class);

        // Clothing
        database.put(AdCategory.CLOTHING, Arrays.asList(
                new Ad("/product/66VCHSJNUP", "Tank top for sale. 20% off.")
        ));

        // Accessories
        database.put(AdCategory.ACCESSORIES, Arrays.asList(
                new Ad("/product/1YMWWN1N4O", "Watch for sale. Buy one, get second kit for free")
        ));

        // Footwear
        database.put(AdCategory.FOOTWEAR, Arrays.asList(
                new Ad("/product/L9ECAV7KIM", "Loafers for sale. Buy one, get second one for free")
        ));

        // Hair
        database.put(AdCategory.HAIR, Arrays.asList(
                new Ad("/product/2ZYFJ3GM2N", "Hairdryer for sale. 50% off.")
        ));

        // Decor
        database.put(AdCategory.DECOR, Arrays.asList(
                new Ad("/product/0PUK6V6EV0", "Candle holder for sale. 30% off.")
        ));

        // Kitchen
        database.put(AdCategory.KITCHEN, Arrays.asList(
                new Ad("/product/9SIQT8TOJO", "Bamboo glass jar for sale. 10% off."),
                new Ad("/product/6E92ZMYYFZ", "Mug for sale. Buy two, get third one for free")
        ));

        return database;
    }


    public List<Ad> findAll() {
        List<Ad> allAds = new ArrayList<>();
        for (List<Ad> ads : adDatabase.values()) {
            allAds.addAll(ads);
        }
        return allAds;
    }

    /**
     * Get ads by a specific category
     * @param category The ad category to search for
     * @return List of ads in that category (empty list if not found)
     */
    public List<Ad> findByCategory(AdCategory category) {
        List<Ad> ads = adDatabase.get(category);
        return ads != null ? new ArrayList<>(ads) : new ArrayList<>();
    }

    /**
     * Get ads by a category string (case-insensitive)
     * @param categoryString The category string
     * @return List of ads in that category (empty list if not found or invalid)
     */
    public List<Ad> findByCategoryString(String categoryString) {
        AdCategory category = AdCategory.fromValue(categoryString);
        if (category == null) {
            return new ArrayList<>();
        }
        return findByCategory(category);
    }

    /**
     * Get ads by multiple categories
     * @param categories List of category strings
     * @return Combined list of ads from all matching categories
     */
    public List<Ad> findByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<Ad> ads = new ArrayList<>();
        for (String categoryString : categories) {
            ads.addAll(findByCategoryString(categoryString));
        }
        return ads;
    }

    /**
     * Get random ads from the database
     * @param count Number of random ads to return
     * @return List of randomly selected ads
     */
    public List<Ad> findRandomAds(int count) {
        List<Ad> allAds = findAll();
        
        if (allAds.isEmpty()) {
            return new ArrayList<>();
        }

        // If we don't have enough ads, return all
        if (allAds.size() <= count) {
            return new ArrayList<>(allAds);
        }

        // Select random ads
        List<Ad> selectedAds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(allAds.size());
            selectedAds.add(allAds.get(randomIndex));
        }

        return selectedAds;
    }

    /**
     * Get random ads with default count
     * @return List of randomly selected ads (default: MAX_ADS_TO_SERVE)
     */
    public List<Ad> findRandomAds() {
        return findRandomAds(MAX_ADS_TO_SERVE);
    }

    /**
     * Check if a category exists in the database
     * @param categoryString Category string to check
     * @return true if category exists, false otherwise
     */
    public boolean categoryExists(String categoryString) {
        return AdCategory.isValid(categoryString);
    }

    /**
     * Get all available categories
     * @return List of all category strings
     */
    public List<String> findAllCategories() {
        List<String> categories = new ArrayList<>();
        for (AdCategory category : AdCategory.values()) {
            categories.add(category.getValue());
        }
        return categories;
    }

    /**
     * Get the total count of ads in the database
     * @return Total number of ads
     */
    public int count() {
        return findAll().size();
    }

    /**
     * Get the max ads to serve configuration
     * @return Maximum number of ads to serve
     */
    public int getMaxAdsToServe() {
        return MAX_ADS_TO_SERVE;
    }
}