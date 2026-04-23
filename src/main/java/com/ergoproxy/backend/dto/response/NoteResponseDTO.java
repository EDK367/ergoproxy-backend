package com.ergoproxy.backend.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NoteResponseDTO(
        UUID id,
        String title,
        String content,
        boolean important,
        UUID userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isOwner,
        boolean isShared
) {}
