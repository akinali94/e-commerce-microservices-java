package com.example.checkout_service.util;

import com.example.checkoutservice.model.Address;
import com.example.checkoutservice.model.CreditCardInfo;
import com.example.checkoutservice.model.Money;

import java.time.Year;
import java.util.regex.Pattern;


public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    
    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validates an email address.
     * 
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates a currency code (ISO 4217).
     * 
     * @param currencyCode the currency code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isEmpty()) {
            return false;
        }
        return CURRENCY_CODE_PATTERN.matcher(currencyCode).matches();
    }
    
    /**
     * Validates an address.
     * 
     * @param address the address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAddress(Address address) {
        if (address == null) {
            return false;
        }
        
        return address.getStreetAddress() != null && !address.getStreetAddress().isEmpty() &&
               address.getCity() != null && !address.getCity().isEmpty() &&
               address.getCountry() != null && !address.getCountry().isEmpty() &&
               address.getZipCode() != null && address.getZipCode() > 0;
    }
    
    /**
     * Validates credit card information.
     * Note: This is a basic validation. In production, use a proper payment validation library.
     * 
     * @param creditCard the credit card info to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCreditCard(CreditCardInfo creditCard) {
        if (creditCard == null) {
            return false;
        }
        
        // Check card number (basic length check)
        String cardNumber = creditCard.getCreditCardNumber();
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }
        
        // Check CVV
        Integer cvv = creditCard.getCreditCardCvv();
        if (cvv == null || cvv < 100 || cvv > 9999) {
            return false;
        }
        
        // Check expiration
        Integer year = creditCard.getCreditCardExpirationYear();
        Integer month = creditCard.getCreditCardExpirationMonth();
        
        if (year == null || month == null) {
            return false;
        }
        
        if (month < 1 || month > 12) {
            return false;
        }
        
        int currentYear = Year.now().getValue();
        if (year < currentYear) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates a Money object.
     * 
     * @param money the money to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidMoney(Money money) {
        return MoneyUtil.isValid(money);
    }
    
    /**
     * Validates a user ID.
     * 
     * @param userId the user ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUserId(String userId) {
        return userId != null && !userId.trim().isEmpty();
    }
    
    /**
     * Validates a product ID.
     * 
     * @param productId the product ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidProductId(String productId) {
        return productId != null && !productId.trim().isEmpty();
    }
    
    /**
     * Sanitizes a string by removing null bytes and trimming.
     * 
     * @param input the string to sanitize
     * @return sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\0", "").trim();
    }
}