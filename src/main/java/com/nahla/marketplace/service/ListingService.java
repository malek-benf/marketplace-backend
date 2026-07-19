package com.nahla.marketplace.service;

import com.nahla.marketplace.dto.request.ListingCreateRequest;
import com.nahla.marketplace.dto.request.ListingSearchRequest;
import com.nahla.marketplace.dto.request.ListingUpdateRequest;
import com.nahla.marketplace.dto.response.ListingDetailResponse;
import com.nahla.marketplace.dto.response.ListingResponse;
import com.nahla.marketplace.dto.response.PagedResponse;
import com.nahla.marketplace.dto.response.SellerSummaryResponse;
import com.nahla.marketplace.exception.ResourceNotFoundException;
import com.nahla.marketplace.model.Listing;
import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.ListingRepository;
import com.nahla.marketplace.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private static final String STATUS_PUBLISHED = "published";
    private static final String STATUS_DELETED = "deleted";
    private static final String DEFAULT_IMAGE = "https://images.unsplash.com/photo-1587049352851-8d4e89133924";

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public PagedResponse<ListingResponse> search(ListingSearchRequest search) {
        Query query = buildSearchQuery(search);

        long total = mongoTemplate.count(query, Listing.class);

        query.with(resolveSort(search.sortBy()));
        query.skip(search.offset()).limit(search.limit());

        List<Listing> results = mongoTemplate.find(query, Listing.class);

        if (search.verifiedOnly()) {
            results = filterByVerifiedSeller(results);
        }

        return new PagedResponse<>(ListingResponse.fromAll(results), total, search.limit(), search.offset());
    }

    public ListingDetailResponse getByIdAndRegisterView(String id) {
        Listing listing = findEntityOrThrow(id);

        listing.setViews(Optional.ofNullable(listing.getViews()).orElse(0) + 1);
        listing = listingRepository.save(listing);

        SellerSummaryResponse seller = userRepository.findById(listing.getSellerId())
                .map(SellerSummaryResponse::from)
                .orElse(null);

        return new ListingDetailResponse(ListingResponse.from(listing), seller);
    }

    public ListingResponse create(ListingCreateRequest request, String sellerPhone) {
        User seller = userRepository.findByPhone(sellerPhone)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", sellerPhone));

        Date now = new Date();

        Listing listing = Listing.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .currency(StringUtils.hasText(request.currency()) ? request.currency() : "TND")
                .sellerId(seller.getId())
                .sellerName(seller.getName())
                .phone(request.phone())
                .categoryId(request.categoryId())
                .category(request.category())
                .images(hasImages(request) ? request.images() : List.of(DEFAULT_IMAGE))
                .governorate(request.governorate())
                .city(request.city())
                .location(request.location())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .condition(request.condition())
                .tags(request.tags())
                .negotiable(request.negotiable() != null ? request.negotiable() : true)
                .deliveryAvailable(request.deliveryAvailable() != null ? request.deliveryAvailable() : false)
                .stock(request.stock() != null ? request.stock() : 1)
                .source(StringUtils.hasText(request.source()) ? request.source() : "Nahla")
                .sourceId(request.sourceId())
                .sourceUrl(request.sourceUrl())
                .status(STATUS_PUBLISHED)
                .views(0)
                .favoritesCount(0)
                .createdAt(now)
                .updatedAt(now)
                .publishedAt(now)
                .build();

        Listing saved = listingRepository.save(listing);

        incrementSellerListingsCount(saved.getSellerId(), now);

        return ListingResponse.from(saved);
    }

    public ListingResponse update(String id, ListingUpdateRequest request, String requesterPhone) {
        Listing listing = findEntityOrThrow(id);
        assertOwnerOrAdmin(listing, requesterPhone);

        if (request.title() != null) listing.setTitle(request.title());
        if (request.description() != null) listing.setDescription(request.description());
        if (request.price() != null) listing.setPrice(request.price());
        if (request.status() != null) listing.setStatus(request.status());
        if (request.phone() != null) listing.setPhone(request.phone());
        if (request.condition() != null) listing.setCondition(request.condition());
        if (request.location() != null) listing.setLocation(request.location());
        if (request.city() != null) listing.setCity(request.city());
        if (request.stock() != null) listing.setStock(request.stock());
        if (request.images() != null) listing.setImages(request.images());
        if (request.tags() != null) listing.setTags(request.tags());

        listing.setUpdatedAt(new Date());

        return ListingResponse.from(listingRepository.save(listing));
    }

    public ListingResponse softDelete(String id, String requesterPhone) {
        Listing listing = findEntityOrThrow(id);
        assertOwnerOrAdmin(listing, requesterPhone);

        listing.setStatus(STATUS_DELETED);
        listing.setUpdatedAt(new Date());
        return ListingResponse.from(listingRepository.save(listing));
    }

    private Query buildSearchQuery(ListingSearchRequest search) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(STATUS_PUBLISHED));

        if (StringUtils.hasText(search.keyword())) {
            String regex = ".*" + Pattern.quote(search.keyword().toLowerCase()) + ".*";
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("description").regex(regex, "i"),
                    Criteria.where("category").regex(regex, "i"),
                    Criteria.where("tags").regex(regex, "i")
            ));
        }

        if (StringUtils.hasText(search.governorate()) && !"all".equalsIgnoreCase(search.governorate())) {
            query.addCriteria(Criteria.where("governorate").is(search.governorate()));
        }

        if (StringUtils.hasText(search.category()) && !"all".equalsIgnoreCase(search.category())) {
            query.addCriteria(Criteria.where("category").is(search.category()));
        }

        if (search.minPrice() != null) {
            query.addCriteria(Criteria.where("price").gte(search.minPrice()));
        }

        if (search.maxPrice() != null) {
            query.addCriteria(Criteria.where("price").lte(search.maxPrice()));
        }

        if (StringUtils.hasText(search.condition())) {
            query.addCriteria(Criteria.where("condition").is(search.condition()));
        }

        if (StringUtils.hasText(search.source())) {
            query.addCriteria(Criteria.where("source").is(search.source()));
        }

        return query;
    }

    private Sort resolveSort(String sortBy) {
        String key = sortBy == null ? "newest" : sortBy.toLowerCase();
        return switch (key) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "most_viewed" -> Sort.by(Sort.Direction.DESC, "views");
            case "popular" -> Sort.by(Sort.Direction.DESC, "favoritesCount").and(Sort.by(Sort.Direction.DESC, "views"));
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private List<Listing> filterByVerifiedSeller(List<Listing> listings) {
        return listings.stream()
                .filter(listing -> userRepository.findById(listing.getSellerId())
                        .map(User::getVerified)
                        .map(Boolean.TRUE::equals)
                        .orElse(false))
                .collect(Collectors.toList());
    }

    private void incrementSellerListingsCount(String sellerId, Date now) {
        if (sellerId == null) return;
        userRepository.findById(sellerId).ifPresent(seller -> {
            seller.setListingsCount(Optional.ofNullable(seller.getListingsCount()).orElse(0) + 1);
            seller.setUpdatedAt(now);
            userRepository.save(seller);
        });
    }

    private boolean hasImages(ListingCreateRequest request) {
        return request.images() != null && !request.images().isEmpty();
    }

    private Listing findEntityOrThrow(String id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("Listing", id));
    }

    /**
     * Endpoint-level authentication only proves "someone is logged in" - it says
     * nothing about whether they own this specific listing. This closes that gap:
     * only the listing's own seller, or an admin, may update/delete it.
     */
    private void assertOwnerOrAdmin(Listing listing, String requesterPhone) {
        User requester = userRepository.findByPhone(requesterPhone)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", requesterPhone));

        boolean isOwner = requester.getId().equals(listing.getSellerId());
        boolean isAdmin = "admin".equalsIgnoreCase(requester.getRole());

        if (!isOwner && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("You can only modify your own listings.");
        }
    }
}