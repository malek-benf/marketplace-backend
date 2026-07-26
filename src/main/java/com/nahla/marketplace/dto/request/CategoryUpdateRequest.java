package com.nahla.marketplace.dto.request;

public record CategoryUpdateRequest(
        String name,
        String nameAr,
        String description,
        String icon,
        String image,
        String parentCategoryId,
        Integer sortOrder,
        Boolean active
) {
}
