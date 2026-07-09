package com.nahla.marketplace.dto.response;
import com.nahla.marketplace.model.User;

public record SellerSummaryResponse(
        String id,
        String name,
        String phone,
        boolean verified,
        Double rating,
        Double trustScore
) {

    public static SellerSummaryResponse from(User seller) {
        return new SellerSummaryResponse(
                seller.getId(),
                seller.getName(),
                seller.getPhone(),
                Boolean.TRUE.equals(seller.getVerified()),
                seller.getRating(),
                seller.getTrustScore()
        );
    }
}