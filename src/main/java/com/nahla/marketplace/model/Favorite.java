package com.nahla.marketplace.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "favorites")
public class Favorite {

    @Id
    private String id;

    private String userId;

    private String listingId;

    @Builder.Default 
    private Date createdAt = new Date();
}