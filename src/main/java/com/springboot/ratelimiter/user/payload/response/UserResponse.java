package com.springboot.ratelimiter.user.payload.response;

import lombok.*;

/**
 * DTO named {@link UserResponse} for user response, including id, name, and email fields.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String name;
    private String email;

}
