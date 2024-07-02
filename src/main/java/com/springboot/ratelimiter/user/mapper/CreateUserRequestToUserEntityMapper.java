package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.common.model.mapper.BaseMapper;
import com.springboot.ratelimiter.user.model.UserEntity;
import com.springboot.ratelimiter.user.payload.request.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateUserRequestToUserEntityMapper extends BaseMapper<CreateUserRequest, UserEntity> {

    @Override
    UserEntity map(CreateUserRequest createUserRequest);

    static CreateUserRequestToUserEntityMapper initialize() {
        return Mappers.getMapper(CreateUserRequestToUserEntityMapper.class);
    }

}
