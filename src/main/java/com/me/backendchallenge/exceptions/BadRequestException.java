package com.me.backendchallenge.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String description) {
        super(HttpStatus.BAD_REQUEST.value(), "entrada_invalida", description);
    }

    public BadRequestException(String description, Throwable cause) {
        super(HttpStatus.BAD_REQUEST.value(), "entrada_invalida", description, cause);
    }
}
