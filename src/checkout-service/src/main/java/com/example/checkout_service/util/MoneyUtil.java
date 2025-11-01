package com.example.checkout_service.util;

import com.example.checkout_service.model.Money;
import com.example.checkout_service.exception.InvalidMoneyException;
import com.example.checkout_service.exception.MismatchingCurrencyException;

public class MoneyUtil {
    private static final int NANOS_MIN = -999999999;
    private static final int NANOS_MAX = 999999999;
    private static final int NANOS_MOD = 1000000000;

    // Validates if money value has valid units/nanos signs and ranges
    public static boolean isValid(Money money) {
        return signMatches(money) && validNanos(money.getNanos());
    }

    private static boolean signMatches(Money money) {
        return money.getNanos() == 0 || money.getUnits() == 0 || 
               (money.getNanos() < 0) == (money.getUnits() < 0);
    }

    private static boolean validNanos(int nanos) {
        return NANOS_MIN <= nanos && nanos <= NANOS_MAX;
    }

    // Returns true if the specified money value is equal to zero
    public static boolean isZero(Money money) {
        return money.getUnits() == 0 && money.getNanos() == 0;
    }

    // Returns true if the specified money value is valid and is positive
    public static boolean isPositive(Money money) {
        return isValid(money) && (money.getUnits() > 0 || 
              (money.getUnits() == 0 && money.getNanos() > 0));
    }

    // Returns true if the specified money value is valid and is negative
    public static boolean isNegative(Money money) {
        return isValid(money) && (money.getUnits() < 0 || 
              (money.getUnits() == 0 && money.getNanos() < 0));
    }

    // Returns true if values a and b have a currency code and they are the same values
    public static boolean areSameCurrency(Money a, Money b) {
        return a.getCurrencyCode() != null && b.getCurrencyCode() != null &&
               a.getCurrencyCode().equals(b.getCurrencyCode()) && 
               !a.getCurrencyCode().isEmpty();
    }

    // Returns true if values a and b are equal, including the currency
    public static boolean areEquals(Money a, Money b) {
        if (a.getCurrencyCode() == null || b.getCurrencyCode() == null) {
            return a.getCurrencyCode() == b.getCurrencyCode() && 
                   a.getUnits() == b.getUnits() && 
                   a.getNanos() == b.getNanos();
        }
        return a.getCurrencyCode().equals(b.getCurrencyCode()) && 
               a.getUnits() == b.getUnits() && 
               a.getNanos() == b.getNanos();
    }

    // Returns the same amount with the sign negated
    public static Money negate(Money money) {
        return new Money(-money.getUnits(), -money.getNanos(), money.getCurrencyCode());
    }

    // Sum adds two money values
    public static Money sum(Money a, Money b) throws InvalidMoneyException, MismatchingCurrencyException {
        if (!isValid(a) || !isValid(b)) {
            throw new InvalidMoneyException("One of the specified money values is invalid");
        }
        
        if (!areSameCurrency(a, b)) {
            throw new MismatchingCurrencyException("Mismatching currency codes");
        }
        
        long units = a.getUnits() + b.getUnits();
        int nanos = a.getNanos() + b.getNanos();
        
        if ((units == 0 && nanos == 0) || (units > 0 && nanos >= 0) || (units < 0 && nanos <= 0)) {
            // Same sign for units and nanos
            units += nanos / NANOS_MOD;
            nanos = nanos % NANOS_MOD;
        } else {
            // Different sign for units and nanos
            if (units > 0) {
                units--;
                nanos += NANOS_MOD;
            } else {
                units++;
                nanos -= NANOS_MOD;
            }
        }
        
        return new Money(units, nanos, a.getCurrencyCode());
    }

    // Slow multiplication by repeated addition
    public static Money multiplySlow(Money money, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Multiplier must be positive");
        }
        
        Money result = new Money(money.getUnits(), money.getNanos(), money.getCurrencyCode());
        
        for (int i = 1; i < n; i++) {
            try {
                result = sum(result, money);
            } catch (Exception e) {
                throw new RuntimeException("Error during multiplication", e);
            }
        }
        
        return result;
    }
}