package com.example.checkout_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a shipping/billing address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @NotBlank(message = "Street address is required")
    @JsonProperty("street_address")
    private String streetAddress;
    
    @NotBlank(message = "City is required")
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("state")
    private String state;
    
    @NotBlank(message = "Country is required")
    @JsonProperty("country")
    private String country;
    
    @NotNull(message = "Zip code is required")
    @JsonProperty("zip_code")
    private Integer zipCode;
}