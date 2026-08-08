package com.nahla.marketplace.service;

import com.nahla.marketplace.dto.request.ChangePasswordRequest;
import com.nahla.marketplace.dto.request.UserCreateRequest;
import com.nahla.marketplace.dto.request.UserUpdateRequest;
import com.nahla.marketplace.dto.response.UserResponse;
import com.nahla.marketplace.dto.response.UserStatsResponse;
import com.nahla.marketplace.exception.DuplicateResourceException;
import com.nahla.marketplace.exception.ResourceNotFoundException;
import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final double DEFAULT_TRUST_SCORE = 75.0;
    private static final String ADMIN_ROLE = "admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getById(String id) {
        return UserResponse.from(findEntityOrThrow(id));
    }

    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException("A user with phone " + request.phone() + " already exists.");
        }

        Date now = new Date();

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .role(request.role() != null ? request.role() : "buyer")
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .governorate(request.governorate())
                .address(request.address())
                .bio(request.bio())
                .avatarUrl(request.avatarUrl())
                .coverImage(request.coverImage())
                .preferredLanguage(request.preferredLanguage() != null ? request.preferredLanguage() : "fr")
                .trustScore(DEFAULT_TRUST_SCORE)
                .rating(0.0)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse update(String id, UserUpdateRequest request, String requesterPhone) {
        assertSelfOrAdmin(id, requesterPhone);

        User user = findEntityOrThrow(id);

        if (request.name() != null) user.setName(request.name());
        if (request.email() != null) user.setEmail(request.email());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.governorate() != null) user.setGovernorate(request.governorate());
        if (request.address() != null) user.setAddress(request.address());
        if (request.bio() != null) user.setBio(request.bio());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());
        if (request.coverImage() != null) user.setCoverImage(request.coverImage());

        user.setUpdatedAt(new Date());

        return UserResponse.from(userRepository.save(user));
    }

    public void delete(String id, String requesterPhone) {
        assertSelfOrAdmin(id, requesterPhone);

        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException.forEntity("User", id);
        }
        userRepository.deleteById(id);
    }

    public void changePassword(String id, ChangePasswordRequest request, String requesterPhone) {
        assertSelfOrAdmin(id, requesterPhone);

        User user = findEntityOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(new Date());
        userRepository.save(user);
    }

    public UserStatsResponse getStats(String id) {
        User user = findEntityOrThrow(id);
        return new UserStatsResponse(
                user.getListingsCount(),
                user.getFollowersCount(),
                user.getRating(),
                user.getTrustScore()
        );
    }

    private User findEntityOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", id));
    }

    /**
     * Endpoint-level @PreAuthorize only checks "is this user logged in / do they
     * have this role" - it can't know whether the {id} in the URL belongs to the
     * caller. This check closes that gap: a user may only modify their own
     * profile, unless they're an admin.
     */
    private void assertSelfOrAdmin(String targetUserId, String requesterPhone) {
        User requester = userRepository.findByPhone(requesterPhone)
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", requesterPhone));

        boolean isSelf = requester.getId().equals(targetUserId);
        boolean isAdmin = ADMIN_ROLE.equalsIgnoreCase(requester.getRole());

        if (!isSelf && !isAdmin) {
            throw new AccessDeniedException("You can only modify your own profile.");
        }
    }
}