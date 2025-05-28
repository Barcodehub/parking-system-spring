package com.nelumbo.parqueadero_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedActionException extends RuntimeException {
    private final String field;
    public UnauthorizedActionException(String message, String field) {
        super(message);
        this.field = field;
    }
}
