package com.nelumbo.parqueadero_api.exception;


import com.nelumbo.parqueadero_api.dto.errors.ErrorDetailDTO;
import com.nelumbo.parqueadero_api.dto.errors.ErrorResponseDTO;
import com.nelumbo.parqueadero_api.dto.errors.RejectionDTO;
import com.nelumbo.parqueadero_api.dto.errors.ValidationDataDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        List<ErrorDetailDTO> errors = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> new ErrorDetailDTO(
//                        "400",
//                        error.getDefaultMessage(),
//                        error.getField(),
//                        null // traceId
//                ))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorResponseDTO(null, errors));
//    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Invalid JSON format: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "404",
                ex.getMessage(),
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "400",
                ex.getMessage(),
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(BusinessRuleException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetailDTO> errors = ex.getFieldErrors().stream()
                .map(fieldError -> new ErrorDetailDTO(
                        "400",
                        fieldError.getDefaultMessage(),
                        fieldError.getField(),
                        null // traceId
                ))
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO(null, errors));
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String code, String message, String field) {
        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(
                        null,
                        List.of(new ErrorDetailDTO(code, message, field, null))
                ));
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailExists(EmailAlreadyExistsException ex) {
        ErrorDetailDTO error = new ErrorDetailDTO(
                ex.getErrorCode(), // "USER_001"
                ex.getMessage(),
                ex.getFieldName(), // "email"
                null // traceId
        );

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(null, List.of(error)));
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
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationFailed(AuthenticationFailedException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "401",
                ex.getMessage(),
                null,  // field si aplica
                null   // traceId si lo generas
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalError(Exception ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "500",
                "Internal server error",
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
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
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        ErrorDetailDTO errorDetail = new ErrorDetailDTO(
                "403",
                ex.getMessage(),
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDTO(null, List.of(errorDetail)));
    }


}