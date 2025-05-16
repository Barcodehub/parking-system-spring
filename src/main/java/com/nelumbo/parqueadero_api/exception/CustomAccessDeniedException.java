package com.nelumbo.parqueadero_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.AccessDeniedException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CustomAccessDeniedException extends AccessDeniedException {
public CustomAccessDeniedException(String message) {
    super(message);
}
        }
