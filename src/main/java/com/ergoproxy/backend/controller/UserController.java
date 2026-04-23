package com.ergoproxy.backend.controller;

import com.ergoproxy.backend.dto.UserDTO;
import com.ergoproxy.backend.dto.response.Response;
import com.ergoproxy.backend.dto.response.UserProfileDTO;
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
                .message("Users retrieved successfully")
                .data(userService.getAllUsers())
                .build()
        );
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Response<UserProfileDTO>> getMyProfile() {
        return ResponseEntity.ok(Response.<UserProfileDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Profile retrieved successfully")
                .data(userService.getMyProfile())
                .build()
        );
    }
}
