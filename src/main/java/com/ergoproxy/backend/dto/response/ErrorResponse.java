package com.ergoproxy.backend.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Builder
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message
) {

    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .build();
    }
}
