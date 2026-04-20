package com.ergoproxy.backend.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,
        String username,
        boolean enabled,
        String metadata,
        String role,
        List<LogsInfoDTO> logsInfo
) {
}
