package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.UserCreateRequest;
import com.nahla.marketplace.dto.request.UserUpdateRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.CountedListResponse;
import com.nahla.marketplace.dto.response.MessageResponse;
import com.nahla.marketplace.dto.response.UserResponse;
import com.nahla.marketplace.dto.response.UserStatsResponse;
import com.nahla.marketplace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public CountedListResponse<UserResponse> getAll() {
        return CountedListResponse.of(userService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable String id) {
        return ApiResponse.of(userService.getById(id));
    }

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.of(userService.create(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        return ApiResponse.of(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(@PathVariable String id) {
        userService.delete(id);
        return new MessageResponse("User deleted");
    }

    @GetMapping("/{id}/stats")
    public ApiResponse<UserStatsResponse> stats(@PathVariable String id) {
        return ApiResponse.of(userService.getStats(id));
    }
}