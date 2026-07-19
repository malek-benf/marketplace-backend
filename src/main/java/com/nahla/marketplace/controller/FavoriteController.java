package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.FavoriteRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.CountedListResponse;
import com.nahla.marketplace.dto.response.FavoriteActionResponse;
import com.nahla.marketplace.dto.response.ListingResponse;
import com.nahla.marketplace.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
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
    public CountedListResponse<ListingResponse> getFavorites(Authentication authentication) {
        return favoriteService.getFavoriteListingsForUser(authentication.getName());
    }

    @PostMapping
    public ApiResponse<FavoriteActionResponse> add(@Valid @RequestBody FavoriteRequest request, Authentication authentication) {
        return ApiResponse.of(favoriteService.addFavorite(authentication.getName(), request.listingId()));
    }

    @DeleteMapping
    public ApiResponse<FavoriteActionResponse> remove(@RequestParam String listingId, Authentication authentication) {
        return ApiResponse.of(favoriteService.removeFavorite(authentication.getName(), listingId));
    }
}