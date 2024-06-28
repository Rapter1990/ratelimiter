package com.springboot.ratelimiter.base;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class AbstractTestContainerConfiguration {

    @Container
    static MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.33");

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);

    @BeforeAll
    static void beforeAll() {
        MYSQL_CONTAINER.withReuse(true);
        MYSQL_CONTAINER.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    private static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getFirstMappedPort().toString());
    }

}
