package com.springboot.ratelimiter.user.service.impl;

import com.springboot.ratelimiter.base.AbstractBaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RateLimiterServiceImpl}
 */
class RateLimiterServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private RateLimiterServiceImpl rateLimiterService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private static final String RATE_LIMITER_KEY = "rate_limiter:user_creation";

    /**
     * Set up method executed before each test method in {@link RateLimiterServiceImplTest}.
     * Initializes mock objects and sets up necessary fields for testing.
     */
    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        ReflectionTestUtils.setField(rateLimiterService, "MAX_REQUESTS", 5);
        ReflectionTestUtils.setField(rateLimiterService, "TIME_WINDOW_IN_SECONDS", 60);
    }

    /**
     * Test case for {@link RateLimiterServiceImpl#isAllowed()} when there is no existing rate limit.
     * Verifies that the method correctly allows the first request within the specified time window.
     */
    @Test
    public void testIsAllowed_FirstRequest() {

        // Given
        when(valueOperations.get(RATE_LIMITER_KEY)).thenReturn(null);

        // When
        boolean isAllowed = rateLimiterService.isAllowed();

        // Then
        assertTrue(isAllowed);
        verify(valueOperations).set(eq(RATE_LIMITER_KEY),
                eq(1),
                eq(60L),
                eq(TimeUnit.SECONDS));

    }

    /**
     * Test case for {@link RateLimiterServiceImpl#isAllowed()} when the request is within the rate limit.
     * Verifies that the method correctly allows the request when it is within the maximum allowed requests per time window.
     */
    @Test
    public void testIsAllowed_WithinLimit() {
        // Given
        when(valueOperations.get(RATE_LIMITER_KEY)).thenReturn(3);

        // When
        boolean isAllowed = rateLimiterService.isAllowed();

        // Then
        assertTrue(isAllowed);
        verify(valueOperations).increment(RATE_LIMITER_KEY);
    }

    /**
     * Test case for {@link RateLimiterServiceImpl#isAllowed()} when the rate limit is exceeded.
     * Verifies that the method correctly denies the request when the maximum allowed requests per time window is reached.
     */
    @Test
    public void testIsAllowed_LimitExceeded() {

        // Given
        when(valueOperations.get(RATE_LIMITER_KEY)).thenReturn(5);

        // When
        boolean isAllowed = rateLimiterService.isAllowed();

        // Then
        assertFalse(isAllowed);
        verify(valueOperations, never()).increment(any());
        verify(valueOperations, never()).set(any(), any(), anyLong(), any(TimeUnit.class));

    }

}