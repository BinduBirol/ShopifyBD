package com.bnroll.annotations.ratelimit;

import io.github.bucket4j.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public boolean allowRequest(String key, int limit, int durationSeconds) {

        Bucket bucket = cache.computeIfAbsent(key, k -> createBucket(limit, durationSeconds));

        return bucket.tryConsume(1);
    }

    private Bucket createBucket(int limit, int durationSeconds) {

        Refill refill = Refill.intervally(limit, Duration.ofSeconds(durationSeconds));
        Bandwidth bandwidth = Bandwidth.classic(limit, refill);

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}