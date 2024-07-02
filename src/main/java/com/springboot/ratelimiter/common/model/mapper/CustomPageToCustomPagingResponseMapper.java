package com.springboot.ratelimiter.common.model.mapper;

import com.springboot.ratelimiter.common.model.dto.response.CustomPagingResponse;
import com.springboot.ratelimiter.common.model.page.CustomPage;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.mapper.UserToUserResponseMapper;
import com.springboot.ratelimiter.user.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;


@Mapper
public interface CustomPageToCustomPagingResponseMapper {

    UserToUserResponseMapper userToUserResponseMapper = Mappers.getMapper(UserToUserResponseMapper.class);


    default CustomPagingResponse<UserResponse> toPagingResponse(CustomPage<User> userPage) {

        if (userPage == null) {
            return null;
        }

        return CustomPagingResponse.<UserResponse>builder()
                .content(toUserResponseList(userPage.getContent()))
                .totalElementCount(userPage.getTotalElementCount())
                .totalPageCount(userPage.getTotalPageCount())
                .pageNumber(userPage.getPageNumber())
                .pageSize(userPage.getPageSize())
                .build();

    }

    default List<UserResponse> toUserResponseList(List<User> users) {

        if (users == null) {
            return null;
        }

        return users.stream()
                .map(userToUserResponseMapper::map)
                .collect(Collectors.toList());

    }

    static CustomPageToCustomPagingResponseMapper initialize() {
        return Mappers.getMapper(CustomPageToCustomPagingResponseMapper.class);
    }

}
