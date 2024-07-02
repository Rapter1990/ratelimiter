package com.springboot.ratelimiter.common.model;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Abstract base class named {@link BaseDomainModel} for domain models, providing common fields for creation and update timestamps.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseDomainModel {

    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

}
