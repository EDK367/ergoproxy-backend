package com.ergoproxy.backend.service;

import com.ergoproxy.backend.exception.CustomException;
import com.ergoproxy.backend.security.CustomUserDetails;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new CustomException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        if (!user.isEnabled()) {
            throw new CustomException(
                    HttpStatus.FORBIDDEN,
                    "User account is disabled"
            );
        }

        return new CustomUserDetails(user);
    }

    public CustomUserDetails loadUserSave(User user) {
        return new CustomUserDetails(user);
    }
}
