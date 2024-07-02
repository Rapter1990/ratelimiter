package com.springboot.ratelimiter.user.payload.request;

import com.springboot.ratelimiter.common.model.dto.request.CustomPagingRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO named {@link UserPagingRequest} for paginating user data, extending from CustomPagingRequest.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserPagingRequest extends CustomPagingRequest {

}
