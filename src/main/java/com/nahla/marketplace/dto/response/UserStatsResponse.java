package com.nahla.marketplace.dto.response;
public record UserStatsResponse(
        Integer listings,
        Integer followers,
        Double rating,
        Double trustScore
) {
}
