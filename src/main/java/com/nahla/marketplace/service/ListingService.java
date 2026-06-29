package com.nahla.marketplace.service;

import com.nahla.marketplace.model.Listing;
import com.nahla.marketplace.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {

    @Autowired
    private ListingRepository repo;

    public List<Listing> getAll() {
        return repo.findAll();
    }

    public Listing create(Listing listing) {
        return repo.save(listing);
    }
}