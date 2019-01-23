package com.me.backendchallenge.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApplicationException {

    public NotFoundException(String description) {
        super(HttpStatus.NOT_FOUND.value(), "recurso_nao_encontrado", description);
    }

    public NotFoundException(String description, Throwable cause) {
        super(HttpStatus.NOT_FOUND.value(), "recurso_nao_encontrado", description, cause);
    }
}
