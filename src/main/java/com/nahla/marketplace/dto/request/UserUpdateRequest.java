package com.nahla.marketplace.dto.request;

/**
 * All fields are optional: only non-null values are applied by the service.
 */
public record UserUpdateRequest(
        String name,
        String email,
        String phone,
        String governorate,
        String address,
        String bio,
        String avatarUrl,
        String coverImage
) {
}