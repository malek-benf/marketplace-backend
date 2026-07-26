package com.nahla.marketplace.service;

import com.nahla.marketplace.dto.response.CountedListResponse;
import com.nahla.marketplace.dto.response.FavoriteActionResponse;
import com.nahla.marketplace.dto.response.ListingResponse;
import com.nahla.marketplace.exception.ResourceNotFoundException;
import com.nahla.marketplace.model.Favorite;
import com.nahla.marketplace.model.Listing;
import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.FavoriteRepository;
import com.nahla.marketplace.repository.ListingRepository;
import com.nahla.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, ListingRepository listingRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    public CountedListResponse<ListingResponse> getFavoriteListingsForUser(String userPhone) {
        String userId = resolveUserId(userPhone);

        List<String> listingIds = favoriteRepository.findByUserId(userId).stream()
                .map(Favorite::getListingId)
                .toList();

        List<Listing> listings = listingIds.isEmpty()
                ? new ArrayList<>()
                : listingRepository.findAllById(listingIds);

        return CountedListResponse.of(ListingResponse.fromAll(listings));
    }

    public FavoriteActionResponse addFavorite(String userPhone, String listingId) {
        String userId = resolveUserId(userPhone);
        boolean alreadyFavorited = favoriteRepository.findByUserIdAndListingId(userId, listingId).isPresent();

        if (alreadyFavorited) {
            return new FavoriteActionResponse(true, false);
        }

        Favorite favorite = new Favorite();
        favorite.setId(UUID.randomUUID().toString());
        favorite.setUserId(userId);
        favorite.setListingId(listingId);
        favorite.setCreatedAt(new Date());
        favoriteRepository.save(favorite);

        adjustFavoritesCount(listingId, 1);

        return new FavoriteActionResponse(true, true);
    }

    public FavoriteActionResponse removeFavorite(String userPhone, String listingId) {
        String userId = resolveUserId(userPhone);
        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndListingId(userId, listingId);

        if (favorite.isEmpty()) {
            return new FavoriteActionResponse(false, false);
        }

        favoriteRepository.delete(favorite.get());
        adjustFavoritesCount(listingId, -1);

        return new FavoriteActionResponse(false, true);
    }

    private void adjustFavoritesCount(String listingId, int delta) {
        listingRepository.findById(listingId).ifPresent(listing -> {
            int current = Optional.ofNullable(listing.getFavoritesCount()).orElse(0);
            listing.setFavoritesCount(Math.max(0, current + delta));
            listingRepository.save(listing);
        });
    }

    /**
     * Favorites are keyed by the user's Mongo _id, but the JWT only carries
     * their phone (our "username"). Resolve one to the other here, so
     * controllers never need to know the difference.
     */
    private String resolveUserId(String userPhone) {
        User user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", userPhone));
        return user.getId();
    }
}