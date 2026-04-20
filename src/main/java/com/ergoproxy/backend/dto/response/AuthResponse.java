package com.ergoproxy.backend.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String username,
        String email,
        String name,
        String lastname,
        String role
) {
}
