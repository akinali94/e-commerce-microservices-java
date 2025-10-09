package com.example.checkout_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Money {
    

    @NotBlank(message = "Currency code is required")
    @JsonProperty("currency_code")
    private String currencyCode;
    

    @JsonProperty("units")
    private Long units;
    
    @Min(value = -999999999, message = "Nanos must be between -999,999,999 and +999,999,999")
    @Max(value = 999999999, message = "Nanos must be between -999,999,999 and +999,999,999")
    @JsonProperty("nanos")
    private Integer nanos;
    
    public boolean isValid() {
        if (nanos == null || units == null) {
            return false;
        }
        // Check nanos range
        if (nanos < -999999999 || nanos > 999999999) {
            return false;
        }
        // Check sign matching: nanos and units must have the same sign
        // (or one of them must be zero)
        if (nanos != 0 && units != 0) {
            return (nanos < 0) == (units < 0);
        }
        return true;
    }
    
    public boolean isZero() {
        return (units == null || units == 0) && (nanos == null || nanos == 0);
    }
    
    public boolean isPositive() {
        return isValid() && (units > 0 || (units == 0 && nanos > 0));
    }
    
    public boolean isNegative() {
        return isValid() && (units < 0 || (units == 0 && nanos < 0));
    }
}