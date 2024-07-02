package com.springboot.ratelimiter.user.service;

/**
 * Service interface named {@link RateLimiterService} for rate limiting operations.
 */
public interface RateLimiterService {

    /**
     * Checks if the current request is allowed based on rate limiting rules.
     *
     * @return true if the request is allowed, false otherwise
     */
    boolean isAllowed();

}
