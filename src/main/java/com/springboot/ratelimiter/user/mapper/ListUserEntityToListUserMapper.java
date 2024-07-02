package com.springboot.ratelimiter.user.mapper;

import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ListUserEntityToListUserMapper {

    UserEntityToUserMapper userEntityToUserMapper  = Mappers.getMapper(UserEntityToUserMapper.class);

    default List<User> toUserList(List<UserEntity> userEntities) {

        if (userEntities == null) {
            return null;
        }

        return userEntities.stream()
                .map(userEntityToUserMapper::map)
                .collect(Collectors.toList());

    }


    static ListUserEntityToListUserMapper initialize() {
        return Mappers.getMapper(ListUserEntityToListUserMapper.class);
    }

}
