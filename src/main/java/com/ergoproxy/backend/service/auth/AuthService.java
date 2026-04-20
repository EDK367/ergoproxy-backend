package com.ergoproxy.backend.service.auth;

import com.ergoproxy.backend.dto.request.LoginRequest;
import com.ergoproxy.backend.dto.request.RegisterRequest;
import com.ergoproxy.backend.dto.response.AuthResponse;
import com.ergoproxy.backend.model.Role;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.RoleRepository;
import com.ergoproxy.backend.repository.UserRepository;
import com.ergoproxy.backend.security.CustomUserDetails;
import com.ergoproxy.backend.security.JwtService;
import com.ergoproxy.backend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(request.username());

        String token = jwtService.generateToken(userDetails);

        User user = userDetails.getUser();

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .lastname(user.getLastname())
                .role(user.getRole().getName())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists: " + request.username());
        }

        User user = User.builder()
                .name(request.name())
                .lastname(request.lastname())
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .role(roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("Role not found: 2")))
                .build();

        userRepository.save(user);

        CustomUserDetails userDetails = customUserDetailsService.loadUserSave(user);

        String token = jwtService.generateToken(userDetails);

         return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .lastname(user.getLastname())
                .role(user.getRole().getName())
                .build();
    }
}
