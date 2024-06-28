package com.springboot.ratelimiter.common.exception;

import com.springboot.ratelimiter.base.AbstractRestControllerTest;
import com.springboot.ratelimiter.common.exception.error.CustomError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
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
    void givenMethodArgumentNotValidException_handleMethodArgumentNotValid_throwCustomError() throws NoSuchMethodException {
        // Given
        MethodArgumentNotValidException mockException = getMethodArgumentNotValidException();

        // When
        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Validation failed")
                .subErrors(Collections.emptyList())
                .build();

        // Then
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(
                mockException, new HttpHeaders(), HttpStatus.BAD_REQUEST, null);

        CustomError actualError = (CustomError) responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    private MethodArgumentNotValidException getMethodArgumentNotValidException() throws NoSuchMethodException {
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("handleMethodArgumentNotValid");
        int parameterIndex = -1;

        MethodParameter mockParameter = new MethodParameter(method, parameterIndex);
        BindingResult mockBindingResult = new BeanPropertyBindingResult(null, "");
        return new MethodArgumentNotValidException(mockParameter, mockBindingResult);
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
                .type(mockViolation.getRootBeanClass().getSimpleName())
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

        when(mockPath.toString()).thenReturn("");
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockViolation.getInvalidValue()).thenReturn("invalid value");
        when(mockViolation.getRootBeanClass()).thenReturn(String.class);

        // Then
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handlePathVariableErrors(mockException);

        CustomError actualError = (CustomError) responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenRuntimeException_whenHandleRuntimeException_throwCustomError() {

        // Given
        RuntimeException mockException = new RuntimeException("Runtime exception");

        // When
        CustomError expectedError = CustomError.builder()
                .time(LocalDateTime.now())
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.API_ERROR.getName())
                .message("Runtime exception")
                .build();

        // Then
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleRuntimeException(mockException);

        CustomError actualError = (CustomError) responseEntity.getBody();
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