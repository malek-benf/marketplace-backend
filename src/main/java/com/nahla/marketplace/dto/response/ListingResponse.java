package com.nahla.marketplace.dto.response;

import com.nahla.marketplace.model.Listing;

import java.util.Date;
import java.util.List;

public record ListingResponse(
        String id,
        String title,
        String description,
        Double price,
        String currency,
        String sellerId,
        String sellerName,
        String phone,
        String categoryId,
        String category,
        List<String> images,
        String governorate,
        String city,
        String location,
        Double latitude,
        Double longitude,
        String condition,
        List<String> tags,
        Boolean negotiable,
        Boolean deliveryAvailable,
        Boolean featured,
        Boolean active,
        Integer stock,
        String status,
        String verificationStatus,
        String source,
        String sourceId,
        String sourceUrl,
        Integer views,
        Integer favoritesCount,
        Integer sharesCount,
        Integer contactClicks,
        Double aiScore,
        Date boostUntil,
        Date publishedAt,
        Date expiresAt,
        Date createdAt,
        Date updatedAt,
        Integer reportsCount
) {

    public static ListingResponse from(Listing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getPrice(),
                listing.getCurrency(),
                listing.getSellerId(),
                listing.getSellerName(),
                listing.getPhone(),
                listing.getCategoryId(),
                listing.getCategory(),
                listing.getImages(),
                listing.getGovernorate(),
                listing.getCity(),
                listing.getLocation(),
                listing.getLatitude(),
                listing.getLongitude(),
                listing.getCondition(),
                listing.getTags(),
                listing.getNegotiable(),
                listing.getDeliveryAvailable(),
                listing.getFeatured(),
                listing.getActive(),
                listing.getStock(),
                listing.getStatus(),
                listing.getVerificationStatus(),
                listing.getSource(),
                listing.getSourceId(),
                listing.getSourceUrl(),
                listing.getViews(),
                listing.getFavoritesCount(),
                listing.getSharesCount(),
                listing.getContactClicks(),
                listing.getAiScore(),
                listing.getBoostUntil(),
                listing.getPublishedAt(),
                listing.getExpiresAt(),
                listing.getCreatedAt(),
                listing.getUpdatedAt(),
                listing.getReportsCount()
        );
    }

    public static List<ListingResponse> fromAll(List<Listing> listings) {
        return listings.stream().map(ListingResponse::from).toList();
    }
}