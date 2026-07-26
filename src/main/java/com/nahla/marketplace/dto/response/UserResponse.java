package com.nahla.marketplace.dto.response;

import com.nahla.marketplace.model.User;

import java.util.Date;

/**
 * Password is intentionally never exposed through the API.
 */
public record UserResponse(
        String id,
        String role,
        String name,
        String email,
        String phone,
        String governorate,
        String address,
        String bio,
        String avatarUrl,
        String coverImage,
        Double trustScore,
        Double rating,
        Integer totalReviews,
        Double responseRate,
        Integer listingsCount,
        Integer followersCount,
        Integer followingCount,
        Boolean verified,
        String verificationStatus,
        Boolean enabled,
        String preferredLanguage,
        Date lastActiveAt,
        Date createdAt,
        Date updatedAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getRole(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getGovernorate(),
                user.getAddress(),
                user.getBio(),
                user.getAvatarUrl(),
                user.getCoverImage(),
                user.getTrustScore(),
                user.getRating(),
                user.getTotalReviews(),
                user.getResponseRate(),
                user.getListingsCount(),
                user.getFollowersCount(),
                user.getFollowingCount(),
                user.getVerified(),
                user.getVerificationStatus(),
                user.getEnabled(),
                user.getPreferredLanguage(),
                user.getLastActiveAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}