package com.springboot.ratelimiter.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO named {@link CreateUserRequest} for creating a new user, including name and email fields with validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotNull(message = "Name is mandatory")
    private String name;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email is mandatory")
    private String email;

}
