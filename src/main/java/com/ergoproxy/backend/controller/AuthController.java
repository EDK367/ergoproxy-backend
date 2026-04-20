package com.ergoproxy.backend.controller;

import com.ergoproxy.backend.dto.request.LoginRequest;
import com.ergoproxy.backend.dto.request.RegisterRequest;
import com.ergoproxy.backend.dto.response.AuthResponse;
import com.ergoproxy.backend.dto.response.Response;
import com.ergoproxy.backend.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(Response.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(authService.login(request))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<Response<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(Response.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Registration successful")
                .data(authService.register(request))
                .build());
    }
}
