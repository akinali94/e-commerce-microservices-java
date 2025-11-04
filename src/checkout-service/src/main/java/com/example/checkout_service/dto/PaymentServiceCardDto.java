package com.example.checkout_service.dto;

/**
 * DTO that matches the payment service's expected credit card format
 */
public class PaymentServiceCardDto {
    private String cardNo;
    private String cvv;
    private String expirationDate;
    
    /**
     * Default constructor
     */
    public PaymentServiceCardDto() {
    }
    
    /**
     * Full constructor
     * 
     * @param cardNo Credit card number
     * @param cvv Credit card verification value
     * @param expirationDate Credit card expiration date in "MM/YYYY" format
     */
    public PaymentServiceCardDto(String cardNo, String cvv, String expirationDate) {
        this.cardNo = cardNo;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
    }
    
    /**
     * Get the credit card number
     * 
     * @return Credit card number
     */
    public String getCardNo() {
        return cardNo;
    }
    
    /**
     * Set the credit card number
     * 
     * @param cardNo Credit card number
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
    
    /**
     * Get the credit card verification value
     * 
     * @return CVV code
     */
    public String getCvv() {
        return cvv;
    }
    
    /**
     * Set the credit card verification value
     * 
     * @param cvv CVV code
     */
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    /**
     * Get the credit card expiration date
     * 
     * @return Expiration date in "MM/YYYY" format
     */
    public String getExpirationDate() {
        return expirationDate;
    }
    
    /**
     * Set the credit card expiration date
     * 
     * @param expirationDate Expiration date in "MM/YYYY" format
     */
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    @Override
    public String toString() {
        // Mask card number for security in logs
        String maskedCardNo = null;
        if (cardNo != null && cardNo.length() > 4) {
            maskedCardNo = "XXXX-XXXX-XXXX-" + cardNo.substring(cardNo.length() - 4);
        } else {
            maskedCardNo = "[invalid card number]";
        }
        
        return "PaymentServiceCardDTO{" +
                "cardNo='" + maskedCardNo + '\'' +
                ", cvv='***'" +
                ", expirationDate='" + expirationDate + '\'' +
                '}';
    }
}