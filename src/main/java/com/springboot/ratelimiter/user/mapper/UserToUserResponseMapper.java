package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.common.model.mapper.BaseMapper;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface named {@link UserToUserResponseMapper} for converting User to UserResponse.
 * Extends BaseMapper for common mapping functionalities.
 */
@Mapper
public interface UserToUserResponseMapper extends BaseMapper<User, UserResponse> {

    /**
     * Maps a User object to a UserResponse object.
     *
     * @param source the User object to map
     * @return the corresponding UserResponse object
     */
    @Override
    UserResponse map(User source);

    /**
     * Initializes the mapper instance using MapStruct's Mappers.getMapper() method.
     *
     * @return the initialized UserToUserResponseMapper instance
     */
    static UserToUserResponseMapper initialize() {
        return Mappers.getMapper(UserToUserResponseMapper.class);
    }

}
