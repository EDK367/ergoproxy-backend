package com.ergoproxy.backend.service;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> counterIpMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedIpMap = new ConcurrentHashMap<>();

    private static final int TOKENS_FOR_WINDOWS = 10;
    private static final Duration LEVEL_0 = Duration.ofMinutes(1);
    private static final Duration LEVEL_1 = Duration.ofMinutes(2);
    private static final Duration LEVEL_2 = Duration.ofMinutes(5);
    private static final Duration LEVEL_3 = Duration.ofMinutes(15);
    private static final Duration LEVEL_4 = Duration.ofMinutes(30);
    private static final Duration LEVEL_5 = Duration.ofHours(1);
    private static final Duration LEVEL_6 = Duration.ofHours(6);
    private static final Duration LEVEL_7 = Duration.ofDays(1);

    public Bucket resolveBucket(String ip) {
        return bucketMap.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(TOKENS_FOR_WINDOWS).refillIntervally(TOKENS_FOR_WINDOWS, LEVEL_0))
                .build();
    }

    public boolean isBlocked(String ip) {
        Instant until = blockedIpMap.get(ip);
        if (until == null) return false;

        if (Instant.now().isBefore(until)) {
            return true;
        }
        blockedIpMap.remove(ip);
        bucketMap.remove(ip);
        return false;
    }

    public boolean tryConsume(String ip) {
        if (isBlocked(ip)) {
            return false;
        }

        Bucket bucket = bucketMap.computeIfAbsent(ip, this::newBucket);
        boolean permitted = bucket.tryConsume(1);

        if (!permitted) {
            int count = counterIpMap.merge(ip, 1, Integer::sum);
            Duration blockDuration = calculateBlockDuration(count);
            blockedIpMap.put(ip, Instant.now().plus(blockDuration));
            bucketMap.remove(ip);
        } else {
            Instant lastBlock = blockedIpMap.get(ip);
            if (lastBlock != null && Instant.now().minus(Duration.ofHours(24)).isAfter(lastBlock)) {
                counterIpMap.remove(ip);
            }
        }

        return permitted;
    }

    public long secondDuration(String ip) {
        Instant until = blockedIpMap.get(ip);
        if (until == null) return 0;
        long seconds = Instant.now().until(until, ChronoUnit.SECONDS);
        return Math.max(0, seconds);
    }

    private Duration calculateBlockDuration(int count) {
        return switch (count) {
            case 1 -> LEVEL_1;
            case 2 -> LEVEL_2;
            case 3 -> LEVEL_3;
            case 4 -> LEVEL_4;
            case 5 -> LEVEL_5;
            case 6 -> LEVEL_6;
            default -> LEVEL_7;
        };
    }
}