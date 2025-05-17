package com.nelumbo.parqueadero_api.exception;


public class EmailAlreadyExistsException extends RuntimeException {
    private final String errorCode;
    private final String fieldName;

    public EmailAlreadyExistsException(String errorCode, String message, String fieldName) {
        super(message);
        this.errorCode = errorCode;
        this.fieldName = fieldName;
    }
    public String getErrorCode() { return errorCode; }
    public String getFieldName() { return fieldName; }

}