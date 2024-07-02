package com.springboot.ratelimiter.user.service;

import com.springboot.ratelimiter.common.model.page.CustomPage;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.payload.request.CreateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UpdateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UserPagingRequest;

/**
 * Service interface named {@link UserService} for managing user operations.
 */
public interface UserService {

    /**
     * Creates a new user based on the provided CreateUserRequest.
     *
     * @param createUserRequest the CreateUserRequest containing user details
     * @return the created User object
     */
    User createUser(CreateUserRequest createUserRequest);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return the User object if found, otherwise null
     */
    User getUserById(String id);

    /**
     * Updates an existing user identified by their unique identifier.
     *
     * @param id               the identifier of the user to update
     * @param updateUserRequest the UpdateUserRequest containing updated user details
     * @return the updated User object
     */
    User updateUser(String id, UpdateUserRequest updateUserRequest);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the identifier of the user to delete
     */
    void deleteUserById(String id);

    /**
     * Retrieves a paginated list of users based on the provided UserPagingRequest.
     *
     * @param userPagingRequest the UserPagingRequest containing pagination parameters
     * @return a CustomPage containing the list of users
     */
    CustomPage<User> getUsers(UserPagingRequest userPagingRequest);

}
