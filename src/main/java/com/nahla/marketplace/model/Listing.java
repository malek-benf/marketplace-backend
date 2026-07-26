package com.nahla.marketplace.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "listings")
public class Listing {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    @Size(min = 4, max = 120, message = "Title must be between 4 and 120 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;
    @Builder.Default
    private String currency = "TND";

    @Indexed
    @NotBlank(message = "Seller id is required")
    private String sellerId;
    private String sellerName;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String categoryId;

    @Indexed
    @NotBlank(message = "Category is required")
    private String category;

    private List<String> images;

    @Indexed
    @NotBlank(message = "Governorate is required")
    private String governorate;
    private String city;
    private String location;
    private Double latitude;
    private Double longitude;

    private String condition;

    private List<String> tags;

    @Builder.Default
    private Boolean negotiable = true;
    @Builder.Default
    private Boolean deliveryAvailable = false;
    @Builder.Default
    private Boolean featured = false;
    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Integer stock = 1;

    @Indexed
    @Builder.Default
    private String status = "published";
    @Builder.Default
    private String verificationStatus = "pending";

    @Builder.Default
    private String source = "Nahla";
    private String sourceId;
    private String sourceUrl;

    @Builder.Default
    private Integer views = 0;
    @Builder.Default
    private Integer favoritesCount = 0;
    @Builder.Default
    private Integer sharesCount = 0;
    @Builder.Default
    private Integer contactClicks = 0;

    private Double aiScore;

    private Date boostUntil;
    private Date publishedAt;
    private Date expiresAt;

    @Indexed
    @Builder.Default
    private Date createdAt = new Date();
    @Builder.Default
    private Date updatedAt = new Date();

    @Builder.Default
    private Integer reportsCount = 0;

}