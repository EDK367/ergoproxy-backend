package com.ergoproxy.backend.dto;

import java.time.LocalDateTime;

public record LogsInfoDTO(
        String ip,
        LocalDateTime createdAt
) {
}
