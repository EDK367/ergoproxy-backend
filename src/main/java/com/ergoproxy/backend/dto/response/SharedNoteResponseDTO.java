package com.ergoproxy.backend.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record SharedNoteResponseDTO(
        Long id,
        UUID noteId,
        String noteTitle,
        UUID noteOwnerId,
        String noteOwnerUsername,
        UUID userSharedId,
        String userSharedUsername,
        LocalDateTime createdAt,
        boolean enabled
) {}
