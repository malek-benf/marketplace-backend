package com.nahla.marketplace.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.util.List;

/**
 * All fields are optional: only the properties present in the JSON body (non-null)
 * are applied to the existing listing by the service layer.
 */
public record ListingUpdateRequest(
        String title,
        String description,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        Double price,

        String status,
        String phone,
        String condition,
        String location,
        String city,
        Integer stock,
        List<String> images,
        List<String> tags
) {
}