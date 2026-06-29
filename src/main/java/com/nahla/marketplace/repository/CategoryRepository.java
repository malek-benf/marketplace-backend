package com.nahla.marketplace.repository;

import com.nahla.marketplace.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {
}