package com.nahla.marketplace.dto.response;

import com.nahla.marketplace.model.Category;

import java.util.Date;

public record CategoryResponse(
        String id,
        String name,
        String nameAr,
        String slug,
        String description,
        String icon,
        String image,
        String parentCategoryId,
        Integer sortOrder,
        Boolean active,
        Date createdAt,
        Date updatedAt
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getNameAr(),
                category.getSlug(),
                category.getDescription(),
                category.getIcon(),
                category.getImage(),
                category.getParentCategoryId(),
                category.getSortOrder(),
                category.getActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}