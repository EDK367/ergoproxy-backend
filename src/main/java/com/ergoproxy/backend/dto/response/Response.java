package com.ergoproxy.backend.dto.response;

import lombok.Builder;

@Builder
public record Response<T> (
    int status,
    String message,
    T data
) { }
