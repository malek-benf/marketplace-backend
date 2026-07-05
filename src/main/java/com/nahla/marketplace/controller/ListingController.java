package com.nahla.marketplace.controller;

import com.nahla.marketplace.model.Listing;
import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.ListingRepository;
import com.nahla.marketplace.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin
public class ListingController {

    @Autowired
    private ListingRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping
    public Map<String, Object> getAll(
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

        Query query = new Query();

        query.addCriteria(Criteria.where("status").is("published"));

        if (keyword != null && !keyword.trim().isEmpty()) {
            String regex = ".*" + keyword.toLowerCase() + ".*";

            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("description").regex(regex, "i"),
                    Criteria.where("category").regex(regex, "i"),
                    Criteria.where("tags").regex(regex, "i")
            ));
        }

        if (governorate != null && !governorate.equalsIgnoreCase("all")) {
            query.addCriteria(Criteria.where("governorate").is(governorate));
        }

        if (category != null && !category.equalsIgnoreCase("all")) {
            query.addCriteria(Criteria.where("category").is(category));
        }

        if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        }

        if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }

        if (condition != null && !condition.isEmpty()) {
            query.addCriteria(Criteria.where("condition").is(condition));
        }

        if (source != null && !source.isEmpty()) {
            query.addCriteria(Criteria.where("source").is(source));
        }

        switch (sortBy.toLowerCase()) {
            case "price_asc":
                query.with(Sort.by(Sort.Direction.ASC, "price"));
                break;
            case "price_desc":
                query.with(Sort.by(Sort.Direction.DESC, "price"));
                break;
            case "most_viewed":
                query.with(Sort.by(Sort.Direction.DESC, "views"));
                break;
            case "popular":
                query.with(Sort.by(Sort.Direction.DESC, "favoritesCount")
                        .and(Sort.by(Sort.Direction.DESC, "views")));
                break;
            default:
                query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        query.skip(offset).limit(limit);

        List<Listing> results = mongoTemplate.find(query, Listing.class);

        long total = mongoTemplate.count(query, Listing.class);

        if (verifiedOnly) {
            results = results.stream()
                    .filter(l -> {
                        User seller = userRepo.findById(l.getSellerId()).orElse(null);
                        return seller != null && Boolean.TRUE.equals(Boolean.TRUE.equals(seller.getVerified()));
                    })
                    .collect(Collectors.toList());
        }

        return Map.of(
                "data", results,
                "total", total,
                "limit", limit,
                "offset", offset
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable String id) {

        Listing listing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        listing.setViews(Optional.ofNullable(listing.getViews()).orElse(0) + 1);
        repo.save(listing);

        Map<String, Object> response = new HashMap<>();
        response.put("data", listing);

        User seller = userRepo.findById(listing.getSellerId()).orElse(null);

        if (seller != null) {
            response.put("seller", Map.of(
                    "id", seller.getId(),
                    "name", seller.getName(),
                    "phone", seller.getPhone(),
                    "verified", Boolean.TRUE.equals(seller.getVerified()),
                    "rating", seller.getRating(),
                    "trustScore", seller.getTrustScore()
            ));
        }

        return response;
    }

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody Listing listing) {

        Date now = new Date();

        listing.setCreatedAt(now);
        listing.setUpdatedAt(now);
        listing.setPublishedAt(now);
        listing.setStatus("published");

        if (listing.getViews() == null) listing.setViews(0);
        if (listing.getFavoritesCount() == null) listing.setFavoritesCount(0);

        if (listing.getImages() == null || listing.getImages().isEmpty()) {
            listing.setImages(List.of(
                    "https://images.unsplash.com/photo-1587049352851-8d4e89133924"
            ));
        }

        Listing saved = repo.save(listing);

        if (saved.getSellerId() != null) {
            userRepo.findById(saved.getSellerId()).ifPresent(user -> {
                user.setListingsCount(Optional.ofNullable(user.getListingsCount()).orElse(0) + 1);
                user.setUpdatedAt(now);
                userRepo.save(user);
            });
        }

        return Map.of("data", saved);
    }

    @PatchMapping("/{id}")
    public Map<String, Object> update(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates
    ) {

        Listing l = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (updates.containsKey("title"))
            l.setTitle((String) updates.get("title"));

        if (updates.containsKey("description"))
            l.setDescription((String) updates.get("description"));

        if (updates.containsKey("price"))
            l.setPrice(((Number) updates.get("price")).doubleValue());

        if (updates.containsKey("status"))
            l.setStatus((String) updates.get("status"));

        if (updates.containsKey("phone"))
            l.setPhone((String) updates.get("phone"));

        if (updates.containsKey("whatsapp"))
            l.setWhatsapp((String) updates.get("whatsapp"));

        if (updates.containsKey("condition"))
            l.setCondition((String) updates.get("condition"));

        if (updates.containsKey("location"))
            l.setLocation((String) updates.get("location"));

        if (updates.containsKey("city"))
            l.setCity((String) updates.get("city"));

        if (updates.containsKey("stock"))
            l.setStock(((Number) updates.get("stock")).intValue());

        if (updates.containsKey("images"))
            l.setImages(toStringList(updates.get("images")));

        if (updates.containsKey("tags"))
            l.setTags(toStringList(updates.get("tags")));

        l.setUpdatedAt(new Date());

        return Map.of("data", repo.save(l));
    }

    private List<String> toStringList(Object value) {
        if (value instanceof List<?> values) {
            List<String> result = new ArrayList<>();
            for (Object item : values) {
                if (item instanceof String text) {
                    result.add(text);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable String id) {

        Listing l = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        l.setStatus("deleted");
        l.setUpdatedAt(new Date());

        return Map.of("data", repo.save(l));
    }
}