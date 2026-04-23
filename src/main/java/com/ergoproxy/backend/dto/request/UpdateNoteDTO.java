package com.ergoproxy.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNoteDTO(

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(max = 10000, message = "Content must not exceed 10000 characters")
        String content,

        @NotNull(message = "Important is required")
        Boolean important
) {}
