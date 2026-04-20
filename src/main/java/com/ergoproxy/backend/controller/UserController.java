package com.ergoproxy.backend.controller;

import com.ergoproxy.backend.dto.UserDTO;
import com.ergoproxy.backend.dto.request.LoginRequest;
import com.ergoproxy.backend.dto.response.AuthResponse;
import com.ergoproxy.backend.dto.response.Response;
import com.ergoproxy.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(Response.<List<UserDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Users sucesfull")
                .data(userService.getAllUsers())
                .build()
        );
    }
}
