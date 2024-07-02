package com.springboot.ratelimiter.common.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a custom error as {@link CustomError} with details such as timestamp, HTTP status, header, message, and sub-errors.
 */
@Getter
@Builder
public class CustomError {

    @Builder.Default
    private LocalDateTime time = LocalDateTime.now();

    private HttpStatus httpStatus;

    private String header;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @Builder.Default
    private final Boolean isSuccess = false;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CustomSubError> subErrors;

    /**
     * Represents a sub-error as {@link CustomSubError} with details such as message, field, value, and type.
     */
    @Getter
    @Builder
    public static class CustomSubError {

        private String message;

        private String field;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Object value;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String type;

    }

    /**
     * Represents the headers as {@link Header} used for categorizing errors.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Header {

        API_ERROR("API ERROR"),

        ALREADY_EXIST("ALREADY EXIST"),

        NOT_FOUND("NOT EXIST"),

        VALIDATION_ERROR("VALIDATION ERROR"),

        DATABASE_ERROR("DATABASE ERROR"),

        PROCESS_ERROR("PROCESS ERROR"),

        RATE_LIMITER_EXCEEDED_ERROR("RATE LIMITER EXCEEDED ERROR");

        private final String name;

    }

}
