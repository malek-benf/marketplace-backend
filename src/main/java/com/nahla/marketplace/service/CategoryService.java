package com.nahla.marketplace.service;

import com.nahla.marketplace.dto.request.CategoryCreateRequest;
import com.nahla.marketplace.dto.request.CategoryUpdateRequest;
import com.nahla.marketplace.dto.response.CategoryResponse;
import com.nahla.marketplace.exception.ResourceNotFoundException;
import com.nahla.marketplace.model.Category;
import com.nahla.marketplace.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class CategoryService {

    private static final int DEFAULT_SORT_ORDER = 99;

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllActive() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse getById(String id) {
        return CategoryResponse.from(findEntityOrThrow(id));
    }

    public CategoryResponse create(CategoryCreateRequest request) {
        Date now = new Date();

        Category category = Category.builder()
                .name(request.name())
                .nameAr(request.nameAr())
                .slug(request.slug())
                .description(request.description())
                .icon(request.icon())
                .image(request.image())
                .parentCategoryId(request.parentCategoryId())
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : DEFAULT_SORT_ORDER)
                .active(request.active() != null ? request.active() : true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public CategoryResponse update(String id, CategoryUpdateRequest request) {
        Category category = findEntityOrThrow(id);

        if (request.name() != null) category.setName(request.name());
        if (request.nameAr() != null) category.setNameAr(request.nameAr());
        if (request.description() != null) category.setDescription(request.description());
        if (request.icon() != null) category.setIcon(request.icon());
        if (request.image() != null) category.setImage(request.image());
        if (request.parentCategoryId() != null) category.setParentCategoryId(request.parentCategoryId());
        if (request.sortOrder() != null) category.setSortOrder(request.sortOrder());
        if (request.active() != null) category.setActive(request.active());

        category.setUpdatedAt(new Date());

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw ResourceNotFoundException.forEntity("Category", id);
        }
        categoryRepository.deleteById(id);
    }

    /**
     * Seeds a small set of default categories on first startup only.
     * Moved here from a CommandLineRunner that used to call the controller directly.
     */
    public String seedDefaultsIfEmpty() {
        if (categoryRepository.count() > 0) {
            return "Already seeded";
        }

        Date now = new Date();

        List<Category> seeds = Arrays.asList(
                Category.builder().slug("bee").name("Bees").nameAr("نحل")
                        .description("Bee related products").icon("bee")
                        .sortOrder(1).active(true).createdAt(now).updatedAt(now).build(),

                Category.builder().slug("honey").name("Honey").nameAr("عسل")
                        .description("Honey products").icon("drop")
                        .sortOrder(2).active(true).createdAt(now).updatedAt(now).build(),

                Category.builder().slug("equipment").name("Equipment").nameAr("معدات")
                        .description("Beekeeping tools").icon("tool")
                        .sortOrder(3).active(true).createdAt(now).updatedAt(now).build()
        );

        categoryRepository.saveAll(seeds);
        return "Seed completed";
    }

    private Category findEntityOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Category", id));
    }
}