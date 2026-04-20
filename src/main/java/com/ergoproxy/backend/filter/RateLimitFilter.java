package com.ergoproxy.backend.filter;

import com.ergoproxy.backend.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ip = obtenerIpReal(request);

        if (rateLimitService.isBlocked(ip)) {
            long seconds = rateLimitService.secondDuration(ip);
            rechazar(response, seconds);
            return;
        }

        if (rateLimitService.tryConsume(ip)) {
            filterChain.doFilter(request, response);
        } else {
            long seconds = rateLimitService.secondDuration(ip);
            rechazar(response, seconds);
        }
    }

    private void rechazar(HttpServletResponse response, long segundosRestantes) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(segundosRestantes));

        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "status", 429,
                "error", "Too Many Requests",
                "message", "Limit for requests exceeded. Please try again after " + segundosRestantes + " seconds."
        );

        objectMapper.writeValue(response.getWriter(), body);
    }

    private String obtenerIpReal(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}