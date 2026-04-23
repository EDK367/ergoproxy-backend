package com.ergoproxy.backend.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfileDTO(
        UUID id,
        String username,
        String name,
        String lastname,
        String email,
        String phoneNumber,
        String role
) {}
