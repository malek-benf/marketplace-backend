package com.nahla.marketplace.repository;

import com.nahla.marketplace.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ListingRepository extends MongoRepository<Listing, String> {
}
