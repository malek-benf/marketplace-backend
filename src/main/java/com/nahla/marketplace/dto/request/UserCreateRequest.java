package com.nahla.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @Pattern(regexp = "beekeeper|farmer|seller|buyer|admin", message = "Invalid role")
        String role,

        @NotBlank(message = "Name is required")
        String name,
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Phone is required")
        String phone,

        String governorate,
        String address,
        String bio,
        String avatarUrl,
        String coverImage,
        String preferredLanguage
) {
}