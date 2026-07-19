package com.nahla.marketplace.security;

import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bridges our MongoDB {@link User} entity to Spring Security's {@link UserDetails}.
 * The "username" Spring Security talks about is the user's phone number.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with phone: " + phone));

        String role = "ROLE_" + user.getRole().toUpperCase();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhone())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .disabled(!Boolean.TRUE.equals(user.getEnabled()))
                .build();
    }
}