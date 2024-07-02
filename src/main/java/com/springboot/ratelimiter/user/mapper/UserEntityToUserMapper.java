package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.common.model.mapper.BaseMapper;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserEntityToUserMapper extends BaseMapper<UserEntity, User> {

    @Override
    User map(UserEntity source);


    static UserEntityToUserMapper initialize() {
        return Mappers.getMapper(UserEntityToUserMapper.class);
    }

}
