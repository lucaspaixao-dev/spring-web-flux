package com.me.backendchallenge.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApplicationException {

    public InternalServerErrorException(String description) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ops... Ocorreu um erro interno.", description);
    }

    public InternalServerErrorException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ops... Ocorreu um erro interno.", null);
    }

}
