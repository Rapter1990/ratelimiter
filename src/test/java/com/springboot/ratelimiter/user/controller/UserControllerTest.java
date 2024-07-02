package com.springboot.ratelimiter.user.controller;

import com.springboot.ratelimiter.base.AbstractRestControllerTest;
import com.springboot.ratelimiter.common.model.dto.response.CustomPagingResponse;
import com.springboot.ratelimiter.common.model.dto.response.CustomResponse;
import com.springboot.ratelimiter.common.model.mapper.CustomPageToCustomPagingResponseMapper;
import com.springboot.ratelimiter.common.model.page.CustomPage;
import com.springboot.ratelimiter.common.model.page.CustomPaging;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.model.UserEntity;
import com.springboot.ratelimiter.user.payload.request.CreateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UpdateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UserPagingRequest;
import com.springboot.ratelimiter.user.payload.response.UserResponse;
import com.springboot.ratelimiter.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link UserController} class, testing various user management operations.
 */
class UserControllerTest extends AbstractRestControllerTest {

    @MockBean
    private UserService userService;

    private final CustomPageToCustomPagingResponseMapper customPageToCustomPagingResponseMapper =
            CustomPageToCustomPagingResponseMapper.initialize();

    /**
     * Test case for saving a new user via HTTP POST request.
     *
     * @throws Exception if there is an error performing the HTTP request or assertions fail.
     */
    @Test
    void givenCreateUserRequest_whenSaveUser_thenReturnSavedUser() throws Exception {

        // Given
        final CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User 1")
                .build();

        final User user = User.builder()
                .email(createUserRequest.getEmail())
                .name(createUserRequest.getName())
                .build();

        // When
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);

        // Then
        mockMvc.perform(post(BASE_PATH + "/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value("CREATED"))
                .andExpect(jsonPath("$.isSuccess").value(true));

        // Verify
        verify(userService).createUser(any(CreateUserRequest.class));

    }

    /**
     * Test case for retrieving a user by ID via HTTP GET request.
     *
     * @throws Exception if there is an error performing the HTTP request or assertions fail.
     */
    @Test
    void givenUserId_whenGetUserById_thenReturnUser() throws Exception {

        // Given
        final String userId = UUID.randomUUID().toString();

        final User user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();

        // When
        when(userService.getUserById(userId)).thenReturn(user);

        // Then
        mockMvc.perform(get(BASE_PATH + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.name").value("Test User"))
                .andExpect(jsonPath("$.response.email").value("test@example.com"));

        // Verify
        verify(userService).getUserById(userId);

    }

    /**
     * Test case for updating an existing user via HTTP PUT request.
     *
     * @throws Exception if there is an error performing the HTTP request or assertions fail.
     */
    @Test
    void givenUpdateUserRequest_whenUpdateUser_thenReturnUpdatedUser() throws Exception {

        // Given
        final String userId = UUID.randomUUID().toString();

        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email("updated@userinfo.com")
                .name("Updated Test User")
                .build();

        final User updatedUser = User.builder()
                .id(userId)
                .name(updateUserRequest.getName())
                .email(updateUserRequest.getEmail())
                .build();

        // When
        when(userService.updateUser(any(String.class), any(UpdateUserRequest.class))).thenReturn(updatedUser);

        // Then
        mockMvc.perform(put(BASE_PATH + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.name").value("Updated Test User"))
                .andExpect(jsonPath("$.response.email").value("updated@userinfo.com"));

        // Verify
        verify(userService).updateUser(any(String.class), any(UpdateUserRequest.class));

    }

    /**
     * Test case for deleting a user by ID via HTTP DELETE request.
     *
     * @throws Exception if there is an error performing the HTTP request or assertions fail.
     */
    @Test
    void givenUserId_whenDeleteUserById_thenStatusOk() throws Exception {

        // Given
        final String userId = UUID.randomUUID().toString();

        // When
        mockMvc.perform(delete(BASE_PATH + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").value("User is deleted by ID: " + userId));

        // Verify
        verify(userService).deleteUserById(userId);

    }

    /**
     * Test case for retrieving a paginated list of users via HTTP GET request.
     *
     * @throws Exception if there is an error performing the HTTP request or assertions fail.
     */
    @Test
    void givenProductPagingRequest_whenGetProductsFromUser_thenReturnCustomPageProduct() throws Exception {

        // Given
        final UserPagingRequest pagingRequest = UserPagingRequest.builder()
                .pagination(
                        CustomPaging.builder()
                                .pageSize(1)
                                .pageNumber(1)
                                .build()
                ).build();

        final String userId = UUID.randomUUID().toString();

        final UserEntity expected = UserEntity.builder()
                .id(userId)
                .name("Test User")
                .email("test@userinfo.com")
                .build();

        final List<UserEntity> userEntities = new ArrayList<>(Collections.singletonList(expected));

        final Page<UserEntity> userEntityPage = new PageImpl<>(userEntities,
                PageRequest.of(1, 1),
                userEntities.size()
        );

        final List<User> userDomainModels = userEntities.stream()
                .map(entity -> new User(entity.getId(),entity.getName(),entity.getEmail()))
                .collect(Collectors.toList());

        final CustomPage<User> userPage = CustomPage.of(userDomainModels, userEntityPage);

        final CustomPagingResponse<UserResponse> userPagingResponse =
                customPageToCustomPagingResponseMapper.toPagingResponse(userPage);

        final CustomResponse<CustomPagingResponse<UserResponse>> expectedResult =
                CustomResponse.ok(userPagingResponse);

        // When
        when(userService.getUsers(any(UserPagingRequest.class))).thenReturn(userPage);

        // Then
        mockMvc.perform(get(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagingRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.content[0].id")
                        .value(expectedResult.getResponse().getContent().get(0).getId()))
                .andExpect(jsonPath("$.response.content[0].name")
                        .value(expectedResult.getResponse().getContent().get(0).getName()))
                .andExpect(jsonPath("$.response.content[0].email")
                        .value(expectedResult.getResponse().getContent().get(0).getEmail()));

        // Verify
        verify(userService).getUsers(any(UserPagingRequest.class));

    }

}