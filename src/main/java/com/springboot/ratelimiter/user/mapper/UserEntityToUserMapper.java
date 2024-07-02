package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.common.model.mapper.BaseMapper;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface named {@link UserEntityToUserMapper} for converting UserEntity to User.
 * Extends BaseMapper for common mapping functionalities.
 */
@Mapper
public interface UserEntityToUserMapper extends BaseMapper<UserEntity, User> {

    /**
     * Maps a UserEntity object to a User object.
     *
     * @param source the UserEntity object to map
     * @return the corresponding User object
     */
    @Override
    User map(UserEntity source);

    /**
     * Initializes the mapper instance using MapStruct's Mappers.getMapper() method.
     *
     * @return the initialized UserEntityToUserMapper instance
     */
    static UserEntityToUserMapper initialize() {
        return Mappers.getMapper(UserEntityToUserMapper.class);
    }

}
