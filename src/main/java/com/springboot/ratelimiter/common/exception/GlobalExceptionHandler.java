package com.springboot.ratelimiter.common.exception;

import com.springboot.ratelimiter.common.exception.error.CustomError;
import com.springboot.ratelimiter.common.exception.ratelimit.RateLimitExceededException;
import com.springboot.ratelimiter.common.exception.user.EmailAlreadyExistsException;
import com.springboot.ratelimiter.common.exception.user.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler class named {@link GlobalExceptionHandler} for handling various exceptions across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles MethodArgumentNotValidException and returns a custom error response with validation details.
     *
     * @param ex the exception thrown when method arguments are not valid
     * @param headers the HTTP headers
     * @param status the HTTP status code
     * @param request the web request
     * @return a ResponseEntity containing the custom error response
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(
                error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(fieldName, message);
                }
        );

        CustomError customError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Validation failed")
                .subErrors(errors.entrySet().stream()
                        .map(e -> CustomError.CustomSubError.builder()
                                .field(e.getKey())
                                .message(e.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConstraintViolationException and returns a custom error response with constraint violation details.
     *
     * @param constraintViolationException the exception thrown when constraint violations occur
     * @return a ResponseEntity containing the custom error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handlePathVariableErrors(final ConstraintViolationException constraintViolationException) {

        final List<CustomError.CustomSubError> subErrors = new ArrayList<>();
        constraintViolationException.getConstraintViolations()
                .forEach(constraintViolation ->
                        subErrors.add(
                                CustomError.CustomSubError.builder()
                                        .message(constraintViolation.getMessage())
                                        .field(StringUtils.substringAfterLast(constraintViolation.getPropertyPath().toString(), "."))
                                        .value(constraintViolation.getInvalidValue() != null ? constraintViolation.getInvalidValue().toString() : null)
                                        .type(constraintViolation.getInvalidValue().getClass().getSimpleName())
                                        .build()
                        )
                );

        CustomError customError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Constraint violation")
                .subErrors(subErrors)
                .build();

        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handles RuntimeException and returns a custom error response.
     *
     * @param runtimeException the runtime exception thrown
     * @return a ResponseEntity containing the custom error response
     */
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleRuntimeException(final RuntimeException runtimeException) {

        CustomError customError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.API_ERROR.getName())
                .message(runtimeException.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.NOT_FOUND);

    }

    /**
     * Handles RateLimitExceededException and returns a custom error response.
     *
     * @param ex the exception thrown when rate limit is exceeded
     * @return a ResponseEntity containing the custom error response
     */
    @ExceptionHandler(RateLimitExceededException.class)
    protected ResponseEntity<Object> handleRateLimitExceededException(final RateLimitExceededException ex) {

        CustomError customError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.TOO_MANY_REQUESTS)
                .header(CustomError.Header.RATE_LIMITER_EXCEEDED_ERROR.getName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.TOO_MANY_REQUESTS);
    }

    /**
     * Handles EmailAlreadyExistsException and returns a custom error response.
     *
     * @param ex the exception thrown when an email already exists
     * @return a ResponseEntity containing the custom error response
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEmailAlreadyExistsException(final EmailAlreadyExistsException ex) {

        CustomError customError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.CONFLICT);
    }

    /**
     * Handles UserNotFoundException and returns a custom error response.
     *
     * @param ex the exception thrown when a user is not found
     * @return a ResponseEntity containing the custom error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(final UserNotFoundException ex) {

        CustomError customError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.NOT_FOUND);
    }

}
