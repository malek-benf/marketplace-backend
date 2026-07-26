package com.nahla.marketplace.dto.request;

public record ListingSearchRequest(
        String keyword,
        String governorate,
        String category,
        Double minPrice,
        Double maxPrice,
        String condition,
        String source,
        boolean verifiedOnly,
        String sortBy,
        int limit,
        int offset
) {
}