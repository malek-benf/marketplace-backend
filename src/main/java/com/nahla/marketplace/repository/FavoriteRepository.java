package com.nahla.marketplace.repository;
import com.nahla.marketplace.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;
public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    List<Favorite> findByUserId(String userId);
    Optional<Favorite> findByUserIdAndListingId(String userId, String listingId);
    long countByUserId(String userId);
    long countByListingId(String listingId);
}
