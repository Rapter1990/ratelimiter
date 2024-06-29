package com.springboot.ratelimiter.common.exception.user;

import com.springboot.ratelimiter.common.exception.AlreadyException;

import java.io.Serial;

public class EmailAlreadyExistsException extends AlreadyException {

    @Serial
    private static final long serialVersionUID = -6856741036475854853L;

    private static final String DEFAULT_MESSAGE =
            "The specified email already exists";

    private static final String MESSAGE_TEMPLATE =
            "Email already exists: ";

    public EmailAlreadyExistsException(String email) {
        super(MESSAGE_TEMPLATE.concat(email));
    }

    public EmailAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

}
