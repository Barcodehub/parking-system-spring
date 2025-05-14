package com.nelumbo.parqueadero_api.exception;

public class DuplicateVehicleException extends RuntimeException {
    public DuplicateVehicleException(String message) {
        super(message);
    }
}
