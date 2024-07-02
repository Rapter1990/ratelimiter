package com.springboot.ratelimiter.user.repository;

import com.springboot.ratelimiter.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface named {@link UserRepository} for managing UserEntity entities.
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Checks if a user exists by their email address.
     *
     * @param email the email address to check
     * @return true if a user with the given email address exists, false otherwise
     */
    boolean existsByEmail(String email);
}
