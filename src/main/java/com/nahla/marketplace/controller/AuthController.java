package com.nahla.marketplace.controller;

import com.nahla.marketplace.dto.request.LoginRequest;
import com.nahla.marketplace.dto.request.UserCreateRequest;
import com.nahla.marketplace.dto.response.ApiResponse;
import com.nahla.marketplace.dto.response.AuthResponse;
import com.nahla.marketplace.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.of(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.of(authService.login(request));
    }
}