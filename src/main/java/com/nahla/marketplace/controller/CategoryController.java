package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.CategoryCreateRequest;
import com.nahla.marketplace.dto.request.CategoryUpdateRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.CategoryResponse;
import com.nahla.marketplace.dto.response.MessageResponse;
import com.nahla.marketplace.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.of(categoryService.getAllActive());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable String id) {
        return ApiResponse.of(categoryService.getById(id));
    }

    @PostMapping
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        return ApiResponse.of(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable String id, @RequestBody CategoryUpdateRequest request) {
        return ApiResponse.of(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(@PathVariable String id) {
        categoryService.delete(id);
        return new MessageResponse("Category deleted");
    }

    @GetMapping("/seed")
    public String seedCategories() {
        return categoryService.seedDefaultsIfEmpty();
    }
}