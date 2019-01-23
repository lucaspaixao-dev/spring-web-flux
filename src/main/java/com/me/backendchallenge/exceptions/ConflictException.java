package com.me.backendchallenge.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApplicationException {

    public ConflictException(String description) {
        super(HttpStatus.CONFLICT.value(), "recurso_existente", description);
    }

    public ConflictException(String description, Throwable cause) {
        super(HttpStatus.CONFLICT.value(), "recurso_existente", description, cause);
    }
}
