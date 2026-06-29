package com.nahla.marketplace.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoryController {

    @GetMapping
    public List<String> getCategories() {
        return List.of(
                "Bee colonies",
                "Reines",
                "Ruches",
                "Matériel",
                "Miel",
                "Pollen",
                "Cire",
                "Propolis",
                "Nourrissement",
                "Services",
                "Terrain agricole",
                "Divers"
        );
    }
}