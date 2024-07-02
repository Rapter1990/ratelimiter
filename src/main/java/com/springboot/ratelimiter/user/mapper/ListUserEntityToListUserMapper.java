package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper interface named {@link ListUserEntityToListUserMapper} for converting a List of UserEntity objects to a List of User objects.
 */
@Mapper
public interface ListUserEntityToListUserMapper {

    UserEntityToUserMapper userEntityToUserMapper  = Mappers.getMapper(UserEntityToUserMapper.class);

    /**
     * Converts a List of UserEntity objects to a List of User objects.
     *
     * @param userEntities the List of UserEntity objects to map
     * @return the corresponding List of User objects
     */
    default List<User> toUserList(List<UserEntity> userEntities) {

        if (userEntities == null) {
            return null;
        }

        return userEntities.stream()
                .map(userEntityToUserMapper::map)
                .collect(Collectors.toList());

    }

    /**
     * Initializes the mapper instance using MapStruct's Mappers.getMapper() method.
     *
     * @return the initialized ListUserEntityToListUserMapper instance
     */
    static ListUserEntityToListUserMapper initialize() {
        return Mappers.getMapper(ListUserEntityToListUserMapper.class);
    }

}
