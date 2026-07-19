package com.nahla.marketplace.service;

import com.nahla.marketplace.dto.request.LoginRequest;
import com.nahla.marketplace.dto.request.UserCreateRequest;
import com.nahla.marketplace.dto.response.AuthResponse;
import com.nahla.marketplace.dto.response.UserResponse;
import com.nahla.marketplace.exception.ResourceNotFoundException;
import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.UserRepository;
import com.nahla.marketplace.security.CustomUserDetailsService;
import com.nahla.marketplace.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthResponse register(UserCreateRequest request) {
        // UserService.create() already checks for duplicate phone and hashes the password.
        UserResponse createdUser = userService.create(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.phone());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, createdUser);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.phone(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid phone or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.phone());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> ResourceNotFoundException.forEntity("User", request.phone()));

        return new AuthResponse(token, UserResponse.from(user));
    }
}