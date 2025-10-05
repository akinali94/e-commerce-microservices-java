package com.example.shipping_service.util;

import com.example.shipping_service.model.Address;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TrackerUtil {
    
    private final Random random = new Random();
    
    /**
     * Generates a tracking ID based on address
     * Format: XX-<length><3digits>-<length/2><7digits>
     * Example: AB-15234-71234567
     */
    public String createTrackingId(String salt) {
        char letter1 = getRandomLetterCode();
        char letter2 = getRandomLetterCode();
        int length = salt.length();
        String random3 = getRandomNumber(3);
        int halfLength = length / 2;
        String random7 = getRandomNumber(7);
        
        return String.format("%c%c-%d%s-%d%s", 
            letter1, letter2, length, random3, halfLength, random7);
    }
    
    /**
     * Generates a random capital letter (A-Z)
     */
    private char getRandomLetterCode() {
        return (char) (65 + random.nextInt(26));
    }
    
    /**
     * Generates a random number string with specified digits
     */
    private String getRandomNumber(int digits) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
    
    /**
     * Formats an address as a base string for tracking ID generation
     */
    public String formatAddressForTracking(Address address) {
        return String.format("%s, %s, %s", 
            address.getStreetAddress(), 
            address.getCity(), 
            address.getState());
    }
}
