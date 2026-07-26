package com.nahla.marketplace.repository;

import com.nahla.marketplace.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findBySlug(String slug);
    List<Category> findByActiveTrueOrderBySortOrderAsc();
    boolean existsBySlug(String slug);
    List<Category> findAllByOrderBySortOrderAsc();
}
