package com.nahla.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "Name is required") String name,
        String nameAr,
        @NotBlank(message = "Slug is required") String slug,
        String description,
        String icon,
        String image,
        String parentCategoryId,
        Integer sortOrder,
        Boolean active
) {
}
