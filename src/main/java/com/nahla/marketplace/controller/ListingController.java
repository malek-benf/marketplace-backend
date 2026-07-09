package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.ListingCreateRequest;
import com.nahla.marketplace.dto.request.ListingSearchRequest;
import com.nahla.marketplace.dto.request.ListingUpdateRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.ListingDetailResponse;
import com.nahla.marketplace.dto.response.ListingResponse;
import com.nahla.marketplace.dto.response.PagedResponse;
import com.nahla.marketplace.service.ListingService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/listings")
@CrossOrigin
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public PagedResponse<ListingResponse> getAll(
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "governorate", required = false) String governorate,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "condition", required = false) String condition,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "verified", defaultValue = "false") boolean verifiedOnly,
            @RequestParam(value = "sort", defaultValue = "newest") String sortBy,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        ListingSearchRequest search = new ListingSearchRequest(
                keyword, governorate, category, minPrice, maxPrice,
                condition, source, verifiedOnly, sortBy, limit, offset
        );
        return listingService.search(search);
    }

    @GetMapping("/{id}")
    public ListingDetailResponse getById(@PathVariable String id) {
        return listingService.getByIdAndRegisterView(id);
    }

    @PostMapping
    public ApiResponse<ListingResponse> create(@Valid @RequestBody ListingCreateRequest request) {
        return ApiResponse.of(listingService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<ListingResponse> update(@PathVariable String id, @Valid @RequestBody ListingUpdateRequest request) {
        return ApiResponse.of(listingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<ListingResponse> delete(@PathVariable String id) {
        return ApiResponse.of(listingService.softDelete(id));
    }
}