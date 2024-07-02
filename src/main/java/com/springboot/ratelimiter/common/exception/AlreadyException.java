package com.springboot.ratelimiter.common.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * An abstract exception class named {@link AlreadyException} representing the scenario where an entity already exists.
 */
public abstract class AlreadyException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6091550422939917669L;

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    protected AlreadyException(String message) {
        super(message);
    }

}
