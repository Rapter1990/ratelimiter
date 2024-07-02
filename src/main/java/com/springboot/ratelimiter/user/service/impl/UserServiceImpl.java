package com.springboot.ratelimiter.user.service.impl;

import com.springboot.ratelimiter.common.exception.ratelimit.RateLimitExceededException;
import com.springboot.ratelimiter.common.exception.user.EmailAlreadyExistsException;
import com.springboot.ratelimiter.common.exception.user.UserNotFoundException;
import com.springboot.ratelimiter.common.model.page.CustomPage;
import com.springboot.ratelimiter.user.User;
import com.springboot.ratelimiter.user.mapper.CreateUserRequestToUserEntityMapper;
import com.springboot.ratelimiter.user.mapper.ListUserEntityToListUserMapper;
import com.springboot.ratelimiter.user.mapper.UpdateUserRequestToUserEntityMapper;
import com.springboot.ratelimiter.user.mapper.UserEntityToUserMapper;
import com.springboot.ratelimiter.user.model.UserEntity;
import com.springboot.ratelimiter.user.payload.request.CreateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UpdateUserRequest;
import com.springboot.ratelimiter.user.payload.request.UserPagingRequest;
import com.springboot.ratelimiter.user.repository.UserRepository;
import com.springboot.ratelimiter.user.service.RateLimiterService;
import com.springboot.ratelimiter.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class named {@link UserServiceImpl} implementing for managing user operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RateLimiterService rateLimiterService;

    private final UserEntityToUserMapper userEntityToUserMapper = UserEntityToUserMapper.initialize();

    private final CreateUserRequestToUserEntityMapper createUserRequestToUserEntity =
            CreateUserRequestToUserEntityMapper.initialize();

    private final ListUserEntityToListUserMapper listUserEntityToListUserMapper =
            ListUserEntityToListUserMapper.initialize();

    /**
     * Creates a new user based on the provided CreateUserRequest.
     *
     * @param createUserRequest the CreateUserRequest containing user details
     * @return the created User object
     */
    @Override
    @Transactional
    public User createUser(CreateUserRequest createUserRequest) {

        if (!rateLimiterService.isAllowed()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException(createUserRequest.getEmail());
        }

        final UserEntity userEntityToBeSaved = createUserRequestToUserEntity.map(createUserRequest);
        final UserEntity savedUserEntity = userRepository.save(userEntityToBeSaved);
        return userEntityToUserMapper.map(savedUserEntity);

    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return the User object if found, otherwise null
     */
    @Override
    public User getUserById(String id) {

        if (!rateLimiterService.isAllowed()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userEntityToUserMapper.map(userEntity);

    }

    /**
     * Updates an existing user identified by their unique identifier.
     *
     * @param id               the identifier of the user to update
     * @param updateUserRequest the UpdateUserRequest containing updated user details
     * @return the updated User object
     */
    @Override
    @Transactional
    public User updateUser(String id, UpdateUserRequest updateUserRequest) {

        if (!rateLimiterService.isAllowed()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException(updateUserRequest.getEmail());
        }

        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));


        UserEntity userEntityToBeUpdated = UpdateUserRequestToUserEntityMapper
                .mapForUpdate(userEntity, updateUserRequest);

        final UserEntity userEntityUpdated = userRepository.save(userEntityToBeUpdated);

        return userEntityToUserMapper.map(userEntityUpdated);

    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the identifier of the user to delete
     */
    @Override
    @Transactional
    public void deleteUserById(String id) {

        if (!rateLimiterService.isAllowed()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(userEntity);

    }

    /**
     * Retrieves a paginated list of users based on the provided UserPagingRequest.
     *
     * @param userPagingRequest the UserPagingRequest containing pagination parameters
     * @return a CustomPage containing the list of users
     */
    @Override
    public CustomPage<User> getUsers(UserPagingRequest userPagingRequest) {

        if (!rateLimiterService.isAllowed()) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        final Page<UserEntity> userEntityPage = userRepository.findAll(userPagingRequest.toPageable());

        if (userEntityPage.getContent().isEmpty()) {
            throw new UserNotFoundException("Couldn't find any User");
        }

        final List<User> userDomainModels = listUserEntityToListUserMapper.toUserList(userEntityPage.getContent());

        return CustomPage.of(userDomainModels, userEntityPage);

    }

}
