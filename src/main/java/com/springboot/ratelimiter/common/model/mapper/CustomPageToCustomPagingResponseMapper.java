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

/**
 * Mapper named {@link CustomPageToCustomPagingResponseMapper} for converting CustomPage to CustomPagingResponse.
 */
@Mapper
public interface CustomPageToCustomPagingResponseMapper {

    UserToUserResponseMapper userToUserResponseMapper = Mappers.getMapper(UserToUserResponseMapper.class);

    /**
     * Converts a CustomPage of User to a CustomPagingResponse of UserResponse.
     *
     * @param userPage the CustomPage object
     * @return the CustomPagingResponse object
     */
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

    /**
     * Converts a list of User to a list of UserResponse.
     *
     * @param users the list of User objects
     * @return the list of UserResponse objects
     */
    default List<UserResponse> toUserResponseList(List<User> users) {

        if (users == null) {
            return null;
        }

        return users.stream()
                .map(userToUserResponseMapper::map)
                .collect(Collectors.toList());

    }

    /**
     * Initializes the CustomPageToCustomPagingResponseMapper.
     *
     * @return the CustomPageToCustomPagingResponseMapper instance
     */
    static CustomPageToCustomPagingResponseMapper initialize() {
        return Mappers.getMapper(CustomPageToCustomPagingResponseMapper.class);
    }

}
