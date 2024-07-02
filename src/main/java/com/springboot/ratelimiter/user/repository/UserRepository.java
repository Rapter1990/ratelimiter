package com.springboot.ratelimiter.user.repository;

import com.springboot.ratelimiter.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    boolean existsByEmail(String email);
}
