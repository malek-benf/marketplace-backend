package com.nahla.marketplace.controller;

import com.nahla.marketplace.model.Category;
import com.nahla.marketplace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryRepository repo;

    @GetMapping
    public Map<String, Object> getAll() {
        return Map.of("data", repo.findByActiveTrueOrderBySortOrderAsc());
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @PostMapping
    public Category create(@RequestBody Category category) {
        category.setCreatedAt(new Date());
        category.setUpdatedAt(new Date());

        if (category.getSortOrder() == null) {
            category.setSortOrder(99);
        }
        if (category.getActive() == null) {
            category.setActive(true);
        }

        return repo.save(category);
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable String id, @RequestBody Category updated) {
        Category cat = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (updated.getName() != null) cat.setName(updated.getName());
        if (updated.getNameAr() != null) cat.setNameAr(updated.getNameAr());
        if (updated.getDescription() != null) cat.setDescription(updated.getDescription());
        if (updated.getIcon() != null) cat.setIcon(updated.getIcon());
        if (updated.getImage() != null) cat.setImage(updated.getImage());
        if (updated.getParentCategoryId() != null) cat.setParentCategoryId(updated.getParentCategoryId());
        if (updated.getSortOrder() != null) cat.setSortOrder(updated.getSortOrder());
        if (updated.getActive() != null) cat.setActive(updated.getActive());

        cat.setUpdatedAt(new Date());

        return repo.save(cat);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable String id) {
        repo.deleteById(id);
        return Map.of("message", "Category deleted");
    }

   
    @GetMapping("/seed")
    public String seedCategories() {

        if (repo.count() > 0) return "Already seeded";

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

        repo.saveAll(seeds);
        return "Seed completed";
    }
}