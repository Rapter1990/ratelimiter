package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.common.model.mapper.BaseMapper;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserToUserResponseMapper extends BaseMapper<User, UserResponse> {

    @Override
    UserResponse map(User source);

    static UserToUserResponseMapper initialize() {
        return Mappers.getMapper(UserToUserResponseMapper.class);
    }

}
