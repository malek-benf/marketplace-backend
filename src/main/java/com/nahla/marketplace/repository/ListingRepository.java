package com.nahla.marketplace.repository;

import com.nahla.marketplace.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ListingRepository extends MongoRepository<Listing, String> {
    List<Listing> findBySellerId(String sellerId);
    List<Listing> findBySellerIdAndStatus(String sellerId, String status);
    List<Listing> findByCategory(String category);
    List<Listing> findByCategoryAndStatus(String category, String status);
    List<Listing> findByGovernorate(String governorate);
    List<Listing> findBySource(String source);
    long countByStatus(String status);
    List<Listing> findByStatusOrderByCreatedAtDesc(String status);
}
