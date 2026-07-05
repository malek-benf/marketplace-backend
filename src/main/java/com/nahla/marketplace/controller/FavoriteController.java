package com.nahla.marketplace.controller;

import com.nahla.marketplace.model.Favorite;
import com.nahla.marketplace.model.Listing;
import com.nahla.marketplace.repository.FavoriteRepository;
import com.nahla.marketplace.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepo;

    @Autowired
    private ListingRepository listingRepo;

    @GetMapping
    public Map<String, Object> getFavorites(@RequestParam String userId) {

        List<Favorite> favorites = favoriteRepo.findByUserId(userId);

        List<String> listingIds = favorites.stream()
                .map(fav -> fav.getListingId())
                .toList();

        List<Listing> listings = listingIds.isEmpty()
                ? new ArrayList<>()
                : listingRepo.findAllById(listingIds);

        return Map.of(
                "count", listings.size(),
                "data", listings
        );
    }

    @PostMapping
    public Map<String, Object> add(@RequestBody Map<String, String> body) {

        String userId = body.get("userId");
        String listingId = body.get("listingId");

        Optional<Favorite> exists =
                favoriteRepo.findByUserIdAndListingId(userId, listingId);

        if (exists.isPresent()) {
            return Map.of("message", "Already favorited");
        }

        Favorite fav = new Favorite();
        fav.setId(UUID.randomUUID().toString());
        fav.setUserId(userId);
        fav.setListingId(listingId);
        fav.setCreatedAt(new Date());

        favoriteRepo.save(fav);

        Listing listing = listingRepo.findById(listingId).orElse(null);
        if (listing != null) {
            listing.setFavoritesCount(
                    Optional.ofNullable(listing.getFavoritesCount()).orElse(0) + 1
            );
            listingRepo.save(listing);
        }

        return Map.of("message", "Added to favorites");
    }

    @DeleteMapping
    public Map<String, Object> remove(@RequestParam String userId,
                                      @RequestParam String listingId) {

        Optional<Favorite> fav =
                favoriteRepo.findByUserIdAndListingId(userId, listingId);

        fav.ifPresent(favoriteRepo::delete);

        Listing listing = listingRepo.findById(listingId).orElse(null);
        if (listing != null) {
            listing.setFavoritesCount(
                    Math.max(0,
                            Optional.ofNullable(listing.getFavoritesCount()).orElse(0) - 1
                    )
            );
            listingRepo.save(listing);
        }

        return Map.of("message", "Removed from favorites");
    }
}