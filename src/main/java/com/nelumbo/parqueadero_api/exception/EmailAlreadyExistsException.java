package com.nelumbo.parqueadero_api.exception;


public class EmailAlreadyExistsException extends RuntimeException {
    private final String fieldName;

    public EmailAlreadyExistsException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
    public String getFieldName() { return fieldName; }

}