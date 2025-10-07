package com.example.ad_service.model;

public enum AdCategory {
    CLOTHING("clothing"),
    ACCESSORIES("accessories"),
    FOOTWEAR("footwear"),
    HAIR("hair"),
    DECOR("decor"),
    KITCHEN("kitchen");

    private final String value;

    AdCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get AdCategory from string value (case-insensitive)
     * @param value the category string
     * @return AdCategory or null if not found
     */
    public static AdCategory fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (AdCategory category : AdCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Check if a string is a valid category
     * @param value the category string
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String value) {
        return fromValue(value) != null;
    }

    @Override
    public String toString() {
        return this.value;
    }
}