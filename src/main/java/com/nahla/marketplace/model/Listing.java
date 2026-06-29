package com.nahla.marketplace.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "listings")
public class Listing {

    @Id
    private String id;

    private String title;
    private String description;
    private double price;
    private String category;

    private List<String> images;

    private String sellerId;

    private String source;    
    private String sourceUrl;

    private String governorate;
    private String status; 

    private Date createdAt = new Date();
}