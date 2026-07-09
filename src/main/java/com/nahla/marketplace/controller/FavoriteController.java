package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.FavoriteRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.CountedListResponse;
import com.nahla.marketplace.dto.response.FavoriteActionResponse;
import com.nahla.marketplace.dto.response.ListingResponse;
import com.nahla.marketplace.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public CountedListResponse<ListingResponse> getFavorites(@RequestParam String userId) {
        return favoriteService.getFavoriteListingsForUser(userId);
    }

    @PostMapping
    public ApiResponse<FavoriteActionResponse> add(@Valid @RequestBody FavoriteRequest request) {
        return ApiResponse.of(favoriteService.addFavorite(request.userId(), request.listingId()));
    }

    @DeleteMapping
    public ApiResponse<FavoriteActionResponse> remove(@RequestParam String userId, @RequestParam String listingId) {
        return ApiResponse.of(favoriteService.removeFavorite(userId, listingId));
    }
}