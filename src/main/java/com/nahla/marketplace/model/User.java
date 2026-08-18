package com.nahla.marketplace.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Pattern(regexp = "beekeeper|farmer|seller|buyer|admin", message = "Invalid role")
    @Builder.Default
    private String role = "buyer";

    @NotBlank(message = "Name is required")
    private String name;
    private String email;
    private String password;

    @Indexed(unique = true)
    @NotBlank(message = "Phone is required")
    private String phone;

    private String governorate;
    private String address;
    private String bio;
    private String avatarUrl;
    private String coverImage;

    @Builder.Default
    private Double trustScore = 75.0;
    @Builder.Default
    private Double rating = 0.0;
    @Builder.Default
    private Integer totalReviews = 0;
    @Builder.Default
    private Double responseRate = 0.0;
    @Builder.Default
    private Integer listingsCount = 0;
    @Builder.Default
    private Integer followersCount = 0;
    @Builder.Default
    private Integer followingCount = 0;
    @Builder.Default
    private Boolean verified = false;
    @Builder.Default
    private String verificationStatus = "pending";
    @Builder.Default
    private Boolean enabled = true;
    @Builder.Default
    private String preferredLanguage = "fr";

    // CHANGED TO SUPPORT BATCH/MULTIPLE DEVICES
    @Builder.Default
    private List<String> fcmTokens = new ArrayList<>();

    @Builder.Default
    private Date lastActiveAt = new Date();
    @Builder.Default
    private Date createdAt = new Date();
    @Builder.Default
    private Date updatedAt = new Date();
}