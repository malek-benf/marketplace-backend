package com.nahla.marketplace.dto.request;
import jakarta.validation.constraints.NotBlank;

public record FavoriteRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "listingId is required") String listingId
) {
}