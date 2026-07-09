package com.nahla.marketplace.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ListingCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 4, max = 120, message = "Title must be between 4 and 120 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        Double price,

        String currency,

        @NotBlank(message = "Seller id is required")
        String sellerId,
        String sellerName,

        @NotBlank(message = "Phone is required")
        String phone,

        String categoryId,
        @NotBlank(message = "Category is required")
        String category,

        List<String> images,

        @NotBlank(message = "Governorate is required")
        String governorate,
        String city,
        String location,
        Double latitude,
        Double longitude,

        String condition,
        List<String> tags,

        Boolean negotiable,
        Boolean deliveryAvailable,
        Integer stock,

        String source,
        String sourceId,
        String sourceUrl
) {
}