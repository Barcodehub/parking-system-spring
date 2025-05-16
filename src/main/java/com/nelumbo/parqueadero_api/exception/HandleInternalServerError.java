package com.nelumbo.parqueadero_api.exception;


public class HandleInternalServerError extends RuntimeException {
    public HandleInternalServerError(String message) {
        super(message);
    }
}