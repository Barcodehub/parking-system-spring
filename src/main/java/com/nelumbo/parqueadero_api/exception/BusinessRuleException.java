package com.nelumbo.parqueadero_api.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

}