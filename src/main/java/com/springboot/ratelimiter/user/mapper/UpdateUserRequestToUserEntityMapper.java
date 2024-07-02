package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.user.model.UserEntity;
import com.springboot.ratelimiter.user.payload.request.UpdateUserRequest;
import lombok.experimental.UtilityClass;

/**
 * Utility class named {@link UpdateUserRequestToUserEntityMapper} for mapping UpdateUserRequest to UserEntity for update operations.
 */
@UtilityClass
public class UpdateUserRequestToUserEntityMapper {

    /**
     * Maps fields from UpdateUserRequest to an existing UserEntity for update.
     *
     * @param userEntity          the existing UserEntity to update
     * @param updateUserRequest   the UpdateUserRequest containing new data
     * @return the updated UserEntity with fields from UpdateUserRequest
     */
    public UserEntity mapForUpdate(UserEntity userEntity, final UpdateUserRequest updateUserRequest) {
        userEntity.setName(updateUserRequest.getName());
        userEntity.setEmail(updateUserRequest.getEmail());
        return userEntity;
    }

}
