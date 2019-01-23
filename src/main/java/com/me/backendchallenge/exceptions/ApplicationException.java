package com.me.backendchallenge.exceptions;

import com.me.backendchallenge.exceptions.serializer.ExceptionSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ExceptionSerializer.class)
public class ApplicationException extends RuntimeException {

    private Integer httpCode;
    private String errorCode;

    public ApplicationException(Integer httpCode, String errorCode, String description) {
        super(description);

        this.httpCode = httpCode;
        this.errorCode = errorCode;
    }

    public ApplicationException(Integer httpCode, String errorCode, String description, Throwable cause) {
        super(description, cause);

        this.httpCode = httpCode;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Integer getHttpCode() {
        return httpCode;
    }
}
