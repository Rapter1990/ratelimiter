package com.springboot.ratelimiter.common.exception;

import com.springboot.ratelimiter.base.AbstractRestControllerTest;
import com.springboot.ratelimiter.common.exception.error.CustomError;
import com.springboot.ratelimiter.common.exception.ratelimit.RateLimitExceededException;
import com.springboot.ratelimiter.common.exception.user.EmailAlreadyExistsException;
import com.springboot.ratelimiter.common.exception.user.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest extends AbstractRestControllerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void givenMethodArgumentNotValidException_handleMethodArgumentNotValid_throwCustomError() {

        // Given
        MethodParameter mockParameter = mock(MethodParameter.class);
        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Validation failed")
                .subErrors(Collections.emptyList())
                .build();

        // When
        MethodArgumentNotValidException mockException = new MethodArgumentNotValidException(mockParameter, mockBindingResult);

        // Then
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(
                mockException, new HttpHeaders(), HttpStatus.BAD_REQUEST, null);

        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }


    @Test
    void givenConstraintViolationException_whenHandlePathVariableErrors_throwCustomError() {

        // Given
        ConstraintViolation<String> mockViolation = mock(ConstraintViolation.class);
        Path mockPath = mock(Path.class);
        Set<ConstraintViolation<?>> violations = Set.of(mockViolation);
        ConstraintViolationException mockException = new ConstraintViolationException(violations);

        CustomError.CustomSubError subError = CustomError.CustomSubError.builder()
                .message("must not be null")
                .field("")
                .value("invalid value")
                .type("String") // Default to String if getRootBeanClass() is null
                .build();

        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Constraint violation")
                .subErrors(Collections.singletonList(subError))
                .build();

        // When
        when(mockViolation.getMessage()).thenReturn("must not be null");
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn("field");
        when(mockViolation.getInvalidValue()).thenReturn("invalid value");
        when(mockViolation.getRootBeanClass()).thenReturn(String.class); // Ensure this does not return null

        // Then
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handlePathVariableErrors(mockException);

        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenRuntimeException_whenHandleRuntimeException_throwCustomError() {

        // Given
        RuntimeException mockException = new RuntimeException("Runtime exception");

        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.API_ERROR.getName())
                .message("Runtime exception")
                .build();

        // When
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleRuntimeException(mockException);

        // Then
        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenRateLimitExceededException_whenHandleRateLimitExceededException_throwCustomError() {

        // Given
        RateLimitExceededException mockException = new RateLimitExceededException("Too many requests");

        // When
        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.TOO_MANY_REQUESTS)
                .header(CustomError.Header.RATE_LIMITER_EXCEEDED_ERROR.getName())
                .message("Too many requests")
                .build();

        // When
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleRateLimitExceededException(mockException);

        // Then
        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenEmailAlreadyExistsException_whenHandleEmailAlreadyExistsException_throwCustomError() {

        // Given
        EmailAlreadyExistsException mockException = new EmailAlreadyExistsException("test@example.com");

        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Email already exists: test@example.com")
                .build();

        // When
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleEmailAlreadyExistsException(mockException);

        // Then
        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenUserNotFoundException_whenHandleUserNotFoundException_throwCustomError() {

        // Given
        UserNotFoundException mockException = new UserNotFoundException("123");

        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message("No user was found with ID: 123")
                .build();

        // When
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleUserNotFoundException(mockException);

        // Then
        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }

    private void checkCustomError(CustomError expectedError, CustomError actualError) {

        assertThat(actualError).isNotNull();
        assertThat(actualError.getTime()).isNotNull();
        assertThat(actualError.getHeader()).isEqualTo(expectedError.getHeader());
        assertThat(actualError.getIsSuccess()).isEqualTo(expectedError.getIsSuccess());

        if (expectedError.getMessage() != null) {
            assertThat(actualError.getMessage()).isEqualTo(expectedError.getMessage());
        }

        if (expectedError.getSubErrors() != null) {
            assertThat(actualError.getSubErrors().size()).isEqualTo(expectedError.getSubErrors().size());
            if (!expectedError.getSubErrors().isEmpty()) {
                assertThat(actualError.getSubErrors().get(0).getMessage()).isEqualTo(expectedError.getSubErrors().get(0).getMessage());
                assertThat(actualError.getSubErrors().get(0).getField()).isEqualTo(expectedError.getSubErrors().get(0).getField());
                assertThat(actualError.getSubErrors().get(0).getValue()).isEqualTo(expectedError.getSubErrors().get(0).getValue());
                assertThat(actualError.getSubErrors().get(0).getType()).isEqualTo(expectedError.getSubErrors().get(0).getType());
            }
        }
    }

}