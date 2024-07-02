package com.springboot.ratelimiter.user.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
