package com.springboot.ratelimiter.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public abstract class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4846183318086338894L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    protected NotFoundException(String message) {
        super(message);
    }

}