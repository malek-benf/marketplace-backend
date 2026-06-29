package com.nahla.marketplace.controller;

import com.nahla.marketplace.model.Listing;
import com.nahla.marketplace.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listings")
@CrossOrigin
public class ListingController {

    @Autowired
    private ListingService service;

    @GetMapping
    public List<Listing> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Listing create(@RequestBody Listing listing) {
        return service.create(listing);
    }
}