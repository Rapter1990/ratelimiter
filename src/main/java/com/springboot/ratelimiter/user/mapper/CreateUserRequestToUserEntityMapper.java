package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.common.model.mapper.BaseMapper;
import com.springboot.ratelimiter.user.model.UserEntity;
import com.springboot.ratelimiter.user.payload.request.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface named {@link CreateUserRequestToUserEntityMapper} for converting {@link CreateUserRequest} to {@link UserEntity}.
 * Extends BaseMapper for common mapping functionalities.
 */
@Mapper
public interface CreateUserRequestToUserEntityMapper extends BaseMapper<CreateUserRequest, UserEntity> {

    /**
     * Maps a CreateUserRequest object to a UserEntity object.
     *
     * @param createUserRequest the CreateUserRequest object to map
     * @return the corresponding UserEntity object
     */
    @Override
    UserEntity map(CreateUserRequest createUserRequest);

    /**
     * Initializes the mapper instance using MapStruct's Mappers.getMapper() method.
     *
     * @return the initialized CreateUserRequestToUserEntityMapper instance
     */
    static CreateUserRequestToUserEntityMapper initialize() {
        return Mappers.getMapper(CreateUserRequestToUserEntityMapper.class);
    }

}
