package com.example.payment_service.util;

import com.example.payment_service.model.CardDetails;
import com.example.payment_service.model.CreditCard;
import com.example.payment_service.exception.InvalidCreditCardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditCardValidity {
    private static final Logger log = LoggerFactory.getLogger(CreditCardValidity.class);

    private static final String MASTERCARD_PATTERN_1 = "^5[1-5].*";
    private static final String MASTERCARD_PATTERN_2 = "^2[2-7][0-9][0-9].*";
    private static final String VISA_PREFIX = "4";
    private static final String AMEX_PATTERN = "^3[47].*";

    public CardDetails cardValidation(CreditCard card) {
        if (card == null) {
            log.error("Card validation failed: Card object is null");
            throw new InvalidCreditCardException("Credit card information cannot be null");
        }

        log.info("Validating credit card: {}", mask(card.getCardNo()));
        
        boolean isValid = validate(card.getCardNo());
        String cardType = getCardType(card.getCardNo());
        boolean isNotExpired = isNotExpired(card.getExpirationDate());
        boolean isCvvValid = isCvvValid(card.getCvv(), cardType);
        
        boolean validity = isValid && isCvvValid;

        if (!validity) {
            log.warn("Card validation failed: valid={}, cvvValid={}", isValid, isCvvValid);
        }
        
        if (!isNotExpired) {
            log.warn("Card has expired: {}", card.getExpirationDate());
        }
        
        CardDetails result = new CardDetails(cardType, validity, isNotExpired);
        log.debug("Card validation completed: type={}, valid={}, notExpired={}", 
                 cardType, validity, isNotExpired);
        
        return result;
    }

    private boolean validate(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            log.debug("Card number validation failed: null or empty");
            return false;
        }

        cardNumber = cardNumber.replaceAll("[\\s-]", "");

        if (!cardNumber.matches("\\d+")) {
            log.debug("Card number validation failed: contains non-digit characters");
            return false;
        }

        int length = cardNumber.length();
        if (length < 13 || length > 19) {
            log.debug("Card number validation failed: invalid length {}", length);
            return false;
        }

        boolean luhnCheck = luhnAlgorithmCheck(cardNumber);
        if (!luhnCheck) {
            log.debug("Card number validation failed: Luhn algorithm check failed");
        }
        
        return luhnCheck;
    }

    private boolean luhnAlgorithmCheck(String cardNumber) {
        int total = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            total += digit;
            alternate = !alternate;
        }

        return (total % 10 == 0);
    }

    private String getCardType(String cardNo) {
        if (cardNo == null) {
            log.debug("Card type determination failed: card number is null");
            return "Not Valid";
        }
        
        cardNo = cardNo.replaceAll("[\\s-]", "");
        
        if (!validate(cardNo)) {
            return "Not Valid";
        }
        
        if (cardNo.startsWith(VISA_PREFIX)) {
            return "Visa";
        }
        
        if (cardNo.matches(MASTERCARD_PATTERN_1) || cardNo.matches(MASTERCARD_PATTERN_2)) {
            return "Mastercard";
        }
        
        if (cardNo.matches(AMEX_PATTERN)) {
            return "American Express";
        }
        
        log.debug("Unknown card type for card number pattern");
        return "Not Valid";
    }

    private boolean isCvvValid(String cvv, String cardType) {
        if (cvv == null || !cvv.matches("\\d+")) {
            log.debug("CVV validation failed: null or contains non-digit characters");
            return false;
        }
        
        // American Express uses 4 digits CVV
        if ("American Express".equals(cardType)) {
            boolean isValid = cvv.length() == 4;
            if (!isValid) {
                log.debug("CVV validation failed: American Express requires 4 digits");
            }
            return isValid;
        }
        
        boolean isValid = cvv.length() == 3;
        if (!isValid) {
            log.debug("CVV validation failed: Card type {} requires 3 digits", cardType);
        }
        return isValid;
    }
    
    private boolean isNotExpired(String expiryDate) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            log.debug("Expiry date validation failed: null or empty");
            return false;
        }
        
        // MM/YY or MM/YYYY format
        if (!expiryDate.matches("\\d{2}/\\d{2,4}")) {
            log.debug("Expiry date validation failed: invalid format {}", expiryDate);
            return false;
        }
        
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        
        if (month < 1 || month > 12) {
            log.debug("Expiry date validation failed: invalid month {}", month);
            return false;
        }
        
        // Convert YY to YYYY
        if (year < 100) {
            year += 2000;
        }
        
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        
        if (year < currentYear) {
            log.debug("Card expired: year {} is less than current year {}", year, currentYear);
            return false;
        }
        
        if (year == currentYear && month < currentMonth) {
            log.debug("Card expired: current date is {}/{}", currentMonth, currentYear);
            return false;
        }
        
        // More than ten years
        if (year > currentYear + 10) {
            log.debug("Expiry date validation failed: year {} is more than 10 years in future", year);
            return false;
        }
        
        return true;
    }
    
    public String mask(String cardNo) {
        if (cardNo == null || cardNo.length() < 4) {
            return "****";
        }
        
        cardNo = cardNo.replaceAll("[\\s-]", "");
        int length = cardNo.length();
        String lastFour = cardNo.substring(length - 4);
        
        return "**** **** **** " + lastFour;
    }
}