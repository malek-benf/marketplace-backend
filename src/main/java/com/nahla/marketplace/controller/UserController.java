package com.nahla.marketplace.controller;

import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository repo;

    @GetMapping
    public Map<String, Object> getAll() {
        List<User> users = repo.findAll();
        return Map.of("count", users.size(), "data", users);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        user.setId(UUID.randomUUID().toString());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        if (user.getTrustScore() == null) user.setTrustScore(75.0);
        if (user.getRating() == null) user.setRating(0.0);
        if (user.getEnabled() == null) user.setEnabled(true);

        return repo.save(user);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable String id,
                       @RequestBody Map<String, Object> body) {

        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (body.containsKey("name"))
            user.setName((String) body.get("name"));

        if (body.containsKey("email"))
            user.setEmail((String) body.get("email"));

        if (body.containsKey("phone"))
            user.setPhone((String) body.get("phone"));

        if (body.containsKey("whatsapp"))
            user.setWhatsapp((String) body.get("whatsapp"));

        if (body.containsKey("governorate"))
            user.setGovernorate((String) body.get("governorate"));

        if (body.containsKey("address"))
            user.setAddress((String) body.get("address"));

        if (body.containsKey("bio"))
            user.setBio((String) body.get("bio"));

        if (body.containsKey("avatarUrl"))
            user.setAvatarUrl((String) body.get("avatarUrl"));

        if (body.containsKey("coverImage"))
            user.setCoverImage((String) body.get("coverImage"));

        user.setUpdatedAt(new Date());

        return repo.save(user);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable String id) {
        repo.deleteById(id);
        return Map.of("message", "User deleted");
    }

    @GetMapping("/{id}/stats")
    public Map<String, Object> stats(@PathVariable String id) {

        User u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Map.of(
                "listings", u.getListingsCount(),
                "followers", u.getFollowersCount(),
                "rating", u.getRating(),
                "trustScore", u.getTrustScore()
        );
    }
}