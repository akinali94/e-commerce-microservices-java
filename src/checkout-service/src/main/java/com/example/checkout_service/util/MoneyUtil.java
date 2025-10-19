package com.example.checkout_service.util;

import com.example.checkout_service.exception.CurrencyMismatchException;
import com.example.checkout_service.exception.InvalidMoneyException;
import com.example.checkout_service.model.Money;


public class MoneyUtil {
    
    private static final int NANOS_MIN = -999999999;
    private static final int NANOS_MAX = 999999999;
    private static final int NANOS_MOD = 1000000000;
    
    private MoneyUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validates if specified value has valid units/nanos signs and ranges.
     */
    public static boolean isValid(Money money) {
        if (money == null || money.getUnits() == null || money.getNanos() == null) {
            return false;
        }
        return signMatches(money) && validNanos(money.getNanos());
    }
    
    /**
     * Checks if units and nanos have matching signs.
     */
    private static boolean signMatches(Money money) {
        int nanos = money.getNanos();
        long units = money.getUnits();
        return nanos == 0 || units == 0 || (nanos < 0) == (units < 0);
    }
    
    /**
     * Validates if nanos is within valid range.
     */
    private static boolean validNanos(int nanos) {
        return NANOS_MIN <= nanos && nanos <= NANOS_MAX;
    }
    
    /**
     * Returns true if the specified money value is equal to zero.
     */
    public static boolean isZero(Money money) {
        if (money == null) {
            return true;
        }
        return (money.getUnits() == null || money.getUnits() == 0) && 
               (money.getNanos() == null || money.getNanos() == 0);
    }
    
    /**
     * Returns true if the specified money value is valid and is positive.
     */
    public static boolean isPositive(Money money) {
        if (!isValid(money)) {
            return false;
        }
        return money.getUnits() > 0 || (money.getUnits() == 0 && money.getNanos() > 0);
    }
    
    /**
     * Returns true if the specified money value is valid and is negative.
     */
    public static boolean isNegative(Money money) {
        if (!isValid(money)) {
            return false;
        }
        return money.getUnits() < 0 || (money.getUnits() == 0 && money.getNanos() < 0);
    }
    
    /**
     * Returns true if values l and r have a currency code and they are the same.
     */
    public static boolean areSameCurrency(Money left, Money right) {
        if (left == null || right == null) {
            return false;
        }
        String leftCurrency = left.getCurrencyCode();
        String rightCurrency = right.getCurrencyCode();
        
        return leftCurrency != null && rightCurrency != null && 
               !leftCurrency.isEmpty() && !rightCurrency.isEmpty() &&
               leftCurrency.equals(rightCurrency);
    }
    
    /**
     * Returns true if values l and r are equal, including the currency.
     */
    public static boolean areEquals(Money left, Money right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        
        String leftCurrency = left.getCurrencyCode();
        String rightCurrency = right.getCurrencyCode();
        
        return (leftCurrency == null ? rightCurrency == null : leftCurrency.equals(rightCurrency)) &&
               (left.getUnits() == null ? right.getUnits() == null : left.getUnits().equals(right.getUnits())) &&
               (left.getNanos() == null ? right.getNanos() == null : left.getNanos().equals(right.getNanos()));
    }
    
    /**
     * Returns the same amount with the sign negated.
     */
    public static Money negate(Money money) {
        if (money == null) {
            return null;
        }
        
        return Money.builder()
                .units(money.getUnits() == null ? null : -money.getUnits())
                .nanos(money.getNanos() == null ? null : -money.getNanos())
                .currencyCode(money.getCurrencyCode())
                .build();
    }
    
    /**
     * Adds two money values.
     * Returns an error if one of the values is invalid or currency codes don't match.
     */
    public static Money sum(Money left, Money right) {
        if (!isValid(left) || !isValid(right)) {
            throw new InvalidMoneyException("One of the specified money values is invalid");
        }
        
        if (!left.getCurrencyCode().equals(right.getCurrencyCode())) {
            throw new CurrencyMismatchException("Mismatching currency codes");
        }
        
        long units = left.getUnits() + right.getUnits();
        int nanos = left.getNanos() + right.getNanos();
        
        if ((units == 0 && nanos == 0) || (units > 0 && nanos >= 0) || (units < 0 && nanos <= 0)) {
            // Same sign <units, nanos>
            units += nanos / NANOS_MOD;
            nanos = nanos % NANOS_MOD;
        } else {
            // Different sign. nanos guaranteed not to go over the limit
            if (units > 0) {
                units--;
                nanos += NANOS_MOD;
            } else {
                units++;
                nanos -= NANOS_MOD;
            }
        }
        
        return Money.builder()
                .units(units)
                .nanos(nanos)
                .currencyCode(left.getCurrencyCode())
                .build();
    }
    
    /**
     * Multiplies a money value by a quantity.
     * This is done by adding the value to itself n-1 times.
     */
    public static Money multiply(Money money, int multiplier) {
        if (!isValid(money)) {
            throw new InvalidMoneyException("The specified money value is invalid");
        }
        
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier must be non-negative");
        }
        
        if (multiplier == 0) {
            return Money.builder()
                    .units(0L)
                    .nanos(0)
                    .currencyCode(money.getCurrencyCode())
                    .build();
        }
        
        Money result = Money.builder()
                .units(money.getUnits())
                .nanos(money.getNanos())
                .currencyCode(money.getCurrencyCode())
                .build();
        
        for (int i = 1; i < multiplier; i++) {
            result = sum(result, money);
        }
        
        return result;
    }
    
    /**
     * Creates a Money object from a decimal value.
     * 
     * @param amount the decimal amount (e.g., 12.34)
     * @param currencyCode the currency code (e.g., "USD")
     * @return Money object
     */
    public static Money fromDecimal(double amount, String currencyCode) {
        long units = (long) amount;
        int nanos = (int) Math.round((amount - units) * NANOS_MOD);
        
        // Handle rounding edge case where nanos might be 1000000000
        if (nanos >= NANOS_MOD) {
            units++;
            nanos -= NANOS_MOD;
        } else if (nanos <= -NANOS_MOD) {
            units--;
            nanos += NANOS_MOD;
        }
        
        return Money.builder()
                .units(units)
                .nanos(nanos)
                .currencyCode(currencyCode)
                .build();
    }
    
    /**
     * Converts a Money object to a decimal value.
     * 
     * @param money the Money object
     * @return decimal value
     */
    public static double toDecimal(Money money) {
        if (money == null || money.getUnits() == null || money.getNanos() == null) {
            return 0.0;
        }
        return money.getUnits() + (money.getNanos() / (double) NANOS_MOD);
    }
    
    /**
     * Formats money to a readable string.
     * 
     * @param money the Money object
     * @return formatted string (e.g., "12.34 USD")
     */
    public static String format(Money money) {
        if (money == null) {
            return "0.00";
        }
        
        double amount = toDecimal(money);
        String currency = money.getCurrencyCode() != null ? " " + money.getCurrencyCode() : "";
        return String.format("%.2f%s", amount, currency);
    }
}