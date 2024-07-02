package com.springboot.ratelimiter.user.controller;

import com.springboot.ratelimiter.common.model.dto.response.CustomPagingResponse;
import com.springboot.ratelimiter.common.model.dto.response.CustomResponse;
import com.springboot.ratelimiter.common.model.mapper.CustomPageToCustomPagingResponseMapper;
import com.springboot.ratelimiter.common.model.page.CustomPage;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.payload.request.CreateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UpdateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UserPagingRequest;
import com.springboot.ratelimiter.user.payload.response.UserResponse;
import com.springboot.ratelimiter.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller named {@link UserController} for managing user operations.
 * This controller provides endpoints to create, retrieve, update, and delete users,
 * as well as retrieve a paginated list of users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final CustomPageToCustomPagingResponseMapper customPageToCustomPagingResponseMapper =
            CustomPageToCustomPagingResponseMapper.initialize();

    /**
     * Endpoint to create a new user.
     *
     * @param createUserRequest The request body containing user details.
     * @return CustomResponse with the created user and HTTP status 201 (Created).
     */
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomResponse<User> saveUser(@RequestBody @Valid final CreateUserRequest createUserRequest) {
        final User user = userService.createUser(createUserRequest);
        return CustomResponse.created(user);
    }

    /**
     * Endpoint to retrieve a user by ID.
     *
     * @param id The unique identifier of the user.
     * @return CustomResponse with the retrieved user.
     */
    @GetMapping("/{id}")
    public CustomResponse<User> getUserById(@PathVariable @UUID final String id) {
        final User user = userService.getUserById(id);
        return CustomResponse.ok(user);
    }

    /**
     * Endpoint to update an existing user.
     *
     * @param id                 The unique identifier of the user to update.
     * @param updateUserRequest  The request body containing updated user details.
     * @return CustomResponse with the updated user.
     */
    @PutMapping("/{id}")
    public CustomResponse<User> updateUser(@PathVariable @UUID final String id,
                                           @RequestBody @Valid final UpdateUserRequest updateUserRequest) {
        final User user = userService.updateUser(id, updateUserRequest);
        return CustomResponse.ok(user);
    }

    /**
     * Endpoint to delete a user by ID.
     *
     * @param id The unique identifier of the user to delete.
     * @return CustomResponse indicating successful deletion.
     */
    @DeleteMapping("/{id}")
    public CustomResponse<String> deleteUser(@PathVariable @UUID final String id) {
        userService.deleteUserById(id);
        return CustomResponse.ok("User is deleted by ID: " + id);
    }

    /**
     * Endpoint to retrieve a paginated list of users.
     *
     * @param userPagingRequest The request body containing pagination parameters.
     * @return CustomResponse with a paginated response of users.
     */
    @GetMapping
    public CustomResponse<CustomPagingResponse<UserResponse>> getUsers(
            @RequestBody @Valid final UserPagingRequest userPagingRequest) {

        final CustomPage<User> userPage = userService.getUsers(userPagingRequest);

        final CustomPagingResponse<UserResponse> userPagingResponse =
                customPageToCustomPagingResponseMapper.toPagingResponse(userPage);

        return CustomResponse.ok(userPagingResponse);

    }

}
