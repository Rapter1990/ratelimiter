package com.springboot.ratelimiter.user.service.impl;

import com.springboot.ratelimiter.base.AbstractBaseServiceTest;
import com.springboot.ratelimiter.common.exception.ratelimit.RateLimitExceededException;
import com.springboot.ratelimiter.common.exception.user.EmailAlreadyExistsException;
import com.springboot.ratelimiter.common.exception.user.UserNotFoundException;
import com.springboot.ratelimiter.common.model.page.CustomPage;
import com.springboot.ratelimiter.common.model.page.CustomPaging;
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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}, covering various scenarios of user management operations.
 */
class UserServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RateLimiterService rateLimiterService;

    private final UserEntityToUserMapper userEntityToUserMapper = UserEntityToUserMapper.initialize();

    private final CreateUserRequestToUserEntityMapper createUserRequestToUserEntity =
            CreateUserRequestToUserEntityMapper.initialize();

    private final ListUserEntityToListUserMapper listUserEntityToListUserMapper =
            ListUserEntityToListUserMapper.initialize();

    /**
     * Test case for {@link UserServiceImpl#createUser(CreateUserRequest)} when rate limit is exceeded.
     * Verifies that {@link RateLimitExceededException} is thrown when the rate limit for user creation is exceeded.
     */
    @Test
    void givenCreateRequest_whenCreateUserRateLimitExceeded_thenThrowRateLimitExceededException() {

        // Given
        final CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User 1")
                .build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(false);

        // Then
        final RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> {
            userService.createUser(createUserRequest);
        });

        assertEquals("Rate limit exceeded", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#createUser(CreateUserRequest)} when email already exists.
     * Verifies that {@link EmailAlreadyExistsException} is thrown when trying to create a user with an existing email.
     */
    @Test
    void givenCreateRequest_whenCreateUserEmailAlreadyExists_thenThrowEmailAlreadyExistsException() {

        // Given
        final CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User 1")
                .build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(true);

        // Then
        final EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(createUserRequest);
        });

        assertEquals("Email already exists: user@userinfo.com", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).existsByEmail(createUserRequest.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#createUser(CreateUserRequest)} with a valid create request.
     * Verifies successful creation of a user and mapping of entities to domain objects.
     */
    @Test
    void givenCreateRequest_whenCreateUserValidRequest_thenReturnUser() {

        // Given
        final CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User 1")
                .build();

        final UserEntity userEntity = createUserRequestToUserEntity.map(createUserRequest);

        final User user = userEntityToUserMapper.map(userEntity);

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.existsByEmail(createUserRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Then
        final User result = userService.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).existsByEmail(createUserRequest.getEmail());
        verify(userRepository, times(1)).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#getUserById(String)} when rate limit is exceeded.
     * Verifies that {@link RateLimitExceededException} is thrown when the rate limit for fetching user by ID is exceeded.
     */
    @Test
    void givenUserId_whenGetUserByIdRateLimitExceeded_thenThrowRateLimitExceededException() {

        // Given
        final String userId = "123";

        // When
        when(rateLimiterService.isAllowed()).thenReturn(false);

        // Then
        final RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("Rate limit exceeded", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, never()).findById(anyString());

    }

    /**
     * Test case for {@link UserServiceImpl#getUserById(String)} when user is not found.
     * Verifies that {@link UserNotFoundException} is thrown when trying to fetch a user that does not exist.
     */
    @Test
    void givenUserId_whenUserNotFound_thenThrowUserNotFoundException() {

        // Given
        final String userId = "123";

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Then
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("No user was found with ID: 123", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).findById(userId);

    }

    /**
     * Test case for {@link UserServiceImpl#getUserById(String)} when user is found.
     * Verifies successful retrieval of a user by ID and mapping of entities to domain objects.
     */
    @Test
    void givenUserId_whenUserFound_thenReturnUser() {

        // Given
        final String userId = "123";

        final UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .email("user@test.com")
                .name("User 1")
                .build();

        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Then
        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getName(), result.getName());
        assertEquals(userEntity.getEmail(), result.getEmail());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).findById(userId);

    }

    /**
     * Test case for {@link UserServiceImpl#updateUser(String, UpdateUserRequest)} when rate limit is exceeded.
     * Verifies that {@link RateLimitExceededException} is thrown when the rate limit for updating a user is exceeded.
     */
    @Test
    void givenUpdateUserRequest_whenUpdateUserRateLimitExceeded_thenThrowRateLimitExceededException() {

        // Given
        final String userId = "123";
        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User Updated")
                .build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(false);

        // Then
        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> {
            userService.updateUser(userId, updateUserRequest);
        });

        assertEquals("Rate limit exceeded", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).findById(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#updateUser(String, UpdateUserRequest)} when email already exists.
     * Verifies that {@link EmailAlreadyExistsException} is thrown when trying to update a user with an existing email.
     */
    @Test
    void givenUpdateUserRequest_whenUpdateUserEmailAlreadyExists_thenThrowEmailAlreadyExistsException() {

        // Given
        final String userId = "123";
        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User Updated")
                .build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(true);

        // Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(userId, updateUserRequest);
        });

        assertEquals("Email already exists: user@userinfo.com", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).existsByEmail(updateUserRequest.getEmail());
        verify(userRepository, never()).findById(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#updateUser(String, UpdateUserRequest)} when user is not found.
     * Verifies that {@link UserNotFoundException} is thrown when trying to update a user that does not exist.
     */
    @Test
    void givenUpdateUserRequest_whenUserNotFound_thenThrowUserNotFoundException() {

        // Given
        final String userId = "123";
        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User Updated")
                .build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updateUserRequest);
        });

        assertEquals("No user was found with ID: 123", exception.getMessage());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).existsByEmail(updateUserRequest.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#updateUser(String, UpdateUserRequest)} with a valid update request.
     * Verifies successful update of a user and mapping of entities to domain objects.
     */
    @Test
    void givenUpdateUserRequest_whenUpdateUserWithValidRequest_thenReturnUpdatedUser() {

        // Given
        final String userId = "123";
        final UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email("user@userinfo.com")
                .name("User Updated")
                .build();

        final UserEntity existingUserEntity = UserEntity.builder()
                .id(userId)
                .name("User Original")
                .email("user@original.com")
                .build();

        final UserEntity userEntityToBeUpdated = UpdateUserRequestToUserEntityMapper
                .mapForUpdate(existingUserEntity, updateUserRequest);

        final UserEntity updatedUserEntity = UserEntity.builder()
                .id(userEntityToBeUpdated.getId())
                .name(userEntityToBeUpdated.getName())
                .email(userEntityToBeUpdated.getEmail())
                .build();

        final User updatedUser = userEntityToUserMapper.map(userEntityToBeUpdated);

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUserEntity);

        // Then
        User result = userService.updateUser(userId, updateUserRequest);

        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());

        // Verify
        verify(rateLimiterService, times(1)).isAllowed();
        verify(userRepository, times(1)).existsByEmail(updateUserRequest.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));

    }

    /**
     * Test case for {@link UserServiceImpl#deleteUserById(String)} with a valid user ID.
     * Verifies successful deletion of a user by ID.
     */
    @Test
    void givenValidUserId_whenDeleteUser_thenReturnSuccess() {

        // Given
        final String userId = "123";
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(userEntity));

        // Then
        userService.deleteUserById(userId);

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(userEntity);

    }

    /**
     * Test case for {@link UserServiceImpl#deleteUserById(String)} when user ID is invalid (user not found).
     * Verifies that {@link UserNotFoundException} is thrown when trying to delete a user that does not exist.
     */
    @Test
    void givenInvalidUserId_whenDeleteUser_thenThrowUserNotFoundException() {

        // Given
        final String userId = "456";

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Then
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(userId)
        );

        assertEquals("No user was found with ID: 456", userNotFoundException.getMessage());

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any());

    }

    /**
     * Test case for {@link UserServiceImpl#deleteUserById(String)} when rate limit is exceeded.
     * Verifies that {@link RateLimitExceededException} is thrown when the rate limit for deleting a user is exceeded.
     */
    @Test
    void givenRateLimitExceeded_whenDeleteUser_thenThrowRateLimitExceededException() {

        // Given
        final String userId = "789";

        // When
        when(rateLimiterService.isAllowed()).thenReturn(false);

        // Then
        RateLimitExceededException rateLimitExceededException = assertThrows(RateLimitExceededException.class, () -> userService.deleteUserById(userId));

        assertEquals("Rate limit exceeded", rateLimitExceededException.getMessage());

        // Verify
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).delete(any());

    }

    /**
     * Test case for {@link UserServiceImpl#getUsers(UserPagingRequest)} with a valid paging request.
     * Verifies successful retrieval of a paginated list of users.
     */
    @Test
    void givenUserPagingRequest_whenUserPageList_thenReturnCustomUserList() {

        // Given
        UserPagingRequest pagingRequest = UserPagingRequest.builder()
                .pagination(
                        CustomPaging.builder()
                                .pageSize(1)
                                .pageNumber(1)
                                .build()
                ).build();

        Page<UserEntity> userEntityPage = new PageImpl<>(Collections.singletonList(new UserEntity()));

        List<User> products = listUserEntityToListUserMapper.toUserList(userEntityPage.getContent());

        CustomPage<User> expected = CustomPage.of(products, userEntityPage);

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userEntityPage);

        // Then
        CustomPage<User> result = userService.getUsers(pagingRequest);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(expected.getPageNumber(), result.getPageNumber());
        assertEquals(expected.getContent().get(0).getId(), result.getContent().get(0).getId());
        assertEquals(expected.getTotalPageCount(), result.getTotalPageCount());
        assertEquals(expected.getTotalElementCount(), result.getTotalElementCount());

        // Verify
        verify(userRepository, times(1)).findAll(any(Pageable.class));

    }

    /**
     * Test case for {@link UserServiceImpl#getUsers(UserPagingRequest)} when no users are found for the paging request.
     * Verifies that {@link UserNotFoundException} is thrown when no users are found based on the paging criteria.
     */
    @Test
    void givenUserPagingRequest_whenNoUserPageList_thenThrowUserNotFoundException() {

        // Given
        UserPagingRequest pagingRequest = UserPagingRequest.builder()
                .pagination(
                        CustomPaging.builder()
                                .pageSize(1)
                                .pageNumber(1)
                                .build()
                ).build();

        Page<UserEntity> userEntityPage = new PageImpl<>(Collections.emptyList());

        // When
        when(rateLimiterService.isAllowed()).thenReturn(true);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userEntityPage);

        // Then
        assertThrows(UserNotFoundException.class, () -> userService.getUsers(pagingRequest));

        // Verify
        verify(userRepository, times(1)).findAll(any(Pageable.class));

    }

    /**
     * Test case for {@link UserServiceImpl#getUsers(UserPagingRequest)} when rate limit is exceeded.
     * Verifies that {@link RateLimitExceededException} is thrown when the rate limit for fetching users is exceeded.
     */
    @Test
    void givenUserPagingRequest_WhenNoUserPageList_thenThrowRateLimitExceededException() {

        // Given
        UserPagingRequest pagingRequest = UserPagingRequest.builder()
                .pagination(
                        CustomPaging.builder()
                                .pageSize(1)
                                .pageNumber(1)
                                .build()
                ).build();

        // When
        when(rateLimiterService.isAllowed()).thenReturn(false);

        // Then
        RateLimitExceededException rateLimitExceededException = assertThrows(RateLimitExceededException.class, () -> {
            userService.getUsers(pagingRequest);
        });

        assertEquals("Rate limit exceeded", rateLimitExceededException.getMessage());

        // Verify
        verify(userRepository, never()).findAll(any(Pageable.class));

    }

}
