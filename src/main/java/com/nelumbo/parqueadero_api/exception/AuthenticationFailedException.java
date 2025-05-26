package com.nelumbo.parqueadero_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedException extends RuntimeException {
    private final String field;

    public AuthenticationFailedException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}