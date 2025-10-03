package com.example.payment_service.util;

import com.example.payment_service.model.CardDetails;
import com.example.payment_service.model.CreditCard;;;

public class CreditCardValidity {

    private String MasterCardOne = "^5[1-5].*";
    private String MasterCardTwo = "^2[2-7][0-9][0-9].*";
    private String Visa = "4";
    private String AmericanExpress = "^3[47].*";

    public CardDetails cardValidation(CreditCard card){
        boolean isValid = validate(card.getCardNo());
        String cardType = getCardType(card.getCardNo());
        boolean isNotExpired = isNotExpired(card.getExpirationDate());
        boolean isCvvValid = isCvvValid(card.getCvv(), cardType);
        
        boolean validity = true;

        if(!isValid || !isCvvValid){
            validity = false;
        }

        return new CardDetails(cardType, validity, isNotExpired);
    }

    private boolean validate(String cardNumber){
        if(cardNumber == null || cardNumber.isEmpty()){
            return false;
        }

        cardNumber = cardNumber.replaceAll("[\\s-]", "");

        if(!cardNumber.matches("\\d+")){
            return false;
        }

        int length = cardNumber.length();
        if(length < 13 || length > 19){
            return false;
        }

        return luhnAlgorithmCheck(cardNumber);
    }

    private boolean luhnAlgorithmCheck(String cardNumber){
        int total = 0;
        boolean alternative = false;

        for(int i = cardNumber.length()-1; i>=0; i--){
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if(alternative){
                digit *= 2;
                if(digit > 9){
                    digit -= 9;
                }
            }

            total += digit;
            alternative = !alternative;
        }

        return (total % 10 == 0);
    }


    private String getCardType(String cardNo) {
        if (cardNo == null) {
            return "Not Valid";
        }
        
        cardNo = cardNo.replaceAll("[\\s-]", "");
        
        if (!validate(cardNo)) {
            return "Not Valid";
        }
        
        // For Visa: 4
        if (cardNo.startsWith(Visa)) {
            return "Visa";
        }
        
        // For Mastercard: 51-55 or 2221-2720
        if (cardNo.matches(MasterCardOne) || 
            cardNo.matches(MasterCardTwo)) {
            return "Mastercard";
        }
        
        // American Express: 34 or 37
        if (cardNo.matches(AmericanExpress)) {
            return "American Express";
        }
        
        
        return "Not Valid";
    }

    public static boolean isCvvValid(String cvv, String cardType) {
        if (cvv == null || !cvv.matches("\\d+")) {
            return false;
        }
        
        // American Express is using 4 digits CVV
        if (cardType.equals("American Express")) {
            return cvv.length() == 4;
        }
        
        return cvv.length() == 3;
    }
    
    private boolean isNotExpired(String expiryDate) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            return false;
        }
        
        // MM/YY or MM/YYYY format
        if (!expiryDate.matches("\\d{2}/\\d{2,4}")) {
            return false;
        }
        
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        

        if (month < 1 || month > 12) {
            return false;
        }
        
        // convert YY to YYYY
        if (year < 100) {
            year += 2000;
        }
        
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        
        if (year < currentYear) {
            return false;
        }
        
        if (year == currentYear && month < currentMonth) {
            return false;
        }
        
        // More than ten years
        if (year > currentYear + 10) {
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
        String masked = "**** **** **** " + lastFour;
        
        return masked;
    }
}