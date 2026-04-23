package com.ergoproxy.backend.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShareNoteDTO(

        @NotNull(message = "User ID is required")
        UUID userId
) {}
