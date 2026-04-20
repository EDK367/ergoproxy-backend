package com.ergoproxy.backend.service;

import com.ergoproxy.backend.security.CustomUserDetails;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return new CustomUserDetails(user);
    }

    public CustomUserDetails loadUserSave(User user) {
        return new CustomUserDetails(user);
    }
}
