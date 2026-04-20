package com.ergoproxy.backend.service.auth;

import com.ergoproxy.backend.dto.request.LoginRequest;
import com.ergoproxy.backend.dto.request.RegisterRequest;
import com.ergoproxy.backend.dto.response.AuthResponse;
import com.ergoproxy.backend.model.User;
import com.ergoproxy.backend.repository.RoleRepository;
import com.ergoproxy.backend.repository.UserRepository;
import com.ergoproxy.backend.security.CustomUserDetails;
import com.ergoproxy.backend.service.JwtService;
import com.ergoproxy.backend.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import com.ergoproxy.backend.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
                .name(user.getName())
                .lastname(user.getLastname())
                .role(user.getRole().getName())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new CustomException(HttpStatus.CONFLICT, "Username already exists: " + request.username());
        }

        if (request.email() != null && userRepository.existsByEmail(request.email())) {
            throw new CustomException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (request.phoneNumber() != null && userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new CustomException(HttpStatus.CONFLICT, "Phone number already exists");
        }

        User user = User.builder()
                .name(request.name())
                .lastname(request.lastname())
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .role(roleRepository.findById(2L).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Role not found: 2")))
                .build();

        userRepository.save(user);

        CustomUserDetails userDetails = customUserDetailsService.loadUserSave(user);

        String token = jwtService.generateToken(userDetails);

         return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .name(user.getName())
                .lastname(user.getLastname())
                .role(user.getRole().getName())
                .build();
    }
}
