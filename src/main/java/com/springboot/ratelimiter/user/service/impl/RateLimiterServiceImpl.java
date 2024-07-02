package com.springboot.ratelimiter.user.service.impl;

import com.springboot.ratelimiter.user.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service class named {@link RateLimiterServiceImpl} implementing for rate limiting operations using Redis.
 * This service checks if the current request is allowed based on configured rate limiting rules.
 */
@Service
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${rate.limiter.max.requests}")
    private int MAX_REQUESTS;

    @Value("${rate.limiter.time.window.seconds}")
    private int TIME_WINDOW_IN_SECONDS;

    /**
     * Checks if the current request is allowed based on rate limiting rules.
     *
     * @return true if the request is allowed, false otherwise
     */
    @Override
    public boolean isAllowed() {

        String key = "rate_limiter:" + "user_creation";
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        Integer currentCount = (Integer) valueOperations.get(key);
        if (currentCount == null) {
            valueOperations.set(key, 1, TIME_WINDOW_IN_SECONDS, TimeUnit.SECONDS);
            return true;
        } else if (currentCount < MAX_REQUESTS) {
            valueOperations.increment(key);
            return true;
        } else {
            return false;
        }

    }

}
