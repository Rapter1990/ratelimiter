package com.springboot.ratelimiter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class named {@link RedisConfig} to set up Redis in the application.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a {@link RedisConnectionFactory} using Lettuce.
     * The {@link LettuceConnectionFactory} is a connection factory driven by the Lettuce Redis client library.
     *
     * @return a {@link RedisConnectionFactory} instance
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Creates a {@link RedisTemplate} for interacting with Redis.
     * The {@link RedisTemplate} provides high-level abstraction for Redis interactions, allowing for
     * efficient and convenient Redis data access. The template uses a {@link StringRedisSerializer} for
     * serializing keys and a {@link GenericJackson2JsonRedisSerializer} for serializing values.
     *
     * @param redisConnectionFactory the {@link RedisConnectionFactory} to use for the template
     * @return a configured {@link RedisTemplate} instance
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}

