package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.user.model.UserEntity;
import com.springboot.ratelimiter.user.payload.request.UpdateUserRequest;

public class UpdateUserRequestToUserEntityMapper {

    public static UserEntity mapForUpdate(UserEntity userEntity, final UpdateUserRequest updateUserRequest) {
        userEntity.setName(updateUserRequest.getName());
        userEntity.setEmail(updateUserRequest.getEmail());
        return userEntity;
    }

}
