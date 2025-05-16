package com.nelumbo.parqueadero_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Invalid JSON format: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateVehicleException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateVehicle(DuplicateVehicleException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(AuthenticationFailedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleInternalServerError(HandleInternalServerError ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleCustomAccessDenied(CustomAccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "403");
        response.put("error", "Forbidden");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "403");
        response.put("error", "Forbidden");
        response.put("message", "Acceso denegado: No tienes los permisos necesarios");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }


}