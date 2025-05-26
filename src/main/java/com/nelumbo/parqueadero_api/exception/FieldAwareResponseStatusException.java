package com.nelumbo.parqueadero_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FieldAwareResponseStatusException extends ResponseStatusException {
    private final String field;

    public FieldAwareResponseStatusException(HttpStatus status, String reason, String field) {
        super(status, reason);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}